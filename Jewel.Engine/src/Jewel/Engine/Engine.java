package Jewel.Engine;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import Jewel.Engine.Constants.EntityGUIDs;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Implementation.NameSpace;
import Jewel.Engine.Interfaces.IEngineImpl;
import Jewel.Engine.Interfaces.INameSpace;
import Jewel.Engine.SysObjects.Cache;
import Jewel.Engine.SysObjects.FileXfer;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.JewelWorkerThread;
import Jewel.Engine.SysObjects.ObjectBase;

public class Engine
{
    private static IEngineImpl grefEngine;

    private Engine()
    {
    }

    public static void InitEngine(IEngineImpl prefEngine)
    	throws JewelEngineException
    {
        grefEngine = prefEngine;

        grefEngine.ResetGlobalCache();
    }

    public static UUID FindEntity(UUID pidNSpace, UUID pidObject)
    	throws JewelEngineException
    {
        return grefEngine.GetCache(true).FindEntity(pidNSpace, pidObject);
    }

    public static UUID InternalFindEntity(UUID pidNSpace, UUID pidObject)
    	throws JewelEngineException
    {
        int[] larrMembers;
        java.lang.Object[] larrParams;
        MasterDB ldb;
        ResultSet lrs;
        UUID lidAux;
        INameSpace lrefParent;

        larrMembers = new int[2];
        larrMembers[0] = 0;
        larrMembers[1] = 1;
        larrParams = new java.lang.Object[2];
        larrParams[0] = pidNSpace;
        larrParams[1] = pidObject;

        try {
			ldb = new MasterDB();
	        lrs = Entity.GetInstance(EntityGUIDs.E_Entity).SelectByMembers(ldb, larrMembers, larrParams, null);
	        if (lrs.next())
	            lidAux = UUID.fromString(lrs.getString(1));
	        else
	            lidAux = null;
	        lrs.close();
	        ldb.Disconnect();
		}
        catch (Throwable e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
		}

        if (lidAux == null)
        {
            lrefParent = NameSpace.GetInstance(pidNSpace).getParent();
            if (lrefParent != null)
                lidAux = FindEntity(lrefParent.getKey(), pidObject);
        }

        return lidAux;
    }

    public static ObjectBase GetWorkInstance(UUID pidEntity, UUID pidKey)
    	throws InvocationTargetException, JewelEngineException
    {
        if (pidKey == null)
            return Entity.GetInstance(pidEntity).CreateNew();

        return grefEngine.GetCache(false).getAt(pidEntity, pidKey);
    }

    public static ObjectBase GetWorkInstance(UUID pidEntity, ResultSet prsObject)
    	throws SQLException, JewelEngineException
    {
        return grefEngine.GetCache(false).getAt(pidEntity, prsObject);
    }

    public static void ResetCache(UUID pidNameSpace)
    	throws JewelEngineException
    {
        grefEngine.ResetGlobalCache();
    }

    public static void UnloadEngine(UUID pidNameSpace)
    {
        grefEngine.UnloadEngine();
    }

    public static Cache GetCache(boolean pbGlobal)
    	throws JewelEngineException
    {
        return grefEngine.GetCache(pbGlobal);
    }

    public static void ResetGlobalCache()
    	throws JewelEngineException
    {
        grefEngine.ResetGlobalCache();
    }

    public static UUID getCurrentUser()
    {
        return grefEngine.getCurrentUser();
    }

    public static UUID getCurrentNameSpace()
    {
        return grefEngine.getCurrentNameSpace();
    }

    public static void pushNameSpace(UUID pidNameSpace)
    	throws JewelEngineException
	{
    	grefEngine.pushNameSpace(pidNameSpace);
	}

    public static void popNameSpace()
    	throws JewelEngineException
	{
    	grefEngine.popNameSpace();
	}

    public static Map<String, java.lang.Object> getUserData()
    {
        return grefEngine.getUserData();
    }

    public static String getCurrentPath()
    {
        return grefEngine.getCurrentPath();
    }

    public static void UnloadEngine()
    {
        grefEngine.UnloadEngine();
    }

    public static void OutputFile(FileXfer pobjFile)
    {
        grefEngine.OutputFile(pobjFile);
    }

    public static JewelWorkerThread getThread(Runnable prefThread)
    {
    	return grefEngine.getThread(prefThread);
    }
}
