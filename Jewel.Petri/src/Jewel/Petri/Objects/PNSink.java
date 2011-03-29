package Jewel.Petri.Objects;

import java.sql.ResultSet;
import java.util.*;

import Jewel.Engine.Engine;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;
import Jewel.Petri.Interfaces.*;

public class PNSink
	extends ObjectBase
	implements ISink
{
    public static PNSink GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNSink)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNSink), pidKey);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public static PNSink GetInstance(UUID pidNameSpace, ResultSet prsObject)
		throws JewelPetriException
	{
	    try
	    {
			return (PNSink)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNSink), prsObject);
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
    	UUID lidCScript, lidOScript;

		if ( (parrData[0] == null) || (parrData[1] == null) )
			return "";

    	try
    	{
    		lidCScript = PNController.GetInstance(getNameSpace(), (UUID)parrData[0]).GetScriptID();
    		lidOScript = PNOperation.GetInstance(getNameSpace(), (UUID)parrData[1]).GetScriptID();
		}
    	catch (Throwable e)
    	{
    		throw new RuntimeException(e.getMessage(), e);
		}

		if ( !(lidOScript.equals(lidCScript)) )
			return "Operation and Controller not in the same script.";

    	return "";
    }

    public UUID GetControllerID()
    {
    	return (UUID)getAt(0);
    }

    public IController GetController()
    	throws JewelPetriException
    {
		return (IController)PNController.GetInstance(getNameSpace(), GetControllerID());
    }

    public UUID GetOperationID()
    {
    	return (UUID)getAt(1);
    }

    public IOperation GetOperation()
    	throws JewelPetriException
    {
		return (IOperation)PNOperation.GetInstance(getNameSpace(), GetOperationID());
    }
}
