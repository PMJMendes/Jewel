package Jewel.Petri.SysObjects;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Interfaces.IEntity;
import Jewel.Petri.Constants;
import Jewel.Petri.Objects.PNProcess;
import Jewel.Petri.Objects.PNStep;

public abstract class Operation
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	protected UUID midProcess;

	public Operation(UUID pidProcess)
	{
		midProcess = pidProcess;
	}

	protected abstract UUID OpID();
	protected abstract void Run() throws JewelPetriException;

	public final void Execute()
		throws JewelPetriException
	{
		PNProcess lrefProcess;
		int i;
		PNStep lobjStep;

		lrefProcess = PNProcess.GetInstance(Engine.getCurrentNameSpace(), midProcess);

		i = 0;
		while ( !lrefProcess.Lock() )
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

		try
		{
			lobjStep = GetStep();
			Run();
		}
		finally
		{
			lrefProcess.Unlock();
		}
	}

	private PNStep GetStep()
		throws JewelPetriException
	{
		int[] larrMembers;
		java.lang.Object[] larrParams;
		IEntity lrefStep;
		MasterDB ldb;
		ResultSet lrsSteps;
		PNStep lobjResult;

		larrMembers = new int[2];
		larrMembers[0] = Constants.FKProcess_In_Step;
		larrMembers[1] = Constants.FKOperation_In_Step;
		larrParams = new java.lang.Object[2];
		larrParams[0] = midProcess;
		larrParams[1] = OpID();

		lobjResult = null;

		try
		{
			lrefStep = Entity.GetInstance(Engine.FindEntity(Engine.getCurrentNameSpace(), Constants.ObjID_PNStep));
			ldb = new MasterDB();
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			lrsSteps = lrefStep.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
		}
		catch (Throwable e)
		{
			try { ldb.Disconnect(); } catch (SQLException e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			if ( lrsSteps.next() )
			{
				lobjResult = PNStep.GetInstance(Engine.getCurrentNameSpace(), lrsSteps);
				if ( lrsSteps.next() )
				{
					lobjResult = null;
				}
			}
		}
		catch (Throwable e)
		{
			try { lrsSteps.close(); } catch (SQLException e1) {}
			try { ldb.Disconnect(); } catch (SQLException e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			lrsSteps.close();
		}
		catch (Throwable e)
		{
			try { ldb.Disconnect(); } catch (SQLException e1) {}
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

		if ( lobjResult == null )
			throw new JewelPetriException("Database is inconsistent: Unexpected number of steps for operation in process.");

		return lobjResult;
	}
}
