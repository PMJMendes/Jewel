package Jewel.Engine.Interfaces;

import java.lang.reflect.InvocationTargetException;

import Jewel.Engine.SysObjects.*;

public interface IDynaCode
	extends IJewelBase
{
		String getName();
        int getIndex();
        String getAssembly();
        String getClassName();
		String getMethod();
        void Run(ObjectBase pobjSource, java.lang.Object[] parrParams)
        	throws InvocationTargetException, JewelEngineException;
}
