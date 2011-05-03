package Jewel.Petri.SysObjects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.SysObjects.FileXfer;
import Jewel.Petri.Interfaces.IProcess;
import Jewel.Petri.Interfaces.IStep;
import Jewel.Petri.Objects.PNLog;
import Jewel.Petri.Objects.PNProcess;

public abstract class Operation
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static Operation getOperation(FileXfer pobjFile)
		throws JewelPetriException
	{
        ByteArrayInputStream lstream;
        ObjectInputStream lreader;
        Operation lobjResult;

        try
        {
            lstream = new ByteArrayInputStream(pobjFile.getData());
			lreader = new ObjectInputStream(lstream);
	        lobjResult = (Operation)lreader.readObject();
	        lreader.close();
		}
        catch (Throwable e)
        {
			throw new JewelPetriException(e.getMessage(), e);
		}

		return lobjResult;
	}

	private transient UUID midProcess;
	private transient IProcess mrefProcess;
	private transient IStep mrefStep;
	private transient boolean mbDone;

	public Operation(UUID pidProcess)
	{
		midProcess = pidProcess;
		mbDone = false;
	}

	public abstract String ShortDesc();
	public abstract String LongDesc(String pstrLineBreak);
	public abstract String UndoDesc(String pstrLineBreak);

	protected abstract UUID OpID();
	protected abstract void Run(SQLServer pdb) throws JewelPetriException;

	public synchronized final void Execute()
		throws JewelPetriException
	{
		MasterDB ldb;

		if ( mbDone )
			throw new JewelPetriException("Error: Attempt to run operation twice.");

		mrefProcess = PNProcess.GetInstance(Engine.getCurrentNameSpace(), midProcess);

		LockProcess();

		try
		{
			ldb = new MasterDB();
		}
		catch (Throwable e)
		{
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			ldb.BeginTrans();
		}
		catch (Throwable e)
		{
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			CheckRunnable();
			Run(ldb);
			BuildLog(ldb);
		}
		catch (JewelPetriException e)
		{
			try { ldb.Rollback(); } catch (Throwable e1) {}
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			mrefProcess.Unlock();
			throw e;
		}
		catch (Throwable e)
		{
			try { ldb.Rollback(); } catch (Throwable e1) {}
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			mrefStep.DoSafeRun(ldb);
			mrefProcess.RecalcSteps(ldb);
		}
		catch (JewelPetriException e)
		{
			try { ldb.Rollback(); } catch (Throwable e1) {}
			mrefStep.RollbackSafeRun();
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			mrefProcess.Unlock();
			throw e;
		}
		catch (Throwable e)
		{
			try { ldb.Rollback(); } catch (Throwable e1) {}
			mrefStep.RollbackSafeRun();
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			ldb.Commit();
		}
		catch (Throwable e)
		{
			mrefStep.RollbackSafeRun();
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		mrefStep.CommitSafeRun();

		try
		{
			ldb.Disconnect();
		}
		catch (Throwable e)
		{
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		mrefProcess.Unlock();

		mbDone = true;
	}

	private void LockProcess()
		throws JewelPetriException
	{
		int i;

		i = 0;
		while ( !mrefProcess.Lock() )
		{
			i++;
			if ( i>10000 )
				throw new JewelPetriException("Erro: Processo bloqueado por outro utilizador.");
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	private void CheckRunnable()
		throws JewelPetriException
	{
		MasterDB ldb;

		mrefStep = mrefProcess.GetOperation(OpID());

		if ( mrefStep == null )
			throw new JewelPetriException("Error: Operation not currently available in this process.");

		if ( !mrefStep.IsRunnable() )
		{
			try
			{
				ldb = new MasterDB();
			}
			catch (Throwable e)
			{
				throw new JewelPetriException(e.getMessage(), e);
			}

			try
			{
				mrefProcess.RemoveStep(ldb, mrefStep);
			}
			catch (JewelPetriException e)
			{
				try { ldb.Disconnect(); } catch (Throwable e1) {}
				throw e;
			}

			try
			{
				ldb.Disconnect();
			}
			catch (Throwable e)
			{
				throw new JewelPetriException(e.getMessage(), e);
			}
			throw new JewelPetriException("Error: Operation not currently available in this process.");
		}
	}

	private void BuildLog(SQLServer pdb)
		throws JewelPetriException
	{
		PNLog lobjLog;
		ByteArrayOutputStream lstream;
		ObjectOutputStream lstreamObj;
		FileXfer lobjFile;

		lobjLog = PNLog.GetInstance(Engine.getCurrentNameSpace(), (UUID)null);

		try
		{
			lstream = new ByteArrayOutputStream();
			lstreamObj = new ObjectOutputStream(lstream);
			lstreamObj.writeObject(this);
			lstreamObj.close();
			lstream.close();
			lobjFile = new FileXfer((int)lstream.size(), "application/octet-stream", "log",
					new ByteArrayInputStream(lstream.toByteArray()));

			lobjLog.setAt(0, midProcess);
			lobjLog.setAt(1, OpID());
			lobjLog.setAt(2, new Timestamp(new java.util.Date().getTime()));
			lobjLog.setAt(3, Engine.getCurrentUser());
			lobjLog.setAt(4, (UUID)null);
			lobjLog.setAt(5, false);
			lobjLog.setAt(6, lobjFile);
			lobjLog.SaveToDb(pdb);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}
	}
}
