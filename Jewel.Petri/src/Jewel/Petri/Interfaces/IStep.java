package Jewel.Petri.Interfaces;

import java.util.UUID;

import Jewel.Engine.Interfaces.IJewelBase;

public interface IStep
	extends IJewelBase
{
    public UUID GetProcessID();
    public IProcess GetProcess();
    public UUID GetOperationID();
	public IOperation GetOperation();
    public INode[] getInputs();
    public INode[] getOutputs();
}
