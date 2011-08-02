package Jewel.Petri.Interfaces;

import java.util.UUID;

import Jewel.Engine.Interfaces.IJewelBase;
import Jewel.Petri.SysObjects.JewelPetriException;
import Jewel.Petri.SysObjects.Operation;

public interface ILog
	extends IJewelBase
{
	Operation GetOperationData() throws JewelPetriException;
	IOperation GetOperation();
	UUID GetProcessID();
}
