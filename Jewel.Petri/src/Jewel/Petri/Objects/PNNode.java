package Jewel.Petri.Objects;

import java.sql.ResultSet;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;
import Jewel.Petri.Interfaces.INode;
import Jewel.Petri.SysObjects.JewelPetriException;

public class PNNode
	extends ObjectBase
	implements INode
{
	private int mlngCount;
	private int mlngTryCount;

    public static PNNode GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNNode)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNNode), pidKey);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

    public static PNNode GetInstance(UUID pidNameSpace, ResultSet prsObject)
		throws JewelPetriException
	{
	    try
	    {
			return (PNNode)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNNode), prsObject);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public void Initialize()
		throws JewelEngineException
	{
		mlngCount = (Integer)getAt(2);
		mlngTryCount = 0;
	}

	public void Reset()
	{
		mlngTryCount = 0;
	}

	public void TryDecCount()
	{
		mlngTryCount++;
	}

	public boolean CheckCount()
	{
		return (mlngCount >= mlngTryCount);
	}
}
