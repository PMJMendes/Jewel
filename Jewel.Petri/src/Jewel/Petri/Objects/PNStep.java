package Jewel.Petri.Objects;

import java.sql.ResultSet;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Interfaces.IEntity;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Petri.Constants;
import Jewel.Petri.Interfaces.IController;
import Jewel.Petri.Interfaces.INode;
import Jewel.Petri.Interfaces.IOperation;
import Jewel.Petri.Interfaces.IProcess;
import Jewel.Petri.Interfaces.IStep;
import Jewel.Petri.SysObjects.JewelPetriException;

public class PNStep
	extends ObjectBase
	implements IStep
{
	private IProcess mrefProcess;
	private IOperation mrefOperation;
	private INode[] marrInputs;
	private INode[] marrOutputs;

    public static PNStep GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNStep)Engine.GetWorkInstance(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNStep), pidKey);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

    public static PNStep GetInstance(UUID pidNameSpace, ResultSet prsObject)
		throws JewelPetriException
	{
	    try
	    {
			return (PNStep)Engine.GetWorkInstance(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNStep), prsObject);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public void Initialize()
		throws JewelEngineException
	{
		IController[] larrAux;
		IEntity lrefNode;
		MasterDB ldb;
		int[] larrMembers;
		java.lang.Object[] larrParams;
		ResultSet lrsNodes;
		int i;

		larrMembers = new int[2];
		larrMembers[0] = Constants.FKProcess_In_Node;
		larrMembers[1] = Constants.FKController_In_Node;
		larrParams = new java.lang.Object[2];
		larrParams[0] = (UUID)getAt(0);

		try
		{
			mrefProcess = (IProcess)PNProcess.GetInstance(getNameSpace(), (UUID)getAt(0));
			mrefOperation = (IOperation)PNOperation.GetInstance(getNameSpace(), (UUID)getAt(1));
			lrefNode = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNNode));

			ldb = new MasterDB();

			larrAux = mrefOperation.getInputs();
			marrInputs = new INode[larrAux.length];

			for ( i = 0; i < larrAux.length; i++ )
			{
				marrInputs[i] = null;
				larrParams[1] = larrAux[i].getKey();
				lrsNodes = lrefNode.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
				if ( lrsNodes.next() )
				{
					marrInputs[i] = (INode)PNNode.GetInstance(getNameSpace(), lrsNodes);
					if ( lrsNodes.next() )
						marrInputs[i] = null;
				}
				if ( marrInputs[i] == null )
					throw new JewelEngineException("Database is inconsistent: Unexpected number of nodes for controller in process.");
				lrsNodes.close();
			}

			larrAux = mrefOperation.getOutputs();
			marrOutputs = new INode[larrAux.length];

			for ( i = 0; i < larrAux.length; i++ )
			{
				marrOutputs[i] = null;
				larrParams[1] = larrAux[i].getKey();
				lrsNodes = lrefNode.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
				if ( lrsNodes.next() )
				{
					marrOutputs[i] = (INode)PNNode.GetInstance(getNameSpace(), lrsNodes);
					if ( lrsNodes.next() )
						marrOutputs[i] = null;
				}
				if ( marrOutputs[i] == null )
					throw new JewelEngineException("Database is inconsistent: Unexpected number of nodes for controller in process.");
				lrsNodes.close();
			}
		
			ldb.Disconnect();
		}
		catch (JewelEngineException e)
		{
			throw e;
		}
		catch (Throwable e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}
	}

	public UUID GetProcessID()
	{
		return (UUID)getAt(0);
	}

	public IProcess GetProcess()
	{
		return mrefProcess;
	}

	public UUID GetOperationID()
	{
		return (UUID)getAt(1);
	}

	public IOperation GetOperation()
	{
		return mrefOperation;
	}

	public INode[] getInputs()
	{
		return marrInputs;
	}

	public INode[] getOutputs()
	{
		return marrOutputs;
	}
}
