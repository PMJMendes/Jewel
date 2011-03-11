package Jewel.Engine.Implementation;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class FormAction
	extends ObjectBase
	implements IFormAction
{
    private IForm mrefOwner;
    private IDynaCode mrefSourceCode;
    private Method mrefMethod;

    private static Class<?>[] garrTypes = {UUID.class, java.lang.Object[].class};

    public static FormAction GetInstance(UUID pidKey)
    	throws JewelEngineException
	{
		return (FormAction)Engine.GetCache(true).getAt(EntityGUIDs.E_FormAction, pidKey);
	}

	public static FormAction GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
		return (FormAction)Engine.GetCache(true).getAt(EntityGUIDs.E_FormAction, prsObject);
	}

	public void Initialize()
		throws JewelEngineException
	{
        mrefOwner = (IForm)Form.GetInstance((UUID)getAt(0));

		try
		{
			if ((getAt(5) != null) && (mrefOwner.getEditedObject() != null))
				mrefSourceCode = mrefOwner.getEditedObject().GetActionAt((Integer)getAt(5));

			if ((mrefOwner.getClassType() != null) && (getAt(4) != null))
				mrefMethod = mrefOwner.getClassType().getMethod((String)getAt(4), garrTypes);
		}
		catch (Throwable e)
		{
			throw new JewelEngineException("Unexpected exception in FormAction Initialize", e);
		}
    }

	public String getName()
	{
		return (String)getAt(2);
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

    public int getIndex()
    {
        if (getAt(5) != null)
            return (Integer)getAt(5);

        return -1;
    }

    public void Run(ObjectBase pobjSource, java.lang.Object[] parrParams)
    	throws InvocationTargetException, JewelEngineException
    {
        if (mrefSourceCode != null)
            mrefSourceCode.Run(pobjSource, parrParams);
    }

    public void Run(UUID pidNameSpace, java.lang.Object[] parrParams)
    	throws InvocationTargetException, JewelEngineException
    {
        java.lang.Object[] larrAux;

        if (mrefMethod != null)
        {
            larrAux = new java.lang.Object[2];
            larrAux[0] = pidNameSpace;
            larrAux[1] = parrParams;

            try
            {
				mrefMethod.invoke(null, larrAux);
			}
            catch (InvocationTargetException e)
            {
            	throw e;
			}
            catch (Exception e)
            {
            	throw new JewelEngineException("Unexpected exception in FormAction Run", e);
			}
        }
    }
}
