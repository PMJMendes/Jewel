package Jewel.Engine.Implementation;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class DynaCode
	extends ObjectBase
	implements IDynaCode
{
        private IObject mrefOwner;
        private Method mrefMethod;

        private static Class<?>[] garrTypes = {java.lang.Object[].class};

        public static DynaCode GetInstance(UUID pidKey)
        	throws JewelEngineException
		{
			return (DynaCode)Engine.GetCache(true).getAt(EntityGUIDs.E_DynaCode, pidKey);
		}

		public static DynaCode GetInstance(ResultSet prsObject)
			throws SQLException, JewelEngineException
		{
			return (DynaCode)Engine.GetCache(true).getAt(EntityGUIDs.E_DynaCode, prsObject);
		}

		public void Initialize()
			throws JewelEngineException
		{
            mrefOwner = (IObject)Object.GetInstance((UUID)getAt(0));

            try
            {
				mrefMethod = mrefOwner.getClassType().getMethod((String)getAt(4), garrTypes);
			}
            catch (NullPointerException e)
            {
            	mrefMethod = null;
            }
            catch (SecurityException e)
            {
            	mrefMethod = null;
			}
            catch (NoSuchMethodException e)
            {
            	mrefMethod = null;
			}
        }

		public String getName()
		{
			return (String)getAt(2);
		}

        public int getIndex()
        {
            return (Integer)getAt(1);
        }

        public String getAssembly()
        {
            return mrefOwner.getAssembly();
        }

        public String getClassName()
        {
            return mrefOwner.getClassName();
        }

		public String getMethod()
		{
			return (String)getAt(4);
        }

        public void Run(ObjectBase pobjSource, java.lang.Object[] parrParams)
        	throws InvocationTargetException, JewelEngineException
        {
        	java.lang.Object[] larrAux;

            larrAux = new java.lang.Object[1];
            larrAux[0] = parrParams;

            try
            {
				mrefMethod.invoke(pobjSource, larrAux);
			}
            catch (InvocationTargetException e)
            {
            	throw e;
			}
            catch (Throwable e)
            {
            	throw new JewelEngineException("Unexpected exception in DynaCode Run", e);
			}
        }
}
