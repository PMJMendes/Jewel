package Jewel.Petri.Objects;

import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;
import Jewel.Petri.Interfaces.IProcess;
import Jewel.Petri.Interfaces.IScript;
import Jewel.Petri.Interfaces.IStep;
import Jewel.Petri.SysObjects.JewelPetriException;

public class PNProcess
	extends ObjectBase
	implements IProcess
{
	private IScript lrefScript;
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
		lrefScript = null;
		mlngLock = 0;
	}

	public UUID GetScriptID()
	{
		return (UUID)getAt(0);
	}

	public IScript GetScript()
		throws JewelPetriException
	{
		if ( lrefScript == null )
			lrefScript = (IScript)PNScript.GetInstance(getNameSpace(), (UUID)getAt(0));

		return lrefScript;
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

	public IStep[] GetValidSteps()
	{
		return null;
	}
}
