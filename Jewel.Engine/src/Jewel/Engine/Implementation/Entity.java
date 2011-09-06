package Jewel.Engine.Implementation;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class Entity
	extends ObjectBase
	implements IEntity
{
	private INameSpace mrefSpace;
	private IObject mrefObject;
    private IQueryDef mrefDefaultQuery;
    private IForm mrefDefSearch;

	public static void RawCreate(UUID pidKey) 
		throws JewelEngineException
	{
		Engine.GetCache(true).GetNoInit(EntityGUIDs.E_Entity, pidKey);
	}

	public static Entity GetInstance(UUID pidKey)
		throws JewelEngineException
	{
		return (Entity)Engine.GetCache(true).getAt(EntityGUIDs.E_Entity, pidKey);
	}

	public static Entity GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
		return (Entity)Engine.GetCache(true).getAt(EntityGUIDs.E_Entity, prsObject);
	}

	public void Initialize()
		throws JewelEngineException
	{
        mrefSpace = (INameSpace)NameSpace.GetInstance((UUID)getAt(0));
        mrefObject = (IObject)Object.GetInstance((UUID)getAt(1));
    }

    public String getLabel()
    {
    	if ( (mrefObject == null) || (mrefSpace == null) )
    		return null;

    	return mrefObject.getName() + "@"  + mrefSpace.getName();
    }

	private String SQLForInsert(UUID pidKey, java.lang.Object[] parrData, ArrayList<Blob> larrParams)
		throws JewelEngineException
	{
		return "INSERT INTO " + Storage() + " ([PK]" + mrefObject.ColumnsForInsert() + ") VALUES ('" + pidKey.toString() + "'" +
			mrefObject.ValuesForInsert(parrData, larrParams) + ")";
	}

	private String SQLForSelect(String pstrTblAlias)
	{
        if (EntityGUIDs.E_Entity.equals(getKey()))
            return SpecialSQL.EntitySelect;

        if (EntityGUIDs.E_TypeDef.equals(getKey()))
            return SpecialSQL.TypeDefSelect;

        if (EntityGUIDs.E_ObjMember.equals(getKey()))
            return SpecialSQL.ObjMemberSelect;

        if (EntityGUIDs.E_Object.equals(getKey()))
            return SpecialSQL.ObjectSelect;

        if (EntityGUIDs.E_NameSpace.equals(getKey()))
            return SpecialSQL.NameSpaceSelect;

        if (EntityGUIDs.E_Application.equals(getKey()))
            return SpecialSQL.ApplicationSelect;

        return "SELECT " + pstrTblAlias + ".[PK] [PK]" + mrefObject.ColumnsForSelect(pstrTblAlias) + " FROM " + Storage() + " " + pstrTblAlias;
	}

	private String SQLForUpdate(java.lang.Object[] parrData, ArrayList<Blob> parrParams)
		throws JewelEngineException
	{
		return "UPDATE " + Storage() + " SET [_TSUpdate] = GETDATE()" + mrefObject.ValuesForUpdate(parrData, parrParams);
	}

	private String SQLForDelete()
	{
		return "DELETE FROM " + Storage();
	}

	private String SQLForCreateTable()
	{
		return "CREATE TABLE " + Storage() + " (" +
			"[PK] uniqueidentifier ROWGUIDCOL NOT NULL PRIMARY KEY, " +
			"[_VER] rowversion, " +
			"[_TSCreate] datetime DEFAULT GETDATE() NOT NULL, " +
			"[_TSUpdate] datetime DEFAULT GETDATE() NOT NULL, " +
			"[FKType] uniqueidentifier DEFAULT '" + mrefObject.getKey() + "' NOT NULL REFERENCES [madds].[tblObjects] ([PK])";
	}

    private String SQLForDropTable()
    {
        return "DROP TABLE " + Storage();
    }

    private String SQLForAddMember()
	{
		return "ALTER TABLE " + Storage() + " ADD ";
	}

	private String SQLForModifyMember()
	{
		return "ALTER TABLE " + Storage() + " ALTER COLUMN ";
	}

	private String SQLForDropMember()
	{
		return "ALTER TABLE " + Storage() + " DROP COLUMN ";
	}

	public String Storage()
	{
		return mrefSpace.Storage() + "." + mrefObject.Storage();
	}

	public String SQLColumnsForMultiSelect(String pstrColPrefix, R<Integer> plngNextTbl, boolean pbRecurse)
		throws JewelEngineException, SQLException
	{
		return mrefObject.ColumnsForMultiSelect(mrefSpace.getKey(), pstrColPrefix, plngNextTbl, pbRecurse);
	}

	public String SQLTablesForMultiSelect(R<Integer> plngNextTbl, boolean pbRecurse)
		throws JewelEngineException, SQLException
	{
		return mrefObject.TablesForMultiSelect(mrefSpace.getKey(), plngNextTbl, pbRecurse);
	}

    public String SQLColumnForReportFilter(String pstrAlias, String pstrColPrefix, R<Integer> plngNextTbl, boolean pbRecurse)
    	throws JewelEngineException, SQLException
    {
        return mrefObject.ColumnForReportFilter(mrefSpace.getKey(), pstrAlias, pstrColPrefix, plngNextTbl, pbRecurse);
    }

	public String SQLForSelectMulti()
		throws JewelEngineException, SQLException
	{
		String lstrAux;
		R<Integer> llngCurrTbl;

		llngCurrTbl = new R<Integer>(new Integer(1));
		lstrAux = "SELECT [t1].[PK] [PK]" + mrefObject.ColumnsForMultiSelect(mrefSpace.getKey(), "", llngCurrTbl, true);
		llngCurrTbl = new R<Integer>(new Integer(1));
		lstrAux += " FROM " + Storage() + " [t1]" + SQLTablesForMultiSelect(llngCurrTbl, true);

		return lstrAux;
	}

	public String SQLForSelectMultiFiltered(int[] parrMembers, java.lang.Object[] parrValues)
		throws JewelEngineException, SQLException
	{
		String lstrAux;

		lstrAux = SQLForSelectMulti();

		if ( parrMembers.length > 0 )
			lstrAux += " WHERE 1=1" + mrefObject.FilterByMembers("[t1]", parrMembers, parrValues);

		return lstrAux;
    }

	public String SQLForSelectAll()
	{
		return SQLForSelect("[t1]");
	}

	public String SQLForSelectAllSort(int[] parrSorts)
	{
		String lstrAux;

		lstrAux = SQLForSelect("[t1]");

		if ( parrSorts != null )
			lstrAux += " ORDER BY " + mrefObject.OrderByMembers("[t1]", parrSorts) + "[t1].[_TSCreate]";

		return lstrAux;
	}

	public String SQLForSelectByKey(UUID pidKey)
	{
		return SQLForSelect("[t1]") + " WHERE [t1].[PK] = '" + pidKey.toString() + "'";
	}

	public String SQLForSelectByMembers(int[] parrMembers, java.lang.Object[] parrValues, int[] parrSorts)
		throws JewelEngineException
	{
		String lstrAux;

		if ((EntityGUIDs.E_ObjMember.equals(getKey())) && (parrMembers.length == 1) && (parrMembers[0] == Miscellaneous.FKObject_In_Member) && (parrValues[0] != null) &&
				(parrSorts.length == 1) && (parrSorts[0] == Miscellaneous.NOrd_In_Member))
			return SQLForSelect("[t1]") + SpecialSQL.ObjMemberBuild + "'" + ((UUID)parrValues[0]).toString() + "'" + SpecialSQL.ObjMemberSort;

		lstrAux = SQLForSelect("[t1]");
		if ( parrMembers.length > 0 )
			lstrAux += " WHERE 1=1" + mrefObject.FilterByMembers("[t1]", parrMembers, parrValues);
		if ( parrSorts != null )
			lstrAux += " ORDER BY " + mrefObject.OrderByMembers("[t1]", parrSorts) + "[t1].[_TSCreate]";

		return lstrAux;
	}

	public String SQLForSelectForReports(String[] parrColAliases, String[] parrCriteria, String[] parrSorts)
		throws JewelEngineException, SQLException
	{
        String lstrAux;
		R<Integer> llngCurrTbl;
        int i;

        lstrAux = SQLForSelectMulti();

        if (parrCriteria.length > 0)
        {
            lstrAux += " WHERE 1=1";
            for (i = 0; i < parrCriteria.length; i++)
            {
        		llngCurrTbl = new R<Integer>(new Integer(1));
                lstrAux += " AND " + mrefObject.ColumnForReportFilter(mrefSpace.getKey(), parrColAliases[i], "", llngCurrTbl, true) + parrCriteria[i];
            }
        }

        if (parrSorts.length >= 0)
        {
            lstrAux += " ORDER BY ";
            for (i = 0; i < parrSorts.length; i++)
                lstrAux += parrSorts[i] + ", ";
            lstrAux += "[t1].[_TSCreate]";
        }

		return lstrAux;
	}

	public UUID Insert(SQLServer pdb, java.lang.Object[] parrData)
		throws SQLException, JewelEngineException
	{
		ArrayList<Blob> larrParams;
		String lstrSQL;
		UUID lid;

        lid = pdb.ExecuteGUIDFunction("SELECT CAST(CAST(NEWID() AS BINARY(10)) + CAST(GETDATE() AS BINARY(6)) AS UNIQUEIDENTIFIER)");

		larrParams = new ArrayList<Blob>();
		lstrSQL = SQLForInsert(lid, parrData, larrParams);

		pdb.ExecuteSQL(lstrSQL, larrParams.toArray(new Blob[larrParams.size()]));

		return lid;
	}

	public void Update(SQLServer pdb, UUID pidKey, java.lang.Object[] parrData)
		throws SQLException, JewelEngineException
	{
		ArrayList<Blob> larrParams;
		String lstrSQL;

		if (pidKey == null)
			return;

		larrParams = new ArrayList<Blob>();
		lstrSQL = SQLForUpdate(parrData, larrParams);

		pdb.ExecuteSQL(lstrSQL + " WHERE [PK] = '" + pidKey.toString() + "'", larrParams.toArray(new Blob[larrParams.size()]));

		Engine.GetCache(true).DeleteAt(getKey(), pidKey);
	}

	public void Delete(SQLServer pdb, UUID pidKey)
		throws SQLException, JewelEngineException
	{
		pdb.ExecuteSQL(SQLForDelete() + " WHERE [PK] = '" + pidKey.toString() + "'");
		Engine.GetCache(true).DeleteAt(getKey(), pidKey);
		Engine.GetCache(false).DeleteAt(getKey(), pidKey);
	}

	public ResultSet SelectAll(SQLServer pdb)
		throws SQLException
	{
		return pdb.OpenRecordset(SQLForSelectAll());
	}

	public ResultSet SelectAllSort(SQLServer pdb, int[] parrSorts)
		throws SQLException
	{
		return pdb.OpenRecordset(SQLForSelectAllSort(parrSorts));
	}

	public ResultSet SelectByKey(SQLServer pdb, UUID pidKey)
		throws SQLException
	{
		return pdb.OpenRecordset(SQLForSelectByKey(pidKey));
	}

	public ResultSet SelectByMembers(SQLServer pdb, int[] parrMembers, java.lang.Object[] parrValues, int[] parrSorts)
		throws SQLException, JewelEngineException
	{
		return pdb.OpenRecordset(SQLForSelectByMembers(parrMembers, parrValues, parrSorts));
    }

    public ResultSet SelectForReports(SQLServer pdb, String[] parrColAliases, String[] parrCriteria, String[] parrSorts)
    	throws SQLException, JewelEngineException
    {
        return pdb.OpenRecordset(SQLForSelectForReports(parrColAliases, parrCriteria, parrSorts));
    }

	public void CreateTable(SQLServer pdb)
		throws SQLException, JewelEngineException
	{
		pdb.ExecuteDDL(mrefSpace.getKey(), SQLForCreateTable() + mrefObject.ColumnsForCreateTable(mrefSpace.getKey()) + ")");
	}

    public void DropTable(SQLServer pdb)
    	throws SQLException
    {
        pdb.ExecuteDDL(mrefSpace.getKey(), SQLForDropTable());
    }

    public void AddNewMember(SQLServer pdb, java.lang.Object[] parrParams)
    	throws SQLException, JewelEngineException
	{
        String lstrAux;

        if (parrParams[9] == null)
            return;

        lstrAux = SQLForAddMember() + "[" + (String)parrParams[9] + "] " + TypeDef.GetInstance((UUID)parrParams[4]).TypeForCreate();

        if (parrParams[5] != null)
        {
            lstrAux += "(" + ((Integer)parrParams[5]).toString();
            if (parrParams[10] != null)
                lstrAux += "," + ((Integer)parrParams[10]).toString();
            lstrAux += ")";
        }

        if ((parrParams[8] != null) && (Boolean)parrParams[8])
            lstrAux += " UNIQUE";

        if ((parrParams[7] != null) && !(Boolean)parrParams[7])
            lstrAux += " NOT NULL";

        if (parrParams[6] != null)
            lstrAux += " FOREIGN KEY REFERENCES " + Entity.GetInstance(Engine.FindEntity(mrefSpace.getKey(), (UUID)parrParams[6])).Storage() + " ([PK])";

        pdb.ExecuteDDL(mrefSpace.getKey(), lstrAux);
	}

	public void ChangeMember(SQLServer pdb, int plngMember)
		throws SQLException
	{
        pdb.ExecuteDDL(mrefSpace.getKey(), SQLForModifyMember() + mrefObject.ColumnForModifyTable(plngMember));
    }

	public void DropMember(SQLServer pdb, int plngMember)
		throws SQLException
	{
		pdb.ExecuteDDL(mrefSpace.getKey(), SQLForDropMember() + mrefObject.ColumnForTableDrop(plngMember));
	}

	public String getName()
	{
		return mrefObject.getName();
	}

	public IObject getDefObject()
	{
		return mrefObject;
    }

    public int getMemberCount()
    {
        return mrefObject.getMemberCount();
    }

	public INameSpace getMemberOf()
	{
		return mrefSpace;
    }

    public ObjectBase CreateNew()
    	throws InvocationTargetException, JewelEngineException
    {
        ObjectBase lobjAux;

        lobjAux = mrefObject.CreateNew();
        lobjAux.LoadAt(getKey());
        return lobjAux;
    }

    public IQueryDef getDefaultQuery()
    	throws JewelEngineException, SQLException
    {
        IEntity lrefQuery;
        MasterDB ldb;
        ResultSet lrs;
        int[] larrMembers;
        java.lang.Object[] larrParams;

        if (mrefDefaultQuery == null)
        {
            larrMembers = new int[2];
            larrMembers[0] = Miscellaneous.DisplayedObject_In_QueryDef;
            larrMembers[1] = Miscellaneous.Default_In_QueryDef;
            larrParams = new java.lang.Object[2];
            larrParams[0] = mrefObject.getKey();
            larrParams[1] = true;

            lrefQuery = Entity.GetInstance(EntityGUIDs.E_QueryDef);

            ldb = new MasterDB();
            lrs = lrefQuery.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);

            if (lrs.next())
                mrefDefaultQuery = QueryDef.GetInstance(lrs);

            lrs.close();
        }

        return mrefDefaultQuery;
    }

    public IForm getDefaultSearchForm()
    	throws JewelEngineException, SQLException
    {
        IEntity lrefForm;
        MasterDB ldb;
        ResultSet lrs;
        int[] larrMembers;
        java.lang.Object[] larrParams;

        if ( mrefDefSearch == null )
        {
            if (getDefaultQuery() == null)
            {
                larrMembers = new int[1];
                larrMembers[0] = Miscellaneous.EditedObject_In_Form;
                larrParams = new java.lang.Object[1];
                larrParams[0] = getDefObject().getKey();
            }
            else
            {
                larrMembers = new int[1];
                larrMembers[0] = Miscellaneous.ResultsQuery_In_Form;
                larrParams = new java.lang.Object[1];
                larrParams[0] = getDefaultQuery().getKey();
            }

            lrefForm = Entity.GetInstance(EntityGUIDs.E_Form);

            ldb = new MasterDB();
            lrs = lrefForm.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);

            if (lrs.next())
                mrefDefSearch = Form.GetInstance(lrs);

            lrs.close();
        }

        return mrefDefSearch;
    }

    public void CreateInDB(java.lang.Object[] parrParams)
    	throws JewelEngineException, SQLException
    {
        MasterDB ldb;

        if (!getNameSpace().equals(NameSpaceGUIDs.N_MADDS))
            return;

        if (!CheckSaved(parrParams))
            throw new JewelEngineException("Entity must be saved before table can be created.");

        if (mrefSpace == null)
            mrefSpace = (INameSpace)NameSpace.GetInstance((UUID)getAt(0));
        if (mrefObject == null)
            mrefObject = (IObject)Object.GetInstance((UUID)getAt(1));

        ldb = new MasterDB();
        CreateTable(ldb);
        ldb.Disconnect();
    }

    public void DropFromDB(java.lang.Object[] parrParams)
    	throws JewelEngineException, SQLException
    {
        MasterDB ldb;

        if (!getNameSpace().equals(NameSpaceGUIDs.N_MADDS))
            return;

        if (!CheckSaved(parrParams))
            throw new JewelEngineException("Entity must be saved before table can be dropped.");

        if (mrefSpace == null)
            mrefSpace = (INameSpace)NameSpace.GetInstance((UUID)getAt(0));
        if (mrefObject == null)
            mrefObject = (IObject)Object.GetInstance((UUID)getAt(1));

        ldb = new MasterDB();
        DropTable(ldb);
        ldb.Disconnect();
    }
}
