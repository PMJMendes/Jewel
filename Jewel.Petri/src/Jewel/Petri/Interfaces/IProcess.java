package Jewel.Petri.Interfaces;

import java.util.UUID;

import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.Interfaces.IJewelBase;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Petri.SysObjects.JewelPetriException;
import Jewel.Petri.SysObjects.Operation;

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
	public void Setup(SQLServer pdb, Operation.QueueContext pobjContext, boolean pbInitialize) throws JewelPetriException;
	public void RunAutoSteps(Operation.QueueContext pobjContext, SQLServer pdb) throws JewelPetriException;
	public ObjectBase GetData() throws JewelPetriException;
	public boolean IsRunning();
	public void Restart(SQLServer pdb) throws JewelPetriException;
	public void Stop(SQLServer pdb) throws JewelPetriException;
}
