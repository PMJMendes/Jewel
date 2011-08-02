package Jewel.Petri.Objects;

import java.sql.ResultSet;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Petri.Constants;
import Jewel.Petri.Interfaces.IPermission;
import Jewel.Petri.SysObjects.JewelPetriException;

public class PNPermission
	extends ObjectBase
	implements IPermission
{
    public static PNPermission GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNPermission)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNPermission), pidKey);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public static PNPermission GetInstance(UUID pidNameSpace, ResultSet prsObject)
		throws JewelPetriException
	{
	    try
	    {
			return (PNPermission)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNPermission), prsObject);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public void Initialize() throws JewelEngineException
	{
	}

	public UUID getOperation()
	{
		return (UUID)getAt(0);
	}

	public UUID getProfile()
	{
		return (UUID)getAt(1);
	}
}
