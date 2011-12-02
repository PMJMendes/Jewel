package Jewel.Petri.Interfaces;

import java.util.UUID;

import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.Interfaces.IJewelBase;
import Jewel.Petri.SysObjects.JewelPetriException;

public interface IStep
	extends IJewelBase
{
	public void SetupNodes(IProcess prefProcess, SQLServer pdb) throws JewelPetriException;
    public UUID GetProcessID();
    public IProcess GetProcess();
    public UUID GetOperationID();
	public IOperation GetOperation();
    public INode[] getInputs();
    public INode[] getOutputs();
    public UUID GetLevel();
    public void CalcRunnable(SQLServer pdb) throws JewelPetriException;
    public void DoSafeRun() throws JewelPetriException;
	public void RollbackSafeRun();
	public void CommitSafeRun(SQLServer pdb) throws JewelPetriException;
    public UUID GetRole();
}
