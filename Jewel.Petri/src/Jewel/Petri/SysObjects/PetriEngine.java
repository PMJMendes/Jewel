package Jewel.Petri.SysObjects;

import java.sql.ResultSet;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Interfaces.IEntity;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Petri.Constants;
import Jewel.Petri.Objects.PNProcess;

public class PetriEngine
{
	public static void StartupAllProcesses(UUID pidNameSpace)
		throws JewelPetriException
	{
		IEntity lrefProcesses;
		MasterDB ldbRead;
		MasterDB ldbWrite;
		ResultSet lrsProcesses;
		PNProcess lobjProc;
		boolean b;

		try
		{
			lrefProcesses = Entity.GetInstance(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNProcess));
			Engine.pushNameSpace(pidNameSpace);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			ldbWrite = new MasterDB();
		}
		catch (Throwable e)
		{
			try { Engine.popNameSpace(); } catch (JewelEngineException e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			ldbRead = new MasterDB();
		}
		catch (Throwable e)
		{
			try { ldbWrite.Disconnect(); } catch (Throwable e1) {}
			try { Engine.popNameSpace(); } catch (JewelEngineException e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			lrsProcesses = lrefProcesses.SelectByMembers(ldbRead, new int[] {4}, new java.lang.Object[] {false}, null);
		}
		catch (Throwable e)
		{
			try { ldbRead.Disconnect(); } catch (Throwable e1) {}
			try { ldbWrite.Disconnect(); } catch (Throwable e1) {}
			try { Engine.popNameSpace(); } catch (JewelEngineException e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			while ( lrsProcesses.next() )
			{
				lobjProc = PNProcess.GetInstance(pidNameSpace, lrsProcesses);

				b = true;
				ldbWrite.BeginTrans();
				try
				{
					lobjProc.Setup(ldbWrite, null, true);
				}
				catch (Throwable e)
				{
					ldbWrite.Rollback();
					b = false;
				}
				if ( b )
					ldbWrite.Commit();
			}
		}
		catch (Throwable e)
		{
			try { lrsProcesses.close(); } catch (Throwable e1) {}
			try { ldbRead.Disconnect(); } catch (Throwable e1) {}
			try { ldbWrite.Disconnect(); } catch (Throwable e1) {}
			try { Engine.popNameSpace(); } catch (JewelEngineException e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			lrsProcesses.close();
		}
		catch (Throwable e)
		{
			try { ldbRead.Disconnect(); } catch (Throwable e1) {}
			try { ldbWrite.Disconnect(); } catch (Throwable e1) {}
			try { Engine.popNameSpace(); } catch (JewelEngineException e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			ldbRead.Disconnect();
		}
		catch (Throwable e)
		{
			try { ldbWrite.Disconnect(); } catch (Throwable e1) {}
			try { Engine.popNameSpace(); } catch (JewelEngineException e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			ldbWrite.Disconnect();
		}
		catch (Throwable e)
		{
			try { Engine.popNameSpace(); } catch (JewelEngineException e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			Engine.popNameSpace();
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}
	}
}
