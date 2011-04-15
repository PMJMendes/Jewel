package Jewel.Petri.Objects;

import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Petri.Constants;
import Jewel.Petri.SysObjects.JewelPetriException;

public class PNLog
	extends ObjectBase
{
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
	public void Initialize()
		throws JewelEngineException
	{
	}
}
