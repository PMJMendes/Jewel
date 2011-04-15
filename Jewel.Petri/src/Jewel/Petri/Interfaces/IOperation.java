package Jewel.Petri.Interfaces;

import java.util.*;

import Jewel.Engine.Interfaces.*;
import Jewel.Petri.SysObjects.*;

public interface IOperation
	extends IJewelBase
{
    public UUID GetScriptID();
    public IScript GetScript() throws JewelPetriException;
    public IController[] getInputs();
    public IController[] getOutputs();
    public UUID getDefaultLevel();
    public Operation GetNewInstance() throws JewelPetriException;
}
