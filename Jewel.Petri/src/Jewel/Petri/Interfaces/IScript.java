package Jewel.Petri.Interfaces;

import java.util.UUID;

import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Engine.Interfaces.*;
import Jewel.Petri.SysObjects.*;

public interface IScript
	extends IJewelBase
{
	IOperation[] getOperations();
	IController[] getControllers();
	UUID GetDataType();
	boolean IsTopLevel();
	IProcess CreateInstance(UUID pidNSpace, UUID pidData, UUID pidParent, Operation.QueueContext pobjContext, SQLServer pdb)
			throws JewelPetriException;
}
