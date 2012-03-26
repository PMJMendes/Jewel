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
    public INode[] GetNodes(SQLServer pdb) throws JewelPetriException;
    public IStep[] GetSteps(SQLServer pdb) throws JewelPetriException;
    public IStep[] GetValidSteps(SQLServer pdb) throws JewelPetriException;
    public IStep GetOperation(UUID pidOperation, SQLServer pdb) throws JewelPetriException;
    public IStep GetValidOperation(UUID pidOperation) throws JewelPetriException;
    public IProcess GetParent() throws JewelPetriException;
    public UUID GetManagerID();
    public void SetManagerID(UUID pidManager, SQLServer pdb) throws JewelPetriException;
	public boolean Lock();
	public void Unlock();
	public void RecalcSteps(SQLServer pdb) throws JewelPetriException;
	public void Setup(SQLServer pdb, Operation.QueueContext pobjContext, boolean pbInitialize) throws JewelPetriException;
	public void RunAutoSteps(Operation.QueueContext pobjContext, SQLServer pdb) throws JewelPetriException;
	public UUID GetDataKey();
	public ObjectBase GetData() throws JewelPetriException;
	public boolean IsRunning();
	public void Restart(SQLServer pdb) throws JewelPetriException;
	public void Stop(SQLServer pdb) throws JewelPetriException;
	public void SetDataObjectID(UUID pidData, SQLServer pdb) throws JewelPetriException;
	public IProcess[] GetCurrentSubProcesses(SQLServer pdb) throws JewelPetriException;
	public void SetParentProcId(UUID pidParent, SQLServer pdb) throws JewelPetriException;
	public ILog GetLog(UUID pidOpCode) throws JewelPetriException;
}
