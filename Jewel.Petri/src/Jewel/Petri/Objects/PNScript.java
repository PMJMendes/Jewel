package Jewel.Petri.Objects;

import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;

public class PNScript
	extends ObjectBase
{
    public static PNScript GetInstance(UUID pidNameSpace, UUID pidKey)
    	throws JewelPetriException
	{
	    try
	    {
			return (PNScript)Engine.GetWorkInstance(Engine.FindEntity(pidNameSpace, Constants.GUID_PNScript), pidKey);
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
}
