package Jewel.Petri.Interfaces;

import java.util.UUID;

import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.Interfaces.IJewelBase;
import Jewel.Petri.SysObjects.JewelPetriException;

public interface IStep
	extends IJewelBase
{
    public UUID GetProcessID();
    public IProcess GetProcess();
    public UUID GetOperationID();
	public IOperation GetOperation();
    public INode[] getInputs();
    public INode[] getOutputs();
    public boolean IsRunnable();
    public void DoSafeRun() throws JewelPetriException;
	public void RollbackSafeRun();
	public void CommitSafeRun(SQLServer pdb) throws JewelPetriException;
    public void Delete(SQLServer pdb) throws JewelPetriException;
    public UUID GetRole();
}
