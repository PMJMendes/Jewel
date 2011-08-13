package Jewel.Petri.Objects;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.Implementation.User;
import Jewel.Engine.Interfaces.IUser;
import Jewel.Engine.SysObjects.FileXfer;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Petri.Constants;
import Jewel.Petri.Interfaces.ILog;
import Jewel.Petri.Interfaces.IOperation;
import Jewel.Petri.SysObjects.JewelPetriException;
import Jewel.Petri.SysObjects.Operation;
import Jewel.Petri.SysObjects.UndoableOperation;

public class PNLog
	extends ObjectBase
	implements ILog
{
	private IOperation mrefOp;
	private IUser mrefUser;
	private Operation mobjData;

    public static PNLog GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
    		return (PNLog)Engine.GetWorkInstance(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNLog), pidKey);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

    public static PNLog GetInstance(UUID pidNameSpace, ResultSet prsObject)
		throws JewelPetriException
	{
	    try
	    {
			return (PNLog)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNLog), prsObject);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public void Initialize()
		throws JewelEngineException
	{
		try
		{
			mrefOp = PNOperation.GetInstance(getNameSpace(), (UUID)getAt(1));
		}
		catch (Throwable e)
		{
	    	throw new JewelEngineException(e.getMessage(), e);
		}
	}

	public Operation GetOperationData()
		throws JewelPetriException
	{
		if ( mobjData == null )
			mobjData = Operation.getOperation((getAt(6) instanceof FileXfer ? (FileXfer)getAt(6) : new FileXfer((byte[])getAt(6))));

		return mobjData;
	}

	public IOperation GetOperation()
	{
		return mrefOp;
	}

	public UUID GetProcessID()
	{
		return (UUID)getAt(0);
	}

	public IUser GetUser()
		throws JewelPetriException
	{
		if ( mrefUser == null )
		{
			try
			{
				mrefUser = ((IUser)User.GetInstance(getNameSpace(), (UUID)getAt(3)));
			}
			catch (Throwable e)
			{
		    	throw new JewelPetriException(e.getMessage(), e);
			}
		}

		return mrefUser;
	}

	public Timestamp GetTimestamp()
	{
		return (Timestamp)getAt(2);
	}

	public boolean CanUndo()
		throws JewelPetriException
	{
		if ( mrefOp.GetUndoOp() == null )
			return false;

		if ( !(GetOperationData() instanceof UndoableOperation) )
			return false;

		return !((Boolean)getAt(5));
	}
}
