package Jewel.Petri.Objects;

import java.util.*;

import Jewel.Engine.Engine;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;

public class PNController
	extends ObjectBase
{
    public static PNController GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNController)Engine.GetWorkInstance(Engine.FindEntity(pidNameSpace, Constants.GUID_PNController), pidKey);
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
}
