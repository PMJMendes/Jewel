package Jewel.Petri.Objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Interfaces.IEntity;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Petri.Constants;
import Jewel.Petri.Interfaces.IController;
import Jewel.Petri.Interfaces.INode;
import Jewel.Petri.Interfaces.IOperation;
import Jewel.Petri.Interfaces.IProcess;
import Jewel.Petri.Interfaces.IScript;
import Jewel.Petri.Interfaces.IStep;
import Jewel.Petri.SysObjects.JewelPetriException;
import Jewel.Petri.SysObjects.Operation;
import Jewel.Petri.SysObjects.ProcessData;

public class PNProcess
	extends ObjectBase
	implements IProcess
{
	private IScript mrefScript;
	private INode[] marrNodes;
	private IStep[] marrSteps;
	private IProcess mrefParent;
	private int mlngLock;

    public static PNProcess GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNProcess)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNProcess), pidKey);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

    public static PNProcess GetInstance(UUID pidNameSpace, ResultSet prsObject)
		throws JewelPetriException
	{
	    try
	    {
			return (PNProcess)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNProcess), prsObject);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public void Initialize()
		throws JewelEngineException
	{
		int[] larrMembers;
		java.lang.Object[] larrParams;
		MasterDB ldb;
		ArrayList<INode> larrAuxNodes;
		ResultSet lrsNodes;
		ArrayList<IStep> larrAuxSteps;
		ResultSet lrsSteps;

		mrefScript = null;
		mrefParent = null;
		mlngLock = 0;

		larrMembers = new int[1];
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();

		try
		{
			ldb = new MasterDB();

			larrMembers[0] = Constants.FKProcess_In_Node;
			larrAuxNodes = new ArrayList<INode>();
			lrsNodes = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNNode))
					.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
			while ( lrsNodes.next() )
			{
				larrAuxNodes.add((INode)PNNode.GetInstance(getNameSpace(), lrsNodes));
			}
			lrsNodes.close();
			ldb.Disconnect();
		}
		catch (Throwable e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}

		marrNodes = larrAuxNodes.toArray(new INode[larrAuxNodes.size()]);

		try
		{
			ldb = new MasterDB();

			larrMembers[0] = Constants.FKProcess_In_Step;
			larrAuxSteps = new ArrayList<IStep>();
			lrsSteps = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNStep))
					.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
			while ( lrsSteps.next() )
			{
				larrAuxSteps.add((IStep)PNStep.GetInstance(getNameSpace(), lrsSteps));
			}
			lrsSteps.close();
			ldb.Disconnect();
		}
		catch (Throwable e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}

		marrSteps = larrAuxSteps.toArray(new IStep[larrAuxSteps.size()]);
	}
	
	public void Setup(java.lang.Object[] parrParams)
		throws JewelPetriException
	{
		MasterDB ldb;
		ProcessData lobjData;

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
			try { ldb.Disconnect(); } catch (SQLException e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			Setup(ldb, new Operation.QueueContext(), false);
		}
		catch (JewelPetriException e)
		{
			try { ldb.Rollback(); } catch (SQLException e1) {}
			try { ldb.Disconnect(); } catch (SQLException e1) {}
			throw e;
		}
		catch (Throwable e)
		{
			try { ldb.Rollback(); } catch (SQLException e1) {}
			try { ldb.Disconnect(); } catch (SQLException e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		if ( parrParams[1] instanceof ProcessData )
		{
			lobjData = (ProcessData)parrParams[1];
			lobjData.SetProcessID(getKey());
			try
			{
				lobjData.SaveToDb(ldb);
			}
			catch (Throwable e)
			{
				try { ldb.Rollback(); } catch (SQLException e1) {}
				try { ldb.Disconnect(); } catch (SQLException e1) {}
				throw new JewelPetriException(e.getMessage(), e);
			}
		}

		try
		{
			ldb.Commit();
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
	}

	public UUID GetScriptID()
	{
		return (UUID)getAt(0);
	}

	public IScript GetScript()
		throws JewelPetriException
	{
		if ( mrefScript == null )
			mrefScript = (IScript)PNScript.GetInstance(getNameSpace(), (UUID)getAt(0));

		return mrefScript;
	}

	public INode[] GetNodes()
	{
		return marrNodes;
	}

	public IStep[] GetValidSteps()
	{
		return marrSteps;
	}

	public IStep GetOperation(UUID pidOperation)
		throws JewelPetriException
	{
		IStep lobjResult;
		int i;

		lobjResult = null;

		for ( i = 0; i < marrSteps.length; i++ )
		{
			if ( marrSteps[i].GetOperationID().equals(pidOperation) )
			{
				if ( lobjResult != null )
					throw new JewelPetriException("Database is inconsistent: Unexpected number of steps for operation in process.");
				lobjResult = marrSteps[i];
			}
		}

		return lobjResult;
	}

	public IProcess GetParent()
		throws JewelPetriException
	{
		if ( (mrefParent == null) && (getAt(3) != null) )
			mrefParent = (IProcess)PNProcess.GetInstance(getNameSpace(), (UUID)getAt(3));

		return mrefParent;
	}

	public UUID GetManagerID()
	{
		return (UUID)getAt(2);
	}

    public void SetManagerID(UUID pidManager, SQLServer pdb)
    	throws JewelPetriException
    {
    	if ( pidManager == null )
    		throw new JewelPetriException("Process manager cannot be null.");

    	internalSetAt(2, pidManager);

    	try
    	{
			SaveToDb(pdb);
		}
    	catch (Throwable e)
    	{
    		throw new JewelPetriException(e.getMessage(), e);
		}
    }


	public synchronized final boolean Lock()
	{
		if ( mlngLock > 0 )
			return false;
		mlngLock++;
		return true;
	}

	public synchronized final void Unlock()
	{
		mlngLock = 0;
	}

	public void RecalcSteps(SQLServer pdb)
		throws JewelPetriException
	{
		UUID lidSteps;
		ArrayList<IStep> larrAux;
		IOperation[] larrOps;
		PNStep lobjAux;
		int i;

		larrAux = new ArrayList<IStep>();

		for ( i = 0; i < marrSteps.length; i++ )
			if ( marrSteps[i].IsRunnable() )
				larrAux.add(marrSteps[i]);

		larrOps = GetScript().getOperations();

		try
		{
			lidSteps = Engine.FindEntity(getNameSpace(), Constants.ObjID_PNStep);
			for ( i = 0; i < larrOps.length; i++ )
			{
				if ( GetOperation(larrOps[i].getKey()) != null )
					continue;

				lobjAux = PNStep.GetInstance(getNameSpace(), (UUID)null);
				lobjAux.setAt(0, getKey());
				lobjAux.setAt(1, larrOps[i].getKey());
				lobjAux.setAt(2, larrOps[i].getDefaultLevel());
				lobjAux.setAt(3, null);
				lobjAux.setAt(4, null);
				lobjAux.SetupNodes(this);

				if ( lobjAux.IsRunnable() )
				{
					lobjAux.SaveToDb(pdb);
					Engine.GetCache(true).setAt(lidSteps, lobjAux.getKey(), lobjAux);
					larrAux.add((IStep)lobjAux);
				}
			}
		}
		catch (JewelPetriException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}

		for ( i = 0; i < marrSteps.length; i++ )
			if ( !marrSteps[i].IsRunnable() )
				marrSteps[i].Delete(pdb);

		marrSteps = larrAux.toArray(new IStep[larrAux.size()]);
	}

	public void RemoveStep(SQLServer pdb, IStep pobjStep)
		throws JewelPetriException
	{
		ArrayList<IStep> larrAux;
		int i;

		pobjStep.Delete(pdb);

		larrAux = new ArrayList<IStep>();

		for ( i = 0; i < marrSteps.length; i++ )
			if ( !marrSteps[i].getKey().equals(pobjStep) )
				larrAux.add(marrSteps[i]);

		marrSteps = larrAux.toArray(new IStep[larrAux.size()]);
	}

	public void Setup(SQLServer pdb, Operation.QueueContext pobjContext, boolean pbInitialize)
		throws JewelPetriException
	{
		IController[] larrControllers;
		UUID lidNodes;
		UUID lidSteps;
		ArrayList<INode> larrAuxNodes;
		int i;
		PNNode lobjNode;
		ArrayList<IStep> larrAux;
		IOperation[] larrOps;
		PNStep lobjStep;

		if ( !Lock() )
			throw new JewelPetriException("Unexpected: Process locked during setup.");

		if ( IsRunning() )
		{
			Unlock();
			return;
		}

		larrAuxNodes = new ArrayList<INode>();
		larrControllers = GetScript().getControllers();
		try
		{
			lidNodes = Engine.FindEntity(getNameSpace(), Constants.ObjID_PNNode);
			for ( i = 0; i < larrControllers.length; i++ )
			{
				lobjNode = (PNNode)Engine.GetWorkInstance(lidNodes, (UUID)null);
				lobjNode.setAt(0, getKey());
				lobjNode.setAt(1, larrControllers[i].getKey());
				lobjNode.setAt(2, larrControllers[i].getInitialCount());
				lobjNode.SaveToDb(pdb);
				lobjNode.Initialize();
				Engine.GetCache(true).setAt(lidNodes, lobjNode.getKey(), lobjNode);
				larrAuxNodes.add(lobjNode);
			}

			marrNodes = larrAuxNodes.toArray(new INode[larrAuxNodes.size()]);
		}
		catch (Throwable e)
		{
			Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		larrAux = new ArrayList<IStep>();
		larrOps = GetScript().getOperations();
		try
		{
			lidSteps = Engine.FindEntity(getNameSpace(), Constants.ObjID_PNStep);
			for ( i = 0; i < larrOps.length; i++ )
			{
				lobjStep = PNStep.GetInstance(getNameSpace(), (UUID)null);
				lobjStep.setAt(0, getKey());
				lobjStep.setAt(1, larrOps[i].getKey());
				lobjStep.setAt(2, larrOps[i].getDefaultLevel());
				lobjStep.setAt(3, null);
				lobjStep.setAt(4, null);
				lobjStep.SetupNodes(this);

				if ( lobjStep.IsRunnable() )
				{
					lobjStep.SaveToDb(pdb);
					Engine.GetCache(true).setAt(lidSteps, lobjStep.getKey(), lobjStep);
					larrAux.add((IStep)lobjStep);
				}
			}
		}
		catch (JewelPetriException e)
		{
			Unlock();
			throw e;
		}
		catch (Throwable e)
		{
			Unlock();
			throw new JewelPetriException(e.getMessage(), e);
		}

		marrSteps = larrAux.toArray(new IStep[larrAux.size()]);

		Restart(pdb);

		Unlock();

		RunAutoSteps(pobjContext, pdb);
	}

	public void RunAutoSteps(Operation.QueueContext pobjContext, SQLServer pdb)
		throws JewelPetriException
	{
		int i;
		Operation lobjOp;

		if ( !Lock() )
			throw new JewelPetriException("Unexpected: Process locked during autorun.");

		for ( i = 0; i < marrSteps.length; i++ )
		{
			if ( Constants.RoleID_Autorun.equals(marrSteps[i].GetRole()) )
			{
				lobjOp = marrSteps[i].GetOperation().GetNewInstance(getKey());
				Unlock();

				if ( pobjContext == null )
					lobjOp.Execute(pdb);
				else
					lobjOp.Enqueue(pobjContext);

				return;
			}
		}

		Unlock();
	}

	public ObjectBase GetData()
		throws JewelPetriException
	{
		UUID lidData;

		lidData = (UUID)getAt(1);
		if ( lidData == null )
			return null;

		try
		{
			return Engine.GetWorkInstance(Engine.FindEntity(getNameSpace(), GetScript().GetDataType()), lidData);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public boolean IsRunning()
	{
		return (Boolean)getAt(4);
	}

	public void Restart(SQLServer pdb)
		throws JewelPetriException
	{
		internalSetAt(4, true);
		try
		{
			SaveToDb(pdb);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public void Stop(SQLServer pdb)
		throws JewelPetriException
	{
		internalSetAt(4, false);
		try
		{
			SaveToDb(pdb);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public IProcess[] GetCurrentSubProcesses()
		throws JewelPetriException
	{
		ArrayList<IProcess> larrAux;
		int[] larrMembers;
		java.lang.Object[] larrParams;
		IEntity lrefProcess;
	    MasterDB ldb;
	    ResultSet lrsInfo;

		larrAux = new ArrayList<IProcess>();

		larrMembers = new int[1];
		larrMembers[0] = Constants.FKParent_In_Process;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();

		try
		{
			lrefProcess = Entity.GetInstance(Engine.FindEntity(Engine.getCurrentNameSpace(), Constants.ObjID_PNProcess)); 
			ldb = new MasterDB();
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			lrsInfo = lrefProcess.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
		}
		catch (Throwable e)
		{
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			while ( lrsInfo.next() )
				larrAux.add(PNProcess.GetInstance(getNameSpace(), lrsInfo));
		}
		catch (JewelPetriException e)
		{
			try { lrsInfo.close(); } catch (Throwable e1) {}
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			throw e;
		}
		catch (Throwable e)
		{
			try { lrsInfo.close(); } catch (Throwable e1) {}
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			lrsInfo.close();
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

		return larrAux.toArray(new IProcess[larrAux.size()]);
	}

	public void SetParentProcId(UUID pidParent, SQLServer pdb)
		throws JewelPetriException
	{
    	internalSetAt(3, pidParent);

    	try
    	{
			SaveToDb(pdb);
		}
    	catch (Throwable e)
    	{
    		throw new JewelPetriException(e.getMessage(), e);
		}
	}
}
