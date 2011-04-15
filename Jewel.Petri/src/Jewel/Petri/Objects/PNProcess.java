package Jewel.Petri.Objects;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Petri.Constants;
import Jewel.Petri.Interfaces.INode;
import Jewel.Petri.Interfaces.IOperation;
import Jewel.Petri.Interfaces.IProcess;
import Jewel.Petri.Interfaces.IScript;
import Jewel.Petri.Interfaces.IStep;
import Jewel.Petri.SysObjects.JewelPetriException;

public class PNProcess
	extends ObjectBase
	implements IProcess
{
	private IScript mrefScript;
	private INode[] marrNodes;
	private IStep[] marrSteps;
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
			if ( lrsNodes.next() )
			{
				larrAuxNodes.add((INode)PNNode.GetInstance(getNameSpace(), lrsNodes));
			}
			lrsNodes.close();

			larrMembers[0] = Constants.FKProcess_In_Step;
			larrAuxSteps = new ArrayList<IStep>();
			lrsSteps = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNStep))
					.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
			if ( lrsSteps.next() )
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

		marrNodes = larrAuxNodes.toArray(new INode[larrAuxNodes.size()]);
		marrSteps = larrAuxSteps.toArray(new IStep[larrAuxSteps.size()]);
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

	public synchronized boolean Lock()
	{
		if ( mlngLock > 0 )
			return false;
		mlngLock++;
		return true;
	}

	public synchronized void Unlock()
	{
		mlngLock = 0;
	}

	public void RecalcSteps(SQLServer pdb)
		throws JewelPetriException
	{
		ArrayList<IStep> larrAux;
		IOperation[] larrOps;
		PNStep lobjAux;
		int i;

		larrAux = new ArrayList<IStep>();

		for ( i = 0; i < marrSteps.length; i++ )
			if ( marrSteps[i].IsRunnable() )
				larrAux.add(marrSteps[i]);

		larrOps = mrefScript.getOperations();

		try
		{
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
				lobjAux.Initialize();

				if ( lobjAux.IsRunnable() )
				{
					lobjAux.SaveToDb(pdb);
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
}
