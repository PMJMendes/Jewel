package Jewel.Petri.Objects;

import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;

public class PNNode
	extends ObjectBase
{
    public static PNNode GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNNode)Engine.GetWorkInstance(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNNode), pidKey);
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
