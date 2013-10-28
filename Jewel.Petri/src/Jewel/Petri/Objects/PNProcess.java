package Jewel.Petri.Objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import Jewel.Engine.Engine;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Interfaces.IEntity;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Petri.Constants;
import Jewel.Petri.Interfaces.IController;
import Jewel.Petri.Interfaces.ILog;
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
	private ReentrantLock mrefLock;

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
		mrefScript = null;
		mrefParent = null;
		marrNodes = null;
		marrSteps = null;
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

	public INode[] GetNodes(SQLServer pdb)
		throws JewelPetriException
	{
		int[] larrMembers;
		java.lang.Object[] larrParams;
		ArrayList<INode> larrAuxNodes;
		ResultSet lrsNodes;

		if ( marrNodes == null )
		{
			larrMembers = new int[1];
			larrMembers[0] = Constants.FKProcess_In_Node;
			larrParams = new java.lang.Object[1];
			larrParams[0] = getKey();
			larrAuxNodes = new ArrayList<INode>();

			try
			{
				lrsNodes = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNNode))
						.SelectByMembers(pdb, larrMembers, larrParams, new int[0]);
			}
			catch (Throwable e)
			{
				throw new JewelPetriException(e.getMessage(), e);
			}

			try
			{
				while ( lrsNodes.next() )
				{
					larrAuxNodes.add((INode)PNNode.GetInstance(getNameSpace(), lrsNodes));
				}
			}
			catch (Throwable e)
			{
				try {lrsNodes.close();} catch (SQLException e1) {}
				throw new JewelPetriException(e.getMessage(), e);
			}

			try
			{
				lrsNodes.close();
			}
			catch (Throwable e)
			{
				throw new JewelPetriException(e.getMessage(), e);
			}

			marrNodes = larrAuxNodes.toArray(new INode[larrAuxNodes.size()]);
		}

		return marrNodes;
	}

	public IStep[] GetSteps(SQLServer pdb) 
		throws JewelPetriException
	{
		ArrayList<IStep> larrAuxSteps;
		ResultSet lrsSteps;

		if ( marrSteps == null )
		{
			larrAuxSteps = new ArrayList<IStep>();
			lrsSteps = null;
			try
			{
				lrsSteps = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNStep)).SelectByMembers(pdb,
						new int[] {Constants.FKProcess_In_Step}, new java.lang.Object[] {getKey()}, new int[0]);
				while ( lrsSteps.next() )
				{
					larrAuxSteps.add((IStep)PNStep.GetInstance(getNameSpace(), lrsSteps));
				}
				lrsSteps.close();
			}
			catch (Throwable e)
			{
				if ( lrsSteps != null ) try {lrsSteps.close();} catch (Throwable e1) {}
				throw new JewelPetriException(e.getMessage(), e);
			}
			marrSteps = larrAuxSteps.toArray(new IStep[larrAuxSteps.size()]);
		}

		return marrSteps;
	}

	public IStep[] GetValidSteps(SQLServer pdb) 
		throws JewelPetriException
	{
		ArrayList<IStep> larrAuxSteps;
		int i;

		GetSteps(pdb);

		larrAuxSteps = new ArrayList<IStep>();
		for ( i = 0; i < marrSteps.length; i++ )
		{
			if ( !Constants.LevelID_Invalid.equals(marrSteps[i].GetLevel()) )
				larrAuxSteps.add(marrSteps[i]);
		}

		return larrAuxSteps.toArray(new IStep[larrAuxSteps.size()]);
	}

	public IStep GetOperation(UUID pidOperation, SQLServer pdb)
		throws JewelPetriException
	{
		IStep lobjResult;
		int i;

		GetSteps(pdb);

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

    public IStep GetValidOperation(UUID pidOperation)
    	throws JewelPetriException
	{
    	MasterDB ldb;
    	IStep lobjResult;

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
    		lobjResult = GetOperation(pidOperation, ldb);
    	}
    	catch (JewelPetriException e)
    	{
    		try { ldb.Disconnect(); } catch (Throwable e1) {}
    		throw e;
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

    	if ( Constants.LevelID_Invalid.equals(lobjResult.GetLevel()) )
    		return null;

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

	public final boolean Lock()
	{
		synchronized(this)
		{
			if ( mrefLock == null )
				mrefLock = new ReentrantLock();
		}

		return mrefLock.tryLock();
	}

	public final boolean Lock(long plngTimeout)
	{
		synchronized(this)
		{
			if ( mrefLock == null )
				mrefLock = new ReentrantLock();
		}

		try
		{
			return mrefLock.tryLock(plngTimeout, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e)
		{
		}

		return false;
	}

	public final boolean ForceLock()
	{
		synchronized(this)
		{
			if ( mrefLock == null )
				mrefLock = new ReentrantLock();
		}

		try
		{
			mrefLock.lockInterruptibly();
		}
		catch (InterruptedException e)
		{
			return false;
		}

		return true;
	}

	public final void Unlock()
	{
		synchronized(this)
		{
			if ( mrefLock == null )
				mrefLock = new ReentrantLock();
		}

		mrefLock.unlock();
	}

	public void RecalcSteps(SQLServer pdb)
		throws JewelPetriException
	{
		int i;

		GetSteps(pdb);

		for ( i = 0; i < marrSteps.length; i++ )
			marrSteps[i].CalcRunnable(pdb);
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
		ArrayList<IStep> larrAuxSteps;
		IOperation[] larrOps;
		PNStep lobjStep;

		if ( !Lock() )
			throw new JewelPetriException("Unexpected: Process locked during setup.");

		try
		{
			if ( IsRunning() )
				return;

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
				throw new JewelPetriException(e.getMessage(), e);
			}

			larrAuxSteps = new ArrayList<IStep>();
			larrOps = GetScript().getOperations();
			try
			{
				lidSteps = Engine.FindEntity(getNameSpace(), Constants.ObjID_PNStep);
				for ( i = 0; i < larrOps.length; i++ )
				{
					lobjStep = (PNStep)Engine.GetWorkInstance(lidSteps, (UUID)null);
					lobjStep.setAt(0, getKey());
					lobjStep.setAt(1, larrOps[i].getKey());
					lobjStep.setAt(2, larrOps[i].getDefaultLevel());
					lobjStep.setAt(3, null);
					lobjStep.setAt(4, null);
					lobjStep.SetupNodes(this, pdb);
					lobjStep.SaveToDb(pdb);
					lobjStep.CalcRunnable(pdb);
					Engine.GetCache(true).setAt(lidSteps, lobjStep.getKey(), lobjStep);
					larrAuxSteps.add(lobjStep);
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

			marrSteps = larrAuxSteps.toArray(new IStep[larrAuxSteps.size()]);

			Restart(pdb);

			RunAutoSteps(pobjContext, pdb);
		}
		finally
		{
			Unlock();
		}
	}

	public void RunAutoSteps(Operation.QueueContext pobjContext, SQLServer pdb)
		throws JewelPetriException
	{
		int i;
		Operation lobjOp;

		for ( i = 0; i < marrSteps.length; i++ )
		{
			if ( Constants.RoleID_Autorun.equals(marrSteps[i].GetRole()) &&
					!Constants.LevelID_Invalid.equals(marrSteps[i].GetLevel()) )
			{
				lobjOp = marrSteps[i].GetOperation().GetNewInstance(getKey());

				if ( pobjContext == null )
					lobjOp.Execute(pdb);
				else
					lobjOp.Enqueue(pobjContext);

				return;
			}
		}
	}

	public UUID GetDataKey()
	{
		return (UUID)getAt(1);
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

	public void SetDataObjectID(UUID pidData, SQLServer pdb)
		throws JewelPetriException
	{
		internalSetAt(1, pidData);
		try
		{
			SaveToDb(pdb);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public IProcess[] GetCurrentSubProcesses(SQLServer pdb)
		throws JewelPetriException
	{
		ArrayList<IProcess> larrAux;
		int[] larrMembers;
		java.lang.Object[] larrParams;
		IEntity lrefProcess;
	    ResultSet lrsInfo;

		larrAux = new ArrayList<IProcess>();

		larrMembers = new int[1];
		larrMembers[0] = Constants.FKParent_In_Process;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();

		try
		{
			lrefProcess = Entity.GetInstance(Engine.FindEntity(Engine.getCurrentNameSpace(), Constants.ObjID_PNProcess)); 
			lrsInfo = lrefProcess.SelectByMembers(pdb, larrMembers, larrParams, new int[0]);
		}
		catch (Throwable e)
		{
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
			throw e;
		}
		catch (Throwable e)
		{
			try { lrsInfo.close(); } catch (Throwable e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			lrsInfo.close();
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
    	mrefParent = null;
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

	public ILog GetLiveLog(UUID pidOpCode)
		throws JewelPetriException
	{
		MasterDB ldb;
		ILog lobjResult;

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
			lobjResult = GetLiveLog(pidOpCode, ldb);
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

		return lobjResult;
	}

	public ILog GetLiveLog(UUID pidOpCode, SQLServer pdb)
		throws JewelPetriException
	{
		IEntity lrefLogs;
		ResultSet lrsLogs;
		ILog lobjResult;

		lobjResult = null;

		try
		{
			lrefLogs = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNLog));
			lrsLogs = lrefLogs.SelectByMembers(pdb, new int[] {Constants.FKProcess_In_Log, Constants.FKOperation_In_Log,
					Constants.Undone_In_Log}, new java.lang.Object[] {getKey(), pidOpCode, false}, new int[] {-Constants.Timestamp_In_Log});
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			if ( lrsLogs.next() )
				lobjResult = PNLog.GetInstance(getNameSpace(), lrsLogs); 
		}
		catch (Throwable e)
		{
			try { lrsLogs.close(); } catch (Throwable e1) {}
			throw new JewelPetriException(e.getMessage(), e);
		}

		try
		{
			lrsLogs.close();
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}

		return lobjResult;
	}
}
