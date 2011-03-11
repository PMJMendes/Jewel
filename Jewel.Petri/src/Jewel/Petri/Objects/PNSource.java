package Jewel.Petri.Objects;

import java.util.UUID;

import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;

public class PNSource
	extends ObjectBase
{
	public void Initialize()
		throws JewelEngineException
	{
	}

    public String OnValidate(java.lang.Object[] parrData)
    {
    	UUID lidOScript, lidCScript;

		if ( (parrData[0] == null) || (parrData[1] == null) )
			return "";

    	try
    	{
    		lidOScript = PNOperation.GetInstance(getNameSpace(), (UUID)parrData[0]).GetScriptID();
    		lidCScript = PNController.GetInstance(getNameSpace(), (UUID)parrData[1]).GetScriptID();
		}
    	catch (Throwable e)
    	{
    		throw new RuntimeException(e.getMessage(), e);
		}

		if ( !(lidOScript.equals(lidCScript)) )
			return "Operation and Controller not in the same script.";

    	return "";
    }

    public UUID GetOperationID()
    {
    	return (UUID)getAt(0);
    }

    public PNOperation GetOperation()
    	throws JewelPetriException
    {
		return PNOperation.GetInstance(getNameSpace(), GetOperationID());
    }

    public UUID GetControllerID()
    {
    	return (UUID)getAt(1);
    }

    public PNController GetController()
    	throws JewelPetriException
    {
		return PNController.GetInstance(getNameSpace(), GetControllerID());
    }
}
