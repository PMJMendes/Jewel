package Jewel.Petri.Interfaces;

import Jewel.Engine.Interfaces.IJewelBase;

public interface IProcess
	extends IJewelBase
{
	public boolean Lock();
	public void Unlock();
}
