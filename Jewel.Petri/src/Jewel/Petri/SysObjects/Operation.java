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
import Jewel.Petri.Interfaces.IStep;
import Jewel.Petri.Objects.PNOperation;
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
		IStep lobjStep;

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
			lobjStep = PNOperation.GetInstance(Engine.getCurrentNameSpace(), OpID()).GetStepInProcess(midProcess);
			Run();
		}
		finally
		{
			lrefProcess.Unlock();
		}
	}
}
