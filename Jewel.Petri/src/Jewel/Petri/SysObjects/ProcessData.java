package Jewel.Petri.SysObjects;

import java.util.*;

import Jewel.Engine.SysObjects.*;

public abstract class ProcessData
	extends ObjectBase
{
	public UUID GetProcessID()
	{
		return (UUID)getAt(0);
	}
}
