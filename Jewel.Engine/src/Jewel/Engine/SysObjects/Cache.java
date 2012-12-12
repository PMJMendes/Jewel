package Jewel.Engine.SysObjects;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.Constants.DBConstants;
import Jewel.Engine.Constants.EntityGUIDs;
import Jewel.Engine.Constants.GUIDArrays;
import Jewel.Engine.Implementation.Application;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Implementation.NameSpace;
import Jewel.Engine.Implementation.ObjMember;
import Jewel.Engine.Implementation.TypeDef;

public class Cache
{
    private static class TypeMapper
    {
        private Hashtable<UUID, Class<?>> garrGlobalTypes;

        public TypeMapper()
        {
            garrGlobalTypes = new Hashtable<UUID, Class<?>>();

            garrGlobalTypes.put(EntityGUIDs.E_Object,      Jewel.Engine.Implementation.Object.class);
            garrGlobalTypes.put(EntityGUIDs.E_NameSpace,   NameSpace.class);
            garrGlobalTypes.put(EntityGUIDs.E_Entity,      Entity.class);
            garrGlobalTypes.put(EntityGUIDs.E_TypeDef,     TypeDef.class);
            garrGlobalTypes.put(EntityGUIDs.E_ObjMember,   ObjMember.class);
            garrGlobalTypes.put(EntityGUIDs.E_Application, Application.class);
        }

        public Class<?> Map(UUID pidKey)
        {
            return garrGlobalTypes.get(pidKey);
        }
    }

    private HashMap<UUID, CacheElement> marrElements;
	private HashMap<UUID, Constructor<?>> marrConstructors;
    private HashMap<String, UUID> marrEntities;
	private boolean mbIsGlobal;

    private static Class<?>[] garrTypes = {};
    private static java.lang.Object[] garrParams = {};
    private static TypeMapper garrMap = new TypeMapper();

    private class CacheElement
    {
    	private UUID midEntity;
    	private UUID midKey;
    	private ObjectBase mobjObject;

    	public CacheElement(UUID pidEntity, UUID pidKey)
    	{
    		midEntity = pidEntity;
    		midKey = pidKey;
    	}

    	public ObjectBase GetObjectFast()
    	{
    		return mobjObject;
    	}

    	public synchronized ObjectBase GetObject(ResultSet prsObject, boolean pbDoInit)
    		throws JewelEngineException
    	{
    		Class<?> lrefTheType;
            Constructor<?> lrefConst;

    		if ( mobjObject == null )
    		{
                lrefConst = marrConstructors.get(midEntity);

                try
                {
    	            if (lrefConst == null)
    	            {
    	                lrefTheType = garrMap.Map(midEntity);
    	                if (lrefTheType == null)
    	                    lrefTheType = Entity.GetInstance(midEntity).getDefObject().getClassType();
    	                if (lrefTheType == null)
    	                    lrefTheType = ObjectMaster.class;
    	                lrefConst = lrefTheType.getConstructor(garrTypes);
    	
    	                marrConstructors.put(midEntity, lrefConst);
    	            }

    	            mobjObject = (ObjectBase)lrefConst.newInstance(garrParams);

    	            if (prsObject == null)
    	            	mobjObject.LoadAt(midEntity, midKey);
    				else
    					mobjObject.LoadAt(midEntity, prsObject);
                }
                catch(JewelEngineException e)
                {
                	mobjObject = null;
                	throw e;
                }
                catch(Exception e)
                {
                	mobjObject = null;
                	throw new JewelEngineException("Unexpected error in inner Cache.GetObject", e);
                }

                if (mbIsGlobal)
                	mobjObject.SetReadonly();

    			if ( pbDoInit )
    				mobjObject.Initialize();
    		}

    		return mobjObject;
    	}

    	public synchronized void PutObject(ObjectBase value)
    	{
    		mobjObject = value;
            if (mbIsGlobal)
                value.SetReadonly();
    	}

    	public synchronized void DeleteObject()
    	{
    		mobjObject = null;
    	}
    }

    private synchronized CacheElement GetCacheElement(UUID pidEntity, UUID pidKey)
    {
    	CacheElement lobjAux;

    	lobjAux = marrElements.get(pidKey);

    	if ( lobjAux == null )
    	{
    		lobjAux = new CacheElement(pidEntity, pidKey);
    		marrElements.put(pidKey, lobjAux);
    	}

    	return lobjAux;
    }

