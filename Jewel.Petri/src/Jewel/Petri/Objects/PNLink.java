package Jewel.Petri.Objects;

import java.util.*;

import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;

public class PNLink
	extends ObjectBase
{
	public void Initialize()
		throws JewelEngineException
	{
	}

    public String OnValidate(java.lang.Object[] parrData)
    {
		if ( (parrData[0] == null) || (parrData[1] == null) )
			return "";

		if ( ((UUID)parrData[0]).equals((UUID)parrData[1]) )
			return "Parent and Child cannot be the same script.";

    	return "";
    }

    public UUID GetParentID()
    {
    	return (UUID)getAt(0);
    }

    public PNScript GetParent()
    	throws JewelPetriException
    {
		return PNScript.GetInstance(getNameSpace(), GetParentID());
    }

    public UUID GetChildID()
    {
    	return (UUID)getAt(1);
    }

    public PNScript GetChild()
    	throws JewelPetriException
    {
		return PNScript.GetInstance(getNameSpace(), GetChildID());
    }
}
