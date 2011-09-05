package Jewel.Petri.Objects;

import java.sql.ResultSet;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Petri.Constants;
import Jewel.Petri.Interfaces.IController;
import Jewel.Petri.Interfaces.INode;
import Jewel.Petri.SysObjects.JewelPetriException;

public class PNNode
	extends ObjectBase
	implements INode
{
	private IController mrefController;
	private int mlngCount;
	private int mlngTryCount;
	private boolean mbSaved;
	private int mlngTmpCount;
	private boolean mbPending;

    public static PNNode GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNNode)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNNode), pidKey);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

    public static PNNode GetInstance(UUID pidNameSpace, ResultSet prsObject)
		throws JewelPetriException
	{
	    try
	    {
			return (PNNode)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNNode), prsObject);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public void Initialize()
		throws JewelEngineException
	{
		Reset();
	}

	public UUID GetControllerID()
	{
		return (UUID)getAt(1);
	}

	public IController GetController()
		throws JewelPetriException
	{
		if ( mrefController == null )
			mrefController = (IController)PNController.GetInstance(getNameSpace(), (UUID)getAt(1));

		return mrefController;
	}

	public void PrepCount()
	{
		mlngTryCount = 0;
	}

	public void TryDecCount()
	{
		mlngTryCount++;
	}

	public boolean CheckCount()
	{
		return (mlngCount >= mlngTryCount);
	}

	public synchronized void DecCount()
		throws JewelPetriException
	{
		if ( mbPending )
			throw new JewelPetriException("Unexpected: Attempted operation while node is pending.");

		if ( mlngCount <= 0 )
			throw new JewelPetriException("Invalid: Node is empty.");

		mlngCount--;
		mbSaved = false;
	}

	public void IncCount()
		throws JewelPetriException
	{
		if ( mbPending )
			throw new JewelPetriException("Unexpected: Attempted operation while node is pending.");

		if ( mlngCount < GetController().getMaxCount() )
			mlngCount++;

		mbSaved = false;
	}

	public void DoSafeSave()
		throws JewelPetriException
	{
		if ( mbPending )
			throw new JewelPetriException("Unexpected: Attempted operation while node is pending.");

		if ( mbSaved )
			return;

		internalSetAt(2, mlngCount);

		mbSaved = true;
		mbPending = true;
	}

	public void RollbackSafeSave()
	{
		if ( !mbPending )
			return;

		internalSetAt(2, mlngTmpCount);
		Reset();
	}

	public void CommitSafeSave(SQLServer pdb)
		throws JewelPetriException
	{
		if ( !mbPending )
			return;

		try
		{
			SaveToDb(pdb);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}

		Reset();
	}

	private void Reset()
	{
		mlngCount = (Integer)getAt(2);
		mlngTmpCount = mlngCount;
		mlngTryCount = 0;
		mbSaved = true;
		mbPending = false;
	}
}
