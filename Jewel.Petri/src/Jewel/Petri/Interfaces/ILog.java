package Jewel.Petri.Interfaces;

import java.sql.Timestamp;
import java.util.UUID;

import Jewel.Engine.Interfaces.IJewelBase;
import Jewel.Engine.Interfaces.IUser;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Petri.SysObjects.JewelPetriException;
import Jewel.Petri.SysObjects.Operation;

public interface ILog
	extends IJewelBase
{
	Operation GetOperationData() throws JewelPetriException;
	IOperation GetOperation() throws JewelEngineException;
	UUID GetProcessID();
	IUser GetUser() throws JewelPetriException;
	Timestamp GetTimestamp();
	boolean CanUndo() throws JewelPetriException;
	UUID GetExternalProcess();
}
