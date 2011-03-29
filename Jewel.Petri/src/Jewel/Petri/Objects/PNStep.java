package Jewel.Petri.Objects;

import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;

public class PNStep
	extends ObjectBase
{
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

	public void Initialize()
		throws JewelEngineException
	{
	}
}
