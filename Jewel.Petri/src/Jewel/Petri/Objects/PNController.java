package Jewel.Petri.Objects;

import java.sql.ResultSet;
import java.util.*;

import Jewel.Engine.Engine;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;
import Jewel.Petri.Interfaces.*;
import Jewel.Petri.SysObjects.JewelPetriException;

public class PNController
	extends ObjectBase
	implements IController
{
    public static PNController GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNController)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNController), pidKey);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

    public static PNController GetInstance(UUID pidNameSpace, ResultSet prsObject)
    	throws JewelPetriException
    {
        try
        {
			return (PNController)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNController), prsObject);
		}
        catch (Throwable e)
        {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
    }

	public void Initialize()
		throws JewelEngineException
	{
	}

    public String OnValidate(java.lang.Object[] parrData)
    {
    	if ( parrData[2] == null )
    		return "";

    	if ( ((Integer)parrData[2]) < 0 )
    		return "Starting count must be non-negative.";

    	if ( ((Integer)parrData[3]) < 0 )
    		return "Maximum count must be non-negative.";

    	return "";
    }

    public UUID GetScriptID()
    {
    	return (UUID)getAt(1);
    }

    public PNScript GetScript()
    	throws JewelPetriException
    {
		return PNScript.GetInstance(getNameSpace(), GetScriptID());
    }

	public int getInitialCount()
	{
		return (Integer)getAt(2);
	}

	public int getMaxCount()
	{
		return (Integer)getAt(3);
	}
}
