package Jewel.Engine.Implementation;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class Object
	extends ObjectBase
	implements IObject
{
	private IObjMember[] marrDataDefs;
	private IDynaCode[] marrActions;
    private IApplication mrefApplication;
    private Class<?> mrefType;
    private Constructor<?> mrefConstructor;
    private Hashtable<Integer, Integer> marrMemberByNOrd = null;

    private static Class<?>[] garrTypes = {};
    private static java.lang.Object[] garrParams = {};

	public static void RawCreate(UUID pidKey) 
		throws JewelEngineException
	{
		Engine.GetCache(true).GetNoInit(EntityGUIDs.E_Object, pidKey);
	}

	public static Object GetInstance(UUID pidKey)
		throws JewelEngineException
	{
		return (Object)Engine.GetCache(true).getAt(EntityGUIDs.E_Object, pidKey);
	}

	public static Object GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
		return (Object)Engine.GetCache(true).getAt(EntityGUIDs.E_Object, prsObject);
	}

	public void Initialize()
		throws JewelEngineException
	{
		MasterDB ldb;
		ResultSet lrsColumns;
		IEntity lrefObjMember;
		int[] larrMembers;
		java.lang.Object[] larrParams;
		int[] larrSorts;
		ArrayList<IObjMember> larrAux;

		larrAux = new ArrayList<IObjMember>();

		larrMembers = new int[1];
		larrMembers[0] = Miscellaneous.FKObject_In_Member;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();
		larrSorts = new int[1];
		larrSorts[0] = Miscellaneous.NOrd_In_Member;

		lrefObjMember = Entity.GetInstance(EntityGUIDs.E_ObjMember);

		try
		{
			ldb = new MasterDB();
			lrsColumns = lrefObjMember.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
			while ( lrsColumns.next() )
				larrAux.add((IObjMember)ObjMember.GetInstance(lrsColumns));
			lrsColumns.close();
			ldb.Disconnect();

			marrDataDefs = larrAux.toArray(new IObjMember[larrAux.size()]);
		}
		catch (SQLException e1)
		{
			throw new JewelEngineException(e1.getMessage(), e1);
		}

        if (getAt(4) == null)
            mrefApplication = null;
        else
            mrefApplication = (IApplication)Application.GetInstance((UUID)getAt(4));

        if ((mrefApplication != null) && (getAt(3) != null))
			try
        	{
				mrefType = /*mrefApplication.getAssembly().GetType*/Class.forName(((String)getAt(3)).replaceAll("MADDS", "Jewel"));
			}
        	catch (ClassNotFoundException e)
        	{
        		mrefType = null;
			}
		else
            mrefType = null;

        if (mrefType != null)
			try
        	{
				mrefConstructor = mrefType.getConstructor(garrTypes);
			}
        	catch (Exception e)
        	{
        		mrefConstructor = null;
			}
		else
            mrefConstructor = null;
    }

	public String Storage()
	{
		return "[" + (String)getAt(2) + "]";
	}

	public String ColumnsForSelect(String pstrTblAlias)
	{
		String lstrAux;
		int i;

		lstrAux = "";
		for ( i = 0; i < marrDataDefs.length; i++ )
			lstrAux += marrDataDefs[i].ColumnForSelect(pstrTblAlias);
		return lstrAux;
	}

	public String ColumnsForInsert()
	{
		String lstrAux;
		int i;

		lstrAux = "";
		for ( i = 0; i < marrDataDefs.length; i++ )
			lstrAux += marrDataDefs[i].ColumnForInsert();
		return lstrAux;
	}

	public String ValuesForInsert(java.lang.Object[] parrData, ArrayList<Blob> parrParams)
		throws JewelEngineException
	{
		String lstrAux;
		int i;

		lstrAux = "";

		for ( i = 0; i < marrDataDefs.length; i++ )
			lstrAux += marrDataDefs[i].ValueForInsert(parrData[i], parrParams);

		return lstrAux;
	}

	public String ValuesForUpdate(java.lang.Object[] parrData, ArrayList<Blob> parrParams)
		throws JewelEngineException
	{
		String lstrAux;
		int i;

		lstrAux = "";

		for ( i = 0; i < marrDataDefs.length; i ++ )
			lstrAux += marrDataDefs[i].ValueForUpdate(parrData[i], parrParams);

		return lstrAux;
	}

	public String FilterByMembers(String pstrTblAlias, int[] parrMembers, java.lang.Object[] parrValues)
		throws JewelEngineException
	{
		String lstrAux;
		int i;

		lstrAux = "";
		for ( i = 0; i < parrMembers.length; i++ )
			lstrAux += marrDataDefs[parrMembers[i]].ValueForFilter(pstrTblAlias, parrValues[i]);

		return lstrAux;
    }

	public String OrderByMembers(String pstrTblAlias, int[] parrMembers)
	{
		String lstrAux;
		int i;

		lstrAux = "";

		for ( i = 0; i < parrMembers.length; i++ )
		{
			if ( parrMembers[i] >= 0 )
				lstrAux += pstrTblAlias + "." + marrDataDefs[parrMembers[i]].ColumnForSort("ASC");
			else if ( parrMembers[i] == Integer.MIN_VALUE )
				lstrAux += pstrTblAlias + "." + marrDataDefs[0].ColumnForSort("DESC");
			else
				lstrAux += pstrTblAlias + "." + marrDataDefs[-parrMembers[i]].ColumnForSort("DESC");
		}

		return lstrAux;
	}

	public String ColumnsForCreateTable(UUID pidNSpace)
		throws JewelEngineException, SQLException
	{
		String lstrAux;
		int i;

		lstrAux = "";
		for ( i = 0; i < marrDataDefs.length; i++ )
			lstrAux += marrDataDefs[i].ColumnForCreateTable(pidNSpace);
		return lstrAux;
	}

	public String ColumnForAlterTable(UUID pidNSpace, int plngMember)
		throws JewelEngineException, SQLException
	{
		return marrDataDefs[plngMember].ColumnForAlterTable(pidNSpace);
	}

    public String ColumnForModifyTable(/*UUID pidNSpace, */int plngMember)
	{
		return marrDataDefs[plngMember].ColumnForModifyTable(/*pidNSpace*/);
	}

	public String ColumnForTableDrop(int plngMember)
	{
		return marrDataDefs[plngMember].ColumnForTableDrop();
	}

	public String ColumnsForMultiSelect(UUID pidNSpace, String pstrColPrefix, R<Integer> plngNextTbl, boolean pbRecurse)
		throws JewelEngineException, SQLException
	{
		String lstrAux;
		int llngTblNum;
		int i;

		lstrAux = "";
		llngTblNum = plngNextTbl.get();
		plngNextTbl.set(llngTblNum * 100 + 1);
		for ( i = 0; i < marrDataDefs.length; i++ )
			lstrAux += marrDataDefs[i].ColumnsForMultiSelect(pidNSpace, pstrColPrefix, llngTblNum, plngNextTbl, pbRecurse);
		plngNextTbl.set(llngTblNum);
		return lstrAux;
	}

	public String ColumnsForSimpleSelect(UUID pidNSpace, String pstrColPrefix, R<Integer> plngNextTbl)
		throws JewelEngineException, SQLException
	{
		String lstrAux;
		int llngTblNum;
		int i;

		lstrAux = "";
		llngTblNum = plngNextTbl.get();
		plngNextTbl.set(llngTblNum * 100 + 1);
		for ( i = 0; i < marrDataDefs.length; i++ )
			lstrAux += marrDataDefs[i].ColumnsForSimpleSelect(pidNSpace, pstrColPrefix, llngTblNum, plngNextTbl);
		plngNextTbl.set(llngTblNum);
		return lstrAux;
	}

	public String TablesForMultiSelect(UUID pidNSpace, R<Integer> plngNextTbl, boolean pbRecurse)
		throws JewelEngineException, SQLException
	{
		String lstrAux;
		int llngTblNum;
		int i;

		lstrAux = "";
		llngTblNum = plngNextTbl.get();
		plngNextTbl.set(llngTblNum * 100 + 1);
		for ( i = 0; i < marrDataDefs.length; i++ )
			lstrAux += marrDataDefs[i].TablesForMultiSelect(pidNSpace, llngTblNum, plngNextTbl, pbRecurse);
		plngNextTbl.set(llngTblNum);
		return lstrAux;
    }

    public String ColumnForReportFilter(UUID pidNSpace, String pstrAlias, String pstrColPrefix, R<Integer> plngNextTbl, boolean pbRecurse)
    	throws JewelEngineException, SQLException
    {
        String lstrAux;
        int llngTblNum;
        int i;

        llngTblNum = plngNextTbl.get();
        plngNextTbl.set(llngTblNum * 100 + 1);
        for (i = 0; i < marrDataDefs.length; i++)
        {
            lstrAux = marrDataDefs[i].ColumnForReportFilter(pidNSpace, pstrAlias, pstrColPrefix, llngTblNum, plngNextTbl, pbRecurse);
            if (!lstrAux.equals(""))
                return lstrAux;
        }
        plngNextTbl.set(llngTblNum);
        return "";
    }

	public String getName()
	{
		return (String)getAt(0);
	}

    public int getMemberCount()
    {
        return marrDataDefs.length;
    }

	public IObjMember[] getMembers()
	{
		return marrDataDefs;
	}

	public IDynaCode[] getActions()
		throws SQLException, JewelEngineException
	{
		MasterDB ldb;
		ResultSet lrsActions;
		Entity lrefDynaCode;
		int[] larrMembers;
		java.lang.Object[] larrParams;
		int[] larrSorts;
		ArrayList<IDynaCode> larrAux;

		if ( marrActions == null )
		{
			larrAux = new ArrayList<IDynaCode>();

			larrMembers = new int[1];
			larrMembers[0] = Miscellaneous.FKOwner_In_DynaCode;
			larrParams = new java.lang.Object[1];
			larrParams[0] = getKey();
			larrSorts = new int[1];
			larrSorts[0] = Miscellaneous.NOrd_In_DynaCode;

			lrefDynaCode = Entity.GetInstance(EntityGUIDs.E_DynaCode);

			ldb = new MasterDB();
			lrsActions = lrefDynaCode.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
			while ( lrsActions.next() )
				larrAux.add((IDynaCode)DynaCode.GetInstance(lrsActions));
			lrsActions.close();
			ldb.Disconnect();

			marrActions = larrAux.toArray(new IDynaCode[larrAux.size()]);
		}

		return marrActions;
	}

	public int MemberByNOrd(int plngNOrd)
	{
		int i;

        if (marrMemberByNOrd == null)
            marrMemberByNOrd = new Hashtable<Integer, Integer>();

        if (marrMemberByNOrd.get(plngNOrd) != null)
            return (int)marrMemberByNOrd.get(plngNOrd);

		for ( i = 0; i < marrDataDefs.length; i++ )
            if (marrDataDefs[i].getNOrder() == plngNOrd)
            {
                marrMemberByNOrd.put(plngNOrd, i);
                return i;
            }

        marrMemberByNOrd.put(plngNOrd, -1);
		return -1;
	}

	public IQueryDef DefaultQuery()
		throws SQLException, JewelEngineException
	{
		UUID lidQuery;
		IEntity lrefQueryEntity;
		int[] larrMembers;
		java.lang.Object[] larrValues;
		MasterDB ldb;
		ResultSet lrs;

		larrMembers = new int[2];
		larrMembers[0] = 0;
		larrMembers[1] = 1;
		larrValues = new java.lang.Object[2];
		larrValues[0] = getKey();
		larrValues[1] = true;

		lrefQueryEntity = Entity.GetInstance(EntityGUIDs.E_QueryDef);
		ldb = new MasterDB();
		lrs = lrefQueryEntity.SelectByMembers(ldb, larrMembers, larrValues, new int[0]);

		if ( lrs.next() )
			lidQuery = UUID.fromString(lrs.getString(1));
		else
			lidQuery = null;

		lrs.close();
		ldb.Disconnect();

		if (lidQuery == null)
			return null;

		return (IQueryDef)QueryDef.GetInstance(lidQuery);
    }

    public ObjectBase CreateNew()
    	throws InvocationTargetException, JewelEngineException
    {
        if (mrefConstructor == null)
            return new ObjectMaster();

        try
        {
			return (ObjectBase)mrefConstructor.newInstance(garrParams);
		}
        catch (InvocationTargetException e)
        {
        	throw e;
		}
        catch (Exception e)
        {
        	throw new JewelEngineException("Unexpected exception in Object CreateNew", e);
		}
    }

    public IDynaCode GetActionAt(int plngIndex)
    	throws SQLException, JewelEngineException
    {
        int i;

        for (i = 0; i < getActions().length; i++)
            if (marrActions[i].getIndex() == plngIndex)
                return marrActions[i];

        return null;
    }

    public String getAssembly()
    {
        if (mrefApplication != null)
            return mrefApplication.getAssemblyName();
        else
            return null;
    }

    public String getClassName()
    {
        return (String)getAt(3);
    }

    public Class<?> getClassType()
    {
        return mrefType;
    }
}
