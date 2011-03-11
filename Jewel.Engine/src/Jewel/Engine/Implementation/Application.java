package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class Application
	extends ObjectBase
	implements IApplication
{
    private Package mrefAssembly;

	public static Application GetInstance(UUID pidKey)
		throws JewelEngineException
	{
        return (Application)Engine.GetCache(true).getAt(EntityGUIDs.E_Application, pidKey);
	}

	public static Application GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
        return (Application)Engine.GetCache(true).getAt(EntityGUIDs.E_Application, prsObject);
	}

    public void Initialize()
    {
        if (getAssemblyName() != null)
            mrefAssembly = CodeExecuter.LoadAssembly(getAssemblyName());
    }

    public String getAssemblyName()
    {
       return (String)getAt(1);
    }

    public Package getAssembly()
    {
        return mrefAssembly;
    }

    public String getStaticClass()
    {
    	return (String)getAt(3);
    }

    public String getLoginMethod()
    {
       return (String)getAt(4);
    }

    public void Deploy(java.lang.Object[] parrParams)
    	throws JewelEngineException
    {
    	UUID lidNSpace;
		IEntity lrefObject;
		int[] larrMembers;
		java.lang.Object[] larrParams;
		MasterDB ldbWrite;
		MasterDB ldbRead;
		ResultSet lrsColumns;
		IObject lobjObject;
		UUID lidObject;
		UUID lidEntity;
		ObjectBase lobjAux;
		Entity lobjEntity;
		ArrayList<ObjectBase> larrAux;
		int i;
		boolean b;

		if ( (parrParams == null) || (parrParams.length < 6) || (parrParams[5] == null) || !(parrParams[5] instanceof UUID))
			throw new JewelEngineException("Invalid Argument: Name Space to deploy to is null or not an identifier.");
		lidNSpace = (UUID)parrParams[5];

		larrMembers = new int[1];
		larrMembers[0] = Miscellaneous.FKApp_In_Object;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();

		lrefObject = Entity.GetInstance(EntityGUIDs.E_Object);

		larrAux = new ArrayList<ObjectBase>();

		try
		{
			ldbRead = new MasterDB();

			lrsColumns = lrefObject.SelectByMembers(ldbRead, larrMembers, larrParams, new int[0]);
			while ( lrsColumns.next() )
			{
				lobjObject = Object.GetInstance(lrsColumns);
				lidObject = lobjObject.getKey();

				lidEntity = Engine.FindEntity(lidNSpace, lidObject);
				if ( lidEntity != null )
				{
					lobjEntity = Entity.GetInstance(lidEntity);
					if ( lobjEntity.getMemberOf().getKey().equals(lidNSpace) )
						continue;
				}

				lobjAux = Engine.GetWorkInstance(EntityGUIDs.E_Entity, (UUID)null);
				lobjAux.setAt(Miscellaneous.FKNameSpace_In_Entity, lidNSpace);
				lobjAux.setAt(Miscellaneous.FKObject_In_Entity, lidObject);

				larrAux.add(lobjAux);
			}

			lrsColumns.close();
			ldbRead.Disconnect();
		}
		catch (JewelEngineException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}

		try
		{
			ldbWrite = new MasterDB();
			ldbWrite.BeginTrans();

			for ( i = 0; i < larrAux.size(); i++ )
			{
				try
				{
					larrAux.get(i).SaveToDb(ldbWrite);
				}
				catch (JewelEngineException e)
				{
					ldbWrite.Rollback();
					ldbWrite.Disconnect();
					throw e;
				}
				catch (Exception e)
				{
					ldbWrite.Rollback();
					ldbWrite.Disconnect();
					throw new JewelEngineException(e.getMessage(), e);
				}
			}

			ldbWrite.Commit();
			ldbWrite.Disconnect();
		}
		catch (JewelEngineException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}

		b = true;
		while ( b )
		{
			b = false;

			for ( i = 0; i < larrAux.size(); i++ )
			{
				lobjEntity = Entity.GetInstance(larrAux.get(i).getKey());
				if ( lobjEntity == null )
					continue;

				try
				{
					lobjEntity.CreateInDB(lobjEntity.getData());
					larrAux.set(i, null);
				}
				catch (Exception e)
				{
					b = true;
				}
			}
		}

		throw new JewelEngineException("Application deployed. " + Integer.toString(larrAux.size()) +
				" entites created in database.");
    }
}
