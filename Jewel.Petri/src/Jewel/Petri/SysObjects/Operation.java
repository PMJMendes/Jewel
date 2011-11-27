package Jewel.Petri.SysObjects;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.SysObjects.FileXfer;
import Jewel.Petri.Constants;
import Jewel.Petri.Interfaces.ILog;
import Jewel.Petri.Interfaces.IOperation;
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

	private static class QueuedOp
	{
		public Operation mobjQueued;
		public Operation mobjSource;
	}
	
	public static class QueueContext
		extends LinkedList<QueuedOp>
	{
		private static final long serialVersionUID = 1L;
	}

	private transient UUID midProcess;
	private transient IProcess mrefProcess;
	private transient IStep mrefStep;
	private transient boolean mbDone;
	private transient ILog mobjLog;
	protected transient QueueContext marrTriggers;

	public Operation(UUID pidProcess)
	{
		midProcess = pidProcess;
		mbDone = false;
		mobjLog = null;
	}

	protected abstract UUID OpID();
	public abstract String ShortDesc();
	public abstract String LongDesc(String pstrLineBreak);
	public abstract UUID GetExternalProcess();
	protected abstract void Run(SQLServer pdb) throws JewelPetriException;

	public void Enqueue(QueueContext parrTriggers)
	{
		QueuedOp lobjQueue;

		lobjQueue = new QueuedOp();
		lobjQueue.mobjQueued = this;
		lobjQueue.mobjSource = null;
		parrTriggers.add(lobjQueue);
	}

	public void Execute()
		throws JewelPetriException
	{
		Execute(null, new QueueContext());
	}

	public void Execute(UUID pidSourceLog)
		throws JewelPetriException
	{
		Execute(pidSourceLog, new QueueContext());
	}

	public void Execute(SQLServer pdb)
		throws JewelPetriException
	{
		try
		{
			Execute(null, new QueueContext(), pdb);
		}
		catch (NotRunnableException e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}
	}

	private void Execute(UUID pidSourceLog, QueueContext parrTriggers)
		throws JewelPetriException
	{
		MasterDB ldb;
		
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
			ldb.BeginTrans();
		}
		catch (Throwable e)
		{
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			Execute(pidSourceLog, parrTriggers, ldb);
		}
		catch (JewelPetriException e)
		{
			try { ldb.Rollback(); } catch (Throwable e1) {}
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			throw e;
		}
		catch (Throwable e)
		{
			try { ldb.Rollback(); } catch (Throwable e1) {}
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			ldb.Commit();
		}
		catch (Throwable e)
		{
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			ldb.Disconnect();
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}
	}

	private synchronized final void Execute(UUID pidSourceLog, QueueContext parrTriggers, SQLServer pdb)
		throws JewelPetriException, NotRunnableException
	{
		if ( mbDone )
			throw new JewelPetriException("Error: Attempt to run operation twice.");

		if ( (!GetProcess().IsRunning()) && (!(this instanceof UndoOperation)) )
			throw new JewelPetriException("Error: Attempt to run operation on stopped process.");

		LockProcess();

		try
		{
			CheckRunnable(pdb);
		}
		catch (JewelPetriException e)
		{
			mrefProcess.Unlock();
			throw e;
		}
		catch (NotRunnableException e)
		{
			mrefProcess.Unlock();
			throw e;
		}
		catch (Throwable e)
		{
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		marrTriggers = parrTriggers;

		try
		{
			Run(pdb);
		}
		catch (JewelPetriException e)
		{
			marrTriggers = null;
			mrefProcess.Unlock();
			throw e;
		}
		catch (Throwable e)
		{
			marrTriggers = null;
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		marrTriggers = null;

		try
		{
			BuildLog(pdb, pidSourceLog);
		}
		catch (JewelPetriException e)
		{
			mrefProcess.Unlock();
			throw e;
		}
		catch (Throwable e)
		{
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			mrefStep.DoSafeRun();
		}
		catch (JewelPetriException e)
		{
			mrefProcess.Unlock();
			throw e;
		}
		catch (Throwable e)
		{
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			mrefProcess.RecalcSteps(pdb);
		}
		catch (JewelPetriException e)
		{
			mrefStep.RollbackSafeRun();
			mrefProcess.Unlock();
			throw e;
		}
		catch (Throwable e)
		{
			mrefStep.RollbackSafeRun();
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			mrefStep.CommitSafeRun(pdb);
		}
		catch (JewelPetriException e)
		{
			mrefProcess.Unlock();
			throw e;
		}
		catch (Throwable e)
		{
			mrefProcess.Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		mrefProcess.Unlock();

		mbDone = true;

		mrefProcess.RunAutoSteps(parrTriggers, pdb);

		RunTriggers(parrTriggers, pdb);
	}
	
	public ILog getLog()
	{
		return mobjLog;
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

	private void CheckRunnable(SQLServer pdb)
		throws JewelPetriException, NotRunnableException
	{
		mrefStep = mrefProcess.GetOperation(OpID(), pdb);

		if ( mrefStep == null )
			throw new JewelPetriException("Error: Operation not found in this process.");

		if ( Constants.LevelID_Invalid.equals(mrefStep.GetLevel()) )
			throw new NotRunnableException("Error: Operation not currently available in this process.");
	}

	private void BuildLog(SQLServer pdb, UUID pidSource)
		throws JewelPetriException
	{
		PNLog lobjLog;
		ByteArrayOutputStream lstream;
		ObjectOutputStream lstreamObj;
		FileXfer lobjFile;

		if ( IsSilent() )
			return;

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
			lobjLog.setAt(4, pidSource);
			lobjLog.setAt(5, false);
			lobjLog.setAt(6, lobjFile);
			lobjLog.setAt(7, GetExternalProcess());
			lobjLog.SaveToDb(pdb);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}

		mobjLog = lobjLog;
	}

	protected IProcess GetProcess()
		throws JewelPetriException
	{
		if ( mrefProcess == null )
			mrefProcess = PNProcess.GetInstance(Engine.getCurrentNameSpace(), midProcess);

		return mrefProcess;
	}

	protected boolean TriggerOp(Operation pobjQueued)
		throws JewelPetriException
	{
		IOperation[] larrOps;
		int i;
		QueuedOp lobjQueue;

		if ( marrTriggers == null )
			throw new JewelPetriException("Invalid: Attempted to queue operation outside of execution.");

		larrOps = pobjQueued.GetProcess().GetScript().getOperations();
		for ( i = 0; i < larrOps.length; i++ )
		{
			if ( larrOps[i].getKey().equals(pobjQueued.OpID()) )
			{
				if ( !Constants.RoleID_Triggered.equals(larrOps[i].GetRole()) )
					throw new JewelPetriException("Error: Attempted to queue non-triggerable operation.");
				break;
			}
		}

		lobjQueue = new QueuedOp();
		lobjQueue.mobjQueued = pobjQueued;
		lobjQueue.mobjSource = this;
		marrTriggers.add(lobjQueue);

		return true;
	}

	private void RunTriggers(QueueContext parrTriggers, SQLServer pdb)
		throws JewelPetriException
	{
		QueuedOp lobjQueue;
		ILog lobjSourceLog;

		while ( (lobjQueue = parrTriggers.poll()) != null )
		{
			lobjSourceLog = ( lobjQueue.mobjSource == null ? null : lobjQueue.mobjSource.getLog() );
			try
			{
				lobjQueue.mobjQueued.Execute(lobjSourceLog == null ? null : lobjSourceLog.getKey(), parrTriggers, pdb);
			}
			catch (NotRunnableException e)
			{
			}
		}
	}

	protected boolean IsSilent()
	{
		return false;
	}
}
