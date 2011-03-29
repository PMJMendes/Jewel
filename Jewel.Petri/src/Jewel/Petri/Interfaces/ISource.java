package Jewel.Petri.Interfaces;

import java.util.*;

import Jewel.Engine.Interfaces.*;
import Jewel.Petri.*;

public interface ISource
	extends IJewelBase
{
    public UUID GetOperationID();
    public IOperation GetOperation() throws JewelPetriException;
    public UUID GetControllerID();
    public IController GetController() throws JewelPetriException;
}
