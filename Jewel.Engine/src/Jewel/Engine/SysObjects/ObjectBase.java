package Jewel.Engine.SysObjects;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;

public abstract class ObjectBase
	implements IJewelBase
{
    private UUID midEntity;
	private UUID midKey;
	private IEntity mrefDefinition;
	private Object[] marrMembers;
    private boolean mbReadOnly;

    public ObjectBase()
    {
        midEntity = null;
        midKey = null;
        mrefDefinition = null;
        marrMembers = null;
        mbReadOnly = false;
    }

    public void LoadAt(UUID pidEntity)
    	throws JewelEngineException
    {
        int llngCount;
        int i;

        InitObject(pidEntity, null);

        llngCount = mrefDefinition.getMemberCount();

        marrMembers = new Object[llngCount];
        for (i = 0; i < llngCount; i++)
            marrMembers[i] = null;
    }

    public void LoadAt(UUID pidEntity, UUID pidKey)
    	throws SQLException, JewelEngineException
    {
        MasterDB ldb;
        ResultSet lrsObject;

        if (pidKey == null)
        {
            LoadAt(pidEntity);
            return;
        }

        InitObject(pidEntity, pidKey);
        ldb = new MasterDB();
        lrsObject = mrefDefinition.SelectByKey(ldb, midKey);

        if (!lrsObject.next())
        {
            lrsObject.close();
            ldb.Disconnect();
            throw new JewelEngineException("Unexpected: Object not found.");
        }

        LoadObject(lrsObject);
        lrsObject.close();
        ldb.Disconnect();
    }

    public void LoadAt(UUID pidEntity, ResultSet prsObject)
    	throws SQLException, JewelEngineException
    {
		InitObject(pidEntity, UUID.fromString(prsObject.getString(1)));

		LoadObject(prsObject);
	}

    public void LoadAt(ObjectBase pobjSource) 
    	throws JewelEngineException
    {
        InitObject(pobjSource.getDefinition().getKey(), pobjSource.getKey());

        marrMembers = pobjSource.getData();
    }

    public void LoadAt(UUID pidEntity, ObjectBase pobjSource)
    	throws JewelEngineException
    {
        InitObject(pidEntity, null);

        marrMembers = pobjSource.getData();
    }

    public void SetReadonly()
    {
        mbReadOnly = true;
    }

    public Object getAt(int plngMember)
    {
        return marrMembers[plngMember];
    }

    public void setAt(int plngMember, Object value)
    	throws JewelEngineException
    {
        if (mbReadOnly)
            throw new JewelEngineException("Object is read-only.");

        marrMembers[plngMember] = value;
    }

    protected void internalSetAt(int plngMember, Object value)
    {
        marrMembers[plngMember] = value;
    }

    public Object[] getData()
    {
        return (Object[])marrMembers.clone();
    }

    public void setData(Object[] value)
    	throws JewelEngineException
    {
        if (mbReadOnly)
            throw new JewelEngineException("Object is read-only.");

        marrMembers = value;
    }

    public void setDataRange(Object[] value)
		throws JewelEngineException
    {
        if (mbReadOnly)
            throw new JewelEngineException("Object is read-only.");

        marrMembers = Arrays.copyOfRange(value, 0, marrMembers.length);
    }

    public boolean CheckSaved(Object[] parrData)
    {
        int i;

        if (parrData.length < marrMembers.length)
            return false;

        for (i = 0; i < marrMembers.length; i++)
            if (!(((parrData[i] == null) && (marrMembers[i] == null)) ||
            		((parrData[i] != null) && (parrData[i].equals(marrMembers[i])))))
                return false;

        return true;
    }

    public void Validate(Object[] parrData)
    	throws JewelEngineException
    {
        String lstrError;

        lstrError = OnValidate(parrData);
        if (!lstrError.equals(""))
            throw new JewelEngineException(lstrError);

        setData(parrData);
    }

    public void SaveToDb(SQLServer pdb)
    	throws JewelEngineException, SQLException
    {
        String lstrError;

        if (midKey == null)
        {
            midKey = mrefDefinition.Insert(pdb, marrMembers);

            lstrError = AfterSave();
            if (!lstrError.equals(""))
                throw new JewelEngineException(lstrError);

            if ( !mbReadOnly )
            	Engine.GetCache(false).setAt(midEntity, midKey, this);
        }
        else
        {
            mrefDefinition.Update(pdb, midKey, marrMembers);

	        lstrError = AfterSave();
	        if (!lstrError.equals(""))
	            throw new JewelEngineException(lstrError);

            if ( !mbReadOnly )
            {
        		Engine.GetCache(true).DeleteAt(midEntity, midKey);
            	Engine.GetCache(false).getAt(midEntity, midKey).setData(getData());
            }
        }
    }

    public abstract void Initialize() throws JewelEngineException;

    public String OnValidate(Object[] parrData)
    {
        return "";
    }

    public String AfterSave()
    	throws JewelEngineException
    {
        return "";
    }

    public IEntity getDefinition()
    {
        return mrefDefinition;
    }

    public UUID getNameSpace()
    {
        return mrefDefinition.getMemberOf().getKey();
    }

    public UUID getKey()
    {
        return midKey;
    }

    public String getLabel()
    {
    	if ( marrMembers[0] == null )
    		return null;

    	return marrMembers[0].toString();
    }

    private void InitObject(UUID pidEntity, UUID pidKey)
    	throws JewelEngineException
    {
        midEntity = pidEntity;
        midKey = pidKey;

        if ((EntityGUIDs.E_Entity.equals(midEntity)) && (EntityGUIDs.E_Entity.equals(midKey)))
            mrefDefinition = (IEntity)this;
        else
            mrefDefinition = (IEntity)Engine.GetCache(true).getAt(EntityGUIDs.E_Entity, midEntity);
    }

    private void LoadObject(ResultSet prsObject)
    	throws SQLException
    {
        ResultSetMetaData lrsMetaData;
        int numberOfColumns;
        int i;
        String lstrAux;

        lrsMetaData = prsObject.getMetaData();
        numberOfColumns = lrsMetaData.getColumnCount();
        marrMembers = new Object[numberOfColumns - 1];

        for (i = 1; i < numberOfColumns; i++)
        {
        	if ( lrsMetaData.getColumnTypeName(i + 1).equals("uniqueidentifier") )
        	{
        		lstrAux = prsObject.getString(i + 1);
        		if ( lstrAux == null )
            		marrMembers[i - 1] = null;
        		else
        			marrMembers[i - 1] = UUID.fromString(lstrAux);
        	}
	    	else
        		marrMembers[i - 1] = prsObject.getObject(i + 1);
        }
    }
}
