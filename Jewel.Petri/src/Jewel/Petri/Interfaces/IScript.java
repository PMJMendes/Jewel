package Jewel.Petri.Interfaces;

import Jewel.Engine.Interfaces.*;
import Jewel.Petri.*;

public interface IScript
	extends IJewelBase
{
	IOperation[] getOperations();
	IController[] getControllers();
	void CreateInstance(java.lang.Object[] parrParams) throws JewelPetriException;
}
