package Jewel.Petri.Objects;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;
import Jewel.Petri.Interfaces.*;
import Jewel.Petri.SysObjects.JewelPetriException;

public class PNOperation
	extends ObjectBase
	implements IOperation
{
	private IController[] marrInputs;
	private IController[] marrOutputs;

    public static PNOperation GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNOperation)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNOperation), pidKey);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

    public static PNOperation GetInstance(UUID pidNameSpace, ResultSet prsObject)
    	throws JewelPetriException
    {
        try
        {
			return (PNOperation)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNOperation), prsObject);
		}
        catch (Throwable e)
        {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
    }

	public void Initialize()
		throws JewelEngineException
	{
		MasterDB ldb;
		ResultSet lrsInputs, lrsOutputs;
		IEntity lrefSink, lrefSource;
		int[] larrMembers;
		java.lang.Object[] larrParams;
		ArrayList<IController> larrAux;

		larrAux = new ArrayList<IController>();

		larrMembers = new int[1];
		larrMembers[0] = Constants.FKOperation_In_Sink;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();

		try
		{
			lrefSink = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNSink));
			ldb = new MasterDB();
			lrsInputs = lrefSink.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
			while ( lrsInputs.next() )
				larrAux.add(((ISink)PNSink.GetInstance(getNameSpace(), lrsInputs)).GetController());
			lrsInputs.close();
			ldb.Disconnect();

			marrInputs = larrAux.toArray(new IController[larrAux.size()]);
		}
		catch (Throwable e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}

		larrAux = new ArrayList<IController>();

		larrMembers = new int[1];
		larrMembers[0] = Constants.FKOperation_In_Source;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();

		try
		{
			lrefSource = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNSource));
			ldb = new MasterDB();
			lrsOutputs = lrefSource.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
			while ( lrsOutputs.next() )
				larrAux.add(((ISource)PNSource.GetInstance(getNameSpace(), lrsOutputs)).GetController());
			lrsOutputs.close();
			ldb.Disconnect();

			marrOutputs = larrAux.toArray(new IController[larrAux.size()]);
		}
		catch (Throwable e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}
	}

    public UUID GetScriptID()
    {
    	return (UUID)getAt(1);
    }

    public IScript GetScript()
    	throws JewelPetriException
    {
		return (IScript)PNScript.GetInstance(getNameSpace(), GetScriptID());
    }

	public IController[] getInputs()
	{
		return marrInputs;
	}

	public IController[] getOutputs()
	{
		return marrOutputs;
	}

	public UUID getDefaultLevel()
	{
		return (UUID)getAt(3);
	}

	public IStep GetStepInProcess(UUID pidProcess)
		throws JewelPetriException
	{
		int[] larrMembers;
		java.lang.Object[] larrParams;
		IEntity lrefStep;
		MasterDB ldb;
		ResultSet lrsSteps;
		IStep lobjResult;

		larrMembers = new int[2];
		larrMembers[0] = Constants.FKProcess_In_Step;
		larrMembers[1] = Constants.FKOperation_In_Step;
		larrParams = new java.lang.Object[2];
		larrParams[0] = pidProcess;
		larrParams[1] = getKey();

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
					throw new JewelPetriException("Database is inconsistent: Unexpected number of steps for operation in process.");
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

		return lobjResult;
	}
}
