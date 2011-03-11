package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class Report
	extends ObjectBase
	implements IReport
{
    private IObject mrefOwner;

    public static Report GetInstance(UUID pidKey)
    	throws JewelEngineException
	{
		return (Report)Engine.GetCache(true).getAt(EntityGUIDs.E_Report, pidKey);
	}

	public static Report GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
		return (Report)Engine.GetCache(true).getAt(EntityGUIDs.E_Report, prsObject);
	}

    public void Initialize()
    	throws JewelEngineException
    {
        mrefOwner = Object.GetInstance((UUID)getAt(0));
    }

    public UUID getParamForm()
    {
        return (UUID)getAt(2);
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
		return (String)getAt(1);
	}
}
