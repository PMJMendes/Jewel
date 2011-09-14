package Jewel.Petri.SysObjects;

import java.util.*;

import Jewel.Engine.SysObjects.*;

public abstract class ProcessData
	extends ObjectBase
{
	public abstract UUID GetProcessID();
	public abstract void SetProcessID(UUID pidProcess);
}
