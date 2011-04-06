package Jewel.Petri.Interfaces;

import java.util.UUID;

import Jewel.Engine.Interfaces.IJewelBase;
import Jewel.Petri.SysObjects.JewelPetriException;

public interface IProcess
	extends IJewelBase
{
    public UUID GetScriptID();
    public IScript GetScript() throws JewelPetriException;
    public IStep[] GetValidSteps();
	public boolean Lock();
	public void Unlock();
}
