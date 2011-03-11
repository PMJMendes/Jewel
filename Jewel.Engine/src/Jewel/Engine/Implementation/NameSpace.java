package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class NameSpace
	extends ObjectBase
	implements INameSpace
{
    NameSpace mrefParent;
    IApplication mrefApp;

	public static NameSpace GetInstance(UUID pidKey)
		throws JewelEngineException
	{
		return (NameSpace)Engine.GetCache(true).getAt(EntityGUIDs.E_NameSpace, pidKey);
    }

	public static NameSpace GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
		return (NameSpace)Engine.GetCache(true).getAt(EntityGUIDs.E_NameSpace, prsObject);
	}

	public void Initialize()
		throws JewelEngineException
	{
        if (getAt(3) != null)
            mrefParent = NameSpace.GetInstance((UUID)getAt(3));

        mrefApp = Application.GetInstance((UUID)getAt(4));
	}

	public String Storage()
	{
		return "[" + (String)getAt(2) + "]";
	}

	public String getName()
	{
		return (String)getAt(0);
	}

    public INameSpace getParent()
    {
        return mrefParent;
    }

    public String getAssembly()
    {
        return mrefApp.getAssemblyName();
    }

    public String getStaticClass()
    {
        return mrefApp.getStaticClass();
    }

    public String getLoginMethod()
    {
        return mrefApp.getLoginMethod();
    }

    public void DoLogin(UUID pidUser)
    {
        DoLogin(getKey(), pidUser);
    }

    public void CreateInDB(java.lang.Object[] parrParams)
    	throws SQLException
    {
        MasterDB ldb;
        String lstrDBUser;

        if (!getNameSpace().equals(NameSpaceGUIDs.N_MADDS))
            return;

        lstrDBUser = (String)parrParams[2];

        ldb = new MasterDB();
        ldb.ExecuteDDL(getNameSpace(), "CREATE SCHEMA " + lstrDBUser + " AUTHORIZATION madds;");
        ldb.Disconnect();
    }

    public void DoLogin(UUID pidOriginalSpace, UUID pidUser)
    {
        if ((getAssembly() != null) && (getStaticClass() != null) && (getLoginMethod() != null))
            CodeExecuter.ExecuteLogin(getAssembly(), getStaticClass(), getLoginMethod(), pidOriginalSpace, pidUser);

        if (mrefParent != null)
            mrefParent.DoLogin(pidOriginalSpace, pidUser);
    }
}
