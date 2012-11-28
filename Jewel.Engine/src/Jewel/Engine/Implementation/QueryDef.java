package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class QueryDef
	extends ObjectBase
	implements IQueryDef
{
	private IQueryField[] marrFields;
    private IQueryParam[] marrParams;
	private IObject mrefDriverObject;
//	private IReport mrefReport;

    private String mstrType;

	public static QueryDef GetInstance(UUID pidKey)
		throws JewelEngineException
	{
		return (QueryDef)Engine.GetCache(true).getAt(EntityGUIDs.E_QueryDef, pidKey);
	}

	public static QueryDef GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
		return (QueryDef)Engine.GetCache(true).getAt(EntityGUIDs.E_QueryDef, prsObject);
	}

	public void Initialize()
		throws JewelEngineException
	{
		MasterDB ldb;
		ResultSet lrsColumns;
		Entity lrefAux;
		int[] larrMembers;
		java.lang.Object[] larrParams;
		int[] larrSorts;
		ArrayList<IQueryField> larrAux;
		ArrayList<IQueryParam> larrAux2;

		mrefDriverObject = (IObject)Object.GetInstance((UUID)getAt(0));

		larrAux = new ArrayList<IQueryField>();

		larrMembers = new int[1];
		larrMembers[0] = Miscellaneous.FKOwner_In_QueryField;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();
		larrSorts = new int[1];
		larrSorts[0] = Miscellaneous.NOrd_In_QueryField;

		lrefAux = Entity.GetInstance(EntityGUIDs.E_QueryField);

		try
		{
			ldb = new MasterDB();
			lrsColumns = lrefAux.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
			while ( lrsColumns.next() )
				larrAux.add((IQueryField)QueryField.GetInstance(lrsColumns));
			lrsColumns.close();
			ldb.Disconnect();

			marrFields = larrAux.toArray(new IQueryField[larrAux.size()]);
		}
		catch (SQLException e)
		{
			throw new JewelEngineException("SQL Error reading Query Field lists", e);
		}

//		if (getAt(7) != null)
//            mrefReport = (IReport)Report.GetInstance((UUID)getAt(7));

        larrAux2 = new ArrayList<IQueryParam>();

        larrMembers = new int[1];
        larrMembers[0] = Miscellaneous.FKOwner_In_QueryParam;
        larrParams = new java.lang.Object[1];
        larrParams[0] = getKey();
        larrSorts = new int[1];
        larrSorts[0] = Miscellaneous.NOrd_In_QueryParam;

        lrefAux = Entity.GetInstance(EntityGUIDs.E_QueryParam);

		try
		{
	        ldb = new MasterDB();
	        lrsColumns = lrefAux.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
	        while (lrsColumns.next())
	            larrAux2.add((IQueryParam)QueryParam.GetInstance(lrsColumns));
	        lrsColumns.close();
	        ldb.Disconnect();

	        marrParams = larrAux2.toArray(new IQueryParam[larrAux2.size()]);
		}
		catch (SQLException e)
		{
			throw new JewelEngineException("SQL Error reading Query Field lists", e);
		}

        if (QueryTypeGUIDs.QT_AND.equals((UUID)getAt(12)))
            mstrType = "AND";
        else
            mstrType = "OR";
	}

	public ResultSet SelectAll(SQLServer pdb, UUID pidNSpace, HashMap<String, java.lang.Object> parrValues)
		throws SQLException, JewelEngineException
	{
		IEntity lrefEntity;
		String lstrAux;
		int i;

		lrefEntity = (IEntity)Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(0)));

		lstrAux = "SELECT [PK]";
		for ( i = 0; i < marrFields.length; i++ )
			lstrAux += marrFields[i].ColumnForSelect();

		lstrAux += " FROM (" + lrefEntity.SQLForSelectMulti() + ") [Aux]";

        if (marrParams.length > 0)
            lstrAux += " WHERE" + BuildParamSQL(parrValues);

		return pdb.OpenRecordset(lstrAux);
	}

	public ResultSet SelectByParam(SQLServer pdb, UUID pidNSpace, java.lang.Object pobjParamValue, HashMap<String, java.lang.Object> parrValues)
		throws SQLException, JewelEngineException
	{
		IEntity lrefEntity;
		ITypeDef lrefType;
		String lstrAux;
		int i;

		if ( (getAt(2) == null) || (getAt(3) == null) )
			return SelectAll(pdb, pidNSpace, parrValues);

		lrefEntity = (IEntity)Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(0)));
		lrefType = (ITypeDef)TypeDef.GetInstance((UUID)getAt(3));

		lstrAux = "SELECT [PK]";
		for ( i = 0; i < marrFields.length; i++ )
			lstrAux += marrFields[i].ColumnForSelect();

        lstrAux += " FROM (" + lrefEntity.SQLForSelectMulti() + ") [Aux] WHERE " + (String)getAt(2) + " = " + lrefType.TranslateValue(pobjParamValue, false, null);

        if (marrParams.length > 0)
            lstrAux += " AND (" + BuildParamSQL(parrValues) + ")";

		return pdb.OpenRecordset(lstrAux);
	}

	public ResultSet SelectByKey(SQLServer pdb, UUID pidNSpace, UUID pidKey)
		throws SQLException, JewelEngineException
	{
		IEntity lrefEntity;
		String lstrAux;
		int i;

		lrefEntity = (IEntity)Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(0)));

		lstrAux = "SELECT [PK]";
		for ( i = 0; i < marrFields.length; i++ )
			lstrAux += marrFields[i].ColumnForSelect();

		lstrAux += " FROM (" + lrefEntity.SQLForSelectMulti() + ") [Aux] WHERE [PK] = '" + pidKey.toString() + "'";

		return pdb.OpenRecordset(lstrAux);
	}

    public ResultSet SelectByMembers(SQLServer pdb, UUID pidNSpace, int[] parrMembers, java.lang.Object[] parrValues, HashMap<String, java.lang.Object> parrPValues)
    	throws SQLException, JewelEngineException
	{
		IEntity lrefEntity;
		String lstrAux;
		int i;

		lrefEntity = (IEntity)Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(0)));

		lstrAux = "SELECT [PK]";
		for ( i = 0; i < marrFields.length; i++ )
			lstrAux += marrFields[i].ColumnForSelect();

        lstrAux += " FROM (" + lrefEntity.SQLForSelectMultiFiltered(parrMembers, parrValues) + ") [Aux]";

        if (marrParams.length > 0)
            lstrAux += " WHERE" + BuildParamSQL(parrPValues);

		return pdb.OpenRecordset(lstrAux);
	}

	public IObject getDriverObject()
	{
		return mrefDriverObject;
	}

	public UUID getParamType()
	{
		return (UUID)getAt(3);
	}

	public int getParamAppliesTo()
	{
		return (Integer)getAt(4);
	}

	public UUID getEditorViewID()
	{
		return (UUID)getAt(5);
	}

	public int Width(int plngIndex)
	{
		return marrFields[plngIndex].getWidth();
	}

	public boolean getReadOnly()
	{
		return (Boolean)getAt(6);
	}

	public UUID getReportID()
	{
		return (UUID)getAt(7);
	}

    public IQueryField[] getFields()
    {
        return marrFields;
    }

    public IQueryParam[] getParams()
    {
        return marrParams;
    }

    public boolean getCanCreate()
    {
        return (Boolean)getAt(9);
    }

    public boolean getCanEdit()
    {
        return (Boolean)getAt(10);
    }

    public boolean getCanDelete()
    {
        return (Boolean)getAt(11);
    }

    public UUID getQueryType()
    {
        return (UUID)getAt(12);
    }

    private String BuildParamSQL(HashMap<String, java.lang.Object> parrValues)
    	throws JewelEngineException
    {
        String lstrAux;
        int i;

        lstrAux = "";
        for (i = 0; i < marrParams.length; i++)
            lstrAux += marrParams[i].ColumnForFiltering(i == 0 ? "" : mstrType, parrValues);

        return lstrAux;
    }
}
