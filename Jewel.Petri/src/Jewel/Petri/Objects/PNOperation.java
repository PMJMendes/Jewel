package Jewel.Petri.Objects;

import java.util.*;

import Jewel.Engine.Engine;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;

public class PNOperation
	extends ObjectBase
{
    public static PNOperation GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNOperation)Engine.GetWorkInstance(Engine.FindEntity(pidNameSpace, Constants.GUID_PNOperation), pidKey);
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