    private ObjectBase GetObject(UUID pidEntity, UUID pidKey, ResultSet prsObject, boolean pbDoInit)
    	throws JewelEngineException
	{
    	CacheElement lobjAux;
    	ObjectBase lobjResult;

    	lobjAux = GetCacheElement(pidEntity, pidKey);

    	lobjResult = lobjAux.GetObjectFast();
    	if ( lobjResult == null )
    		lobjResult = lobjAux.GetObject(prsObject, pbDoInit);

    	return lobjResult;
	}

	public Cache(boolean pbIsGlobal)
	{
		marrElements = null;
        marrConstructors = null;
        marrEntities = null;
		mbIsGlobal = pbIsGlobal;
	}

    public synchronized void InitCache()
    	throws JewelEngineException
    {
        int i;

        if ( mbIsGlobal )
        {
        	if ( "1".equals(System.getenv(DBConstants.Env_LargeCache)) )
        	{
        		marrElements = new HashMap<UUID, CacheElement>(50000000);
            	marrConstructors = new HashMap<UUID, Constructor<?>>(1000);
        	}
        	else
        	{
        		marrElements = new HashMap<UUID, CacheElement>();
            	marrConstructors = new HashMap<UUID, Constructor<?>>();
        	}
        }
        else
        {
    		marrElements = new HashMap<UUID, CacheElement>();
        	marrConstructors = new HashMap<UUID, Constructor<?>>();
        }

        if (mbIsGlobal)
        {
            marrEntities = new HashMap<String, UUID>();

            for (i = 0; i < GUIDArrays.N_Entities; i++)
                Entity.RawCreate(GUIDArrays.A_Entities[i]);

            for (i = 0; i < GUIDArrays.N_Applications; i++)
                Application.GetInstance(GUIDArrays.A_Applications[i]);

            for (i = 0; i < GUIDArrays.N_NameSpaces; i++)
                NameSpace.GetInstance(GUIDArrays.A_NameSpaces[i]);

            for (i = 0; i < GUIDArrays.N_Objects; i++)
            	Jewel.Engine.Implementation.Object.RawCreate(GUIDArrays.A_Objects[i]);

            for (i = 0; i < GUIDArrays.N_TypeDefs; i++)
                TypeDef.GetInstance(GUIDArrays.A_TypeDefs[i]);

            for (i = 0; i < GUIDArrays.N_ObjectMembers; i++)
                ObjMember.GetInstance(GUIDArrays.A_ObjMembers[i]);

            for (i = 0; i < GUIDArrays.N_Entities; i++)
                Entity.GetInstance(GUIDArrays.A_Entities[i]).Initialize();

            for (i = 0; i < GUIDArrays.N_Objects; i++)
                Jewel.Engine.Implementation.Object.GetInstance(GUIDArrays.A_Objects[i]).Initialize();
        }
    }

	public ObjectBase getAt(UUID pidEntity, UUID pidKey)
		throws JewelEngineException
	{
		return GetObject(pidEntity, pidKey, null, true);
	}

	public void setAt(UUID pidEntity, UUID pidKey, ObjectBase value)
	{
		GetCacheElement(pidEntity, pidKey).PutObject(value);
	}

	public ObjectBase getAt(UUID pidEntity, ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
        return GetObject(pidEntity, UUID.fromString(prsObject.getString(1)), prsObject, true);
	}

	public ObjectBase GetNoInit(UUID pidEntity, UUID pidKey)
		throws JewelEngineException
	{
		return GetObject(pidEntity, pidKey, null, false);
	}

	public void DeleteAt(UUID pidEntity, UUID pidKey)
	{
		GetCacheElement(pidEntity, pidKey).DeleteObject();
	}

    public UUID FindEntity(UUID pidNSpace, UUID pidObject)
    	throws JewelEngineException
    {
    	synchronized(this)
    	{
    		if ( marrEntities == null )
    			throw new JewelEngineException("Unexpected: Cache not initialized!");
    	}

    	synchronized(marrEntities)
    	{
            java.lang.Object lobjAux;
            UUID lidAux;

            lobjAux = marrEntities.get(pidNSpace.toString() + "." + pidObject.toString());
            if (lobjAux != null)
                return (UUID)lobjAux;

            lidAux = Engine.InternalFindEntity(pidNSpace, pidObject);

            if ( lidAux != null )
            	marrEntities.put(pidNSpace.toString() + "." + pidObject.toString(), lidAux);

            return lidAux;
    	}
    }
}
