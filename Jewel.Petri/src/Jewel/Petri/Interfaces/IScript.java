package Jewel.Petri.Interfaces;

import java.util.UUID;

import Jewel.Engine.Interfaces.*;
import Jewel.Petri.SysObjects.*;

public interface IScript
	extends IJewelBase
{
	IOperation[] getOperations();
	IController[] getControllers();
	UUID GetDataType(); 
	void CreateInstance(UUID pidNSpace, UUID pidData, UUID pidParent) throws JewelPetriException;
}
