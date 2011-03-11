package Jewel.Engine.Interfaces;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import Jewel.Engine.SysObjects.*;

public interface IFormAction
	extends IJewelBase
{
	String getName();
	String getAssembly();
	String getClassName();
	String getMethod();
    int getIndex();
    void Run(ObjectBase pobjSource, Object[] parrParams) throws InvocationTargetException, JewelEngineException;
    void Run(UUID pidNameSpace, Object[] parrParams) throws InvocationTargetException, JewelEngineException;
}
