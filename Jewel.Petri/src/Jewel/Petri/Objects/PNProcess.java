package Jewel.Petri.Objects;

import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;

public class PNProcess
	extends ObjectBase
{
    public static PNProcess GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNProcess)Engine.GetWorkInstance(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNProcess), pidKey);
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
