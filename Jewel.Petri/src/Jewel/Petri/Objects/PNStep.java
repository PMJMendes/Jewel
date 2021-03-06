package Jewel.Petri.Objects;

import java.sql.ResultSet;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.DataAccess.SQLServer;
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
			return (PNStep)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNStep), pidKey);
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
			return (PNStep)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNStep), prsObject);
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

		try
		{
			ldb = new MasterDB();
		}
		catch (Throwable e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}

		try
		{
			SetupNodes((IProcess)PNProcess.GetInstance(getNameSpace(), (UUID)getAt(0)), ldb);
		}
		catch (Throwable e)
		{
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			throw new JewelEngineException(e.getMessage(), e);
		}

		try
		{
			ldb.Disconnect();
		}
		catch (Throwable e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}
	}

	public void SetupNodes(IProcess prefProcess, SQLServer pdb)
		throws JewelPetriException
	{
		IController[] larrCtls;
		INode[] larrNodes;
		int i, j;

		mrefProcess = prefProcess;

		try
		{
			mrefOperation = (IOperation)PNOperation.GetInstance(getNameSpace(), (UUID)getAt(1));
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}

		larrNodes = mrefProcess.GetNodes(pdb);

		larrCtls = mrefOperation.getInputs();
		marrInputs = new INode[larrCtls.length];
		for ( i = 0; i < larrCtls.length; i++ )
		{
			marrInputs[i] = null;
			for ( j = 0; j < larrNodes.length; j++ )
			{
				if ( larrNodes[j].GetControllerID().equals(larrCtls[i].getKey()) )
				{
					if ( marrInputs[i] != null )
					{
						marrInputs[i] = null;
						break;
					}
					marrInputs[i] = larrNodes[j];
				}
			}
			if ( marrInputs[i] == null )
				throw new JewelPetriException("Database is inconsistent: Unexpected number of nodes for controller in process.");
		}

		larrCtls = mrefOperation.getOutputs();
		marrOutputs = new INode[larrCtls.length];
		for ( i = 0; i < larrCtls.length; i++ )
		{
			marrOutputs[i] = null;
			for ( j = 0; j < larrNodes.length; j++ )
			{
				if ( larrNodes[j].GetControllerID().equals(larrCtls[i].getKey()) )
				{
					if ( marrOutputs[i] != null )
					{
						marrOutputs[i] = null;
						break;
					}
					marrOutputs[i] = larrNodes[j];
				}
			}
			if ( marrOutputs[i] == null )
				throw new JewelPetriException("Database is inconsistent: Unexpected number of nodes for controller in process.");
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

	public UUID GetLevel()
	{
		return (UUID)getAt(2);
	}

	public void CalcRunnable(SQLServer pdb)
		throws JewelPetriException
	{
		boolean b;
		int i;

		for ( i = 0; i < marrInputs.length; i++ )
			marrInputs[i].PrepCount();

		for ( i = 0; i < marrInputs.length; i++ )
			marrInputs[i].TryDecCount();

		b = true;
		for ( i = 0; b && i < marrInputs.length; i++ )
			b = marrInputs[i].CheckCount();

		if ( b )
		{
			if ( Constants.LevelID_Invalid.equals(GetLevel()) )
			{
				internalSetAt(2, GetOperation().getDefaultLevel());
				b = true;
			}
			else
				b = false;
		}
		else
		{
			if ( !Constants.LevelID_Invalid.equals(GetLevel()) )
			{
				internalSetAt(2, Constants.LevelID_Invalid);
				b = true;
			}
			else
				b = false;
		}

		if ( b )
		{
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

	public void DoSafeRun()
		throws JewelPetriException
	{
		int i;

		for ( i = 0; i < marrInputs.length; i++ )
			marrInputs[i].DecCount();

		for ( i = 0; i < marrOutputs.length; i++ )
			marrOutputs[i].IncCount();

		for ( i = 0; i < marrInputs.length; i++ )
			marrInputs[i].DoSafeSave();

		for ( i = 0; i < marrOutputs.length; i++ )
			marrOutputs[i].DoSafeSave();
	}

	public void RollbackSafeRun()
	{
		int i;

		for ( i = 0; i < marrInputs.length; i++ )
			marrInputs[i].RollbackSafeSave();

		for ( i = 0; i < marrOutputs.length; i++ )
			marrOutputs[i].RollbackSafeSave();
	}

	public void CommitSafeRun(SQLServer pdb)
		throws JewelPetriException
	{
		int i;

		for ( i = 0; i < marrInputs.length; i++ )
			marrInputs[i].CommitSafeSave(pdb);

		for ( i = 0; i < marrOutputs.length; i++ )
			marrOutputs[i].CommitSafeSave(pdb);
	}

	public UUID GetRole()
	{
		return GetOperation().GetRole();
	}
}
