package Jewel.Petri.Objects;

import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;
import Jewel.Petri.Interfaces.IProcess;
import Jewel.Petri.SysObjects.JewelPetriException;

public class PNProcess
	extends ObjectBase
	implements IProcess
{
	private int mlngLock;

    public static PNProcess GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNProcess)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNProcess), pidKey);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public void Initialize()
		throws JewelEngineException
	{
		mlngLock = 0;
	}

	public synchronized boolean Lock()
	{
		if ( mlngLock > 0 )
			return false;
		mlngLock++;
		return true;
	}

	public synchronized void Unlock()
	{
		mlngLock = 0;
	}
}
