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
import Jewel.Petri.SysObjects.ProcessData;

public class PNScript
	extends ObjectBase
	implements IScript
{
	private IOperation[] marrOperations;
	private IController[] marrControllers;

    public static PNScript GetInstance(UUID pidNameSpace, UUID pidKey)
    	throws JewelPetriException
	{
	    try
	    {
			return (PNScript)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNScript), pidKey);
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
			ResultSet lrsOperations, lrsControllers;
			IEntity lrefOperation, lrefController;
			int[] larrMembers;
			java.lang.Object[] larrParams;
			ArrayList<IOperation> larrAuxOps;
			ArrayList<IController> larrAuxCtrls;

			larrAuxOps = new ArrayList<IOperation>();

			larrMembers = new int[1];
			larrMembers[0] = Constants.FKScript_In_Operation;
			larrParams = new java.lang.Object[1];
			larrParams[0] = getKey();

			try
			{
				lrefOperation = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNOperation));
				ldb = new MasterDB();
				lrsOperations = lrefOperation.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
				while ( lrsOperations.next() )
					larrAuxOps.add((IOperation)PNOperation.GetInstance(getNameSpace(), lrsOperations));
				lrsOperations.close();
				ldb.Disconnect();

				marrOperations = larrAuxOps.toArray(new IOperation[larrAuxOps.size()]);
			}
			catch (Throwable e)
			{
				throw new JewelEngineException(e.getMessage(), e);
			}

			larrAuxCtrls = new ArrayList<IController>();

			larrMembers = new int[1];
			larrMembers[0] = Constants.FKScript_In_Controller;
			larrParams = new java.lang.Object[1];
			larrParams[0] = getKey();

			try
			{
				lrefController = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNController));
				ldb = new MasterDB();
				lrsControllers = lrefController.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
				while ( lrsControllers.next() )
					larrAuxCtrls.add((IController)PNController.GetInstance(getNameSpace(), lrsControllers));
				lrsControllers.close();
				ldb.Disconnect();

				marrControllers = larrAuxCtrls.toArray(new IController[larrAuxCtrls.size()]);
			}
			catch (Throwable e)
			{
				throw new JewelEngineException(e.getMessage(), e);
			}
	}

	public IOperation[] getOperations()
	{
		return marrOperations;
	}

	public IController[] getControllers()
	{
		return marrControllers;
	}

	public UUID GetDataType()
	{
		return (UUID)getAt(2);
	}

	public void CreateInstance(UUID pidNSpace, UUID pidData, UUID pidParent, SQLServer pdb)
		throws JewelPetriException
	{
		ObjectBase lobjAux;
		ProcessData lobjData;
		PNProcess lobjProc;

		try
		{
			lobjAux = Engine.GetWorkInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(2)), pidData);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}

		if ( !(lobjAux instanceof ProcessData) )
			throw new JewelPetriException("Unexpected: Invalid data object for new process.");
		lobjData = (ProcessData)lobjAux;

		try
		{
			lobjProc = (PNProcess)Engine.GetWorkInstance(Engine.FindEntity(pidNSpace, Constants.ObjID_PNProcess), (UUID)null);
			lobjProc.setAt(0, getKey());
			lobjProc.setAt(1, pidData);
			lobjProc.setAt(2, Engine.getCurrentUser());
			lobjProc.setAt(3, pidParent);
			lobjProc.setAt(4, false);
			lobjProc.SaveToDb(pdb);
			Engine.GetCache(true).setAt(Engine.FindEntity(pidNSpace, Constants.ObjID_PNProcess), lobjProc.getKey(), lobjProc);

			lobjData.SetProcessID(lobjProc.getKey());
			lobjData.SaveToDb(pdb);

			if ( pidData == null )
			{
				pidData = lobjData.getKey();
				lobjProc.setAt(1, pidData);
				lobjProc.SaveToDb(pdb);
			}

			lobjProc.Setup(pdb, null, false);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public void CreateInstance(java.lang.Object[] parrParams)
		throws JewelPetriException
	{
    	UUID lidNSpace;
		UUID lidData;
		UUID lidParent;
		MasterDB ldb;

		if ( (parrParams == null) || (parrParams.length < 4) || (parrParams[3] == null) || !(parrParams[3] instanceof UUID))
			throw new JewelPetriException("Invalid Argument: Name Space to start up in is null or not an identifier.");
		lidNSpace = (UUID)parrParams[3];
		if ( (parrParams.length >= 5) && (parrParams[4] != null) && (parrParams[4] instanceof UUID))
			lidData = (UUID)parrParams[4];
		else
			lidData = null;
		if ( (parrParams.length >= 6) && (parrParams[5] != null) && (parrParams[5] instanceof UUID))
			lidParent = (UUID)parrParams[5];
		else
			lidParent = null;

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
			CreateInstance(lidNSpace, lidData, lidParent, ldb);
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

		throw new JewelPetriException("Process successfully deployed.");
	}
}
