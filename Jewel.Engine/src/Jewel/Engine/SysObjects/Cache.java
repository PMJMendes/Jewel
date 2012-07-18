package Jewel.Engine.SysObjects;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Implementation.*;

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

    private Hashtable<String, ObjectBase> marrElements;
	private Hashtable<UUID, Constructor<?>> marrConstructors;
    private Hashtable<String, UUID> marrEntities;
	private boolean mbIsGlobal;

    private static Class<?>[] garrTypes = {};
    private static java.lang.Object[] garrParams = {};
    private static TypeMapper garrMap = new TypeMapper();

    private synchronized ObjectBase GetObject(UUID pidEntity, UUID pidKey, ResultSet prsObject, boolean pbDoInit)
    	throws JewelEngineException
	{
		ObjectBase lobjAux;
		Class<?> lrefTheType;
        Constructor<?> lrefConst;

		lobjAux = marrElements.get(pidEntity.toString() + "." + pidKey.toString());

		if ( lobjAux == null )
		{
            lrefConst = marrConstructors.get(pidEntity);

            try
            {
	            if (lrefConst == null)
	            {
	                lrefTheType = garrMap.Map(pidEntity);
	                if (lrefTheType == null)
	                    lrefTheType = Entity.GetInstance(pidEntity).getDefObject().getClassType();
	                if (lrefTheType == null)
	                    lrefTheType = ObjectMaster.class;
	                lrefConst = lrefTheType.getConstructor(garrTypes);
	
	                marrConstructors.put(pidEntity, lrefConst);
	            }

	            lobjAux = (ObjectBase)lrefConst.newInstance(garrParams);

	            if (prsObject == null)
	                lobjAux.LoadAt(pidEntity, pidKey);
				else
	                lobjAux.LoadAt(pidEntity, prsObject);
            }
            catch(JewelEngineException e)
            {
            	throw e;
            }
            catch(Exception e)
            {
            	throw new JewelEngineException("Unexpected error in inner Cache.GetObject", e);
            }

            marrElements.put(pidEntity.toString() + "." + pidKey.toString(), lobjAux);

            if (mbIsGlobal)
                lobjAux.SetReadonly();

			if ( pbDoInit )
				lobjAux.Initialize();
		}

		return lobjAux;
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
        		marrElements = new Hashtable<String, ObjectBase>(50000000);
            	marrConstructors = new Hashtable<UUID, Constructor<?>>(1000);
        	}
        	else
        	{
        		marrElements = new Hashtable<String, ObjectBase>();
            	marrConstructors = new Hashtable<UUID, Constructor<?>>();
        	}
        }
        else
        {
        	marrElements = new Hashtable<String, ObjectBase>();
        	marrConstructors = new Hashtable<UUID, Constructor<?>>();
        }

        if (mbIsGlobal)
        {
            marrEntities = new Hashtable<String, UUID>();

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

	public synchronized void setAt(UUID pidEntity, UUID pidKey, ObjectBase value)
	{
		marrElements.put(pidEntity.toString() + "." + pidKey.toString(), value);
        if (mbIsGlobal)
            value.SetReadonly();
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

	public synchronized void DeleteAt(UUID pidEntity, UUID pidKey)
	{
		marrElements.remove(pidEntity.toString() + "." + pidKey.toString());
	}

    public synchronized UUID FindEntity(UUID pidNSpace, UUID pidObject)
    	throws JewelEngineException
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
