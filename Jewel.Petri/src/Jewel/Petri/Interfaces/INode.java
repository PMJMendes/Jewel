package Jewel.Petri.Interfaces;

import Jewel.Engine.Interfaces.IJewelBase;

public interface INode
	extends IJewelBase
{
	public void Reset();
	public void TryDecCount();
	public boolean CheckCount();
}
