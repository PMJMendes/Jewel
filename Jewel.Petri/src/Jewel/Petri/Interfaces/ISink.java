package Jewel.Petri.Interfaces;

import java.util.*;

import Jewel.Engine.Interfaces.*;
import Jewel.Petri.SysObjects.*;

public interface ISink
	extends IJewelBase
{
    public UUID GetControllerID();
    public IController GetController() throws JewelPetriException;
    public UUID GetOperationID();
    public IOperation GetOperation() throws JewelPetriException;
}
