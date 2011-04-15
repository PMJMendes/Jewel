package Jewel.Petri.Interfaces;

import java.util.UUID;

import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.Interfaces.IJewelBase;
import Jewel.Petri.SysObjects.JewelPetriException;

public interface IProcess
	extends IJewelBase
{
    public UUID GetScriptID();
    public IScript GetScript() throws JewelPetriException;
    public INode[] GetNodes();
    public IStep[] GetValidSteps();
    public IStep GetOperation(UUID pidOperation) throws JewelPetriException;
	public boolean Lock();
	public void Unlock();
	public void RecalcSteps(SQLServer pdb) throws JewelPetriException;
	public void RemoveStep(SQLServer pdb, IStep pobjStep) throws JewelPetriException;
}
