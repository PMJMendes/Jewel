package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class ObjMember
	extends ObjectBase
	implements IObjMember
{
	private ITypeDef mrefDef;

	public static ObjMember GetInstance(UUID pidKey)
		throws JewelEngineException
	{
		return (ObjMember)Engine.GetCache(true).getAt(EntityGUIDs.E_ObjMember, pidKey);
	}

	public static ObjMember GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
		return (ObjMember)Engine.GetCache(true).getAt(EntityGUIDs.E_ObjMember, prsObject);
	}

	public void Initialize()
		throws JewelEngineException
	{
		mrefDef = TypeDef.GetInstance((UUID)getAt(4));
	}

	public String ColumnForSelect(String pstrTblAlias)
	{
		if (getAt(9) == null)
			return "";
		else
			return ", " + pstrTblAlias + ".[" + (String)getAt(9) + "] [" + (String)getAt(2) + "]";
	}

	public String ColumnForInsert()
	{
		if (getAt(9) == null)
			return "";
		else
			return ", [" + (String)getAt(9) + "]";
	}

	public String ColumnForSort(String pstrAscDesc)
	{
		if (getAt(9) == null)
			return "";
		else
			return "[" + (String)getAt(9) + "] " + pstrAscDesc + ", ";
	}

	public String ValueForInsert(java.lang.Object pobjValue, ArrayList<Blob> parrParams)
		throws JewelEngineException
	{
		if (getAt(9) == null)
			return "";
		else
			return ", " + mrefDef.TranslateValue(pobjValue, false, parrParams);
	}

	public String ValueForUpdate(java.lang.Object pobjValue, ArrayList<Blob> parrParams)
		throws JewelEngineException
	{
		if (getAt(9) == null)
			return "";
		else
			return ", [" + (String)getAt(9) + "] = " + mrefDef.TranslateValue(pobjValue, false, parrParams);
	}

    public String ValueForFilter(String pstrTblAlias, java.lang.Object pobjValue)
    	throws JewelEngineException
    {
        String lstrValue, lstrEquals;

        if (getAt(9) == null)
            return "";

        if (!pstrTblAlias.equals(""))
            pstrTblAlias += ".";

        lstrValue = mrefDef.TranslateValue(pobjValue, true, null);
        if (lstrValue.equals("NULL") || lstrValue.equals("NOT NULL"))
            lstrEquals = " IS ";
        else
            if (TypeDefGUIDs.T_String.equals(mrefDef.getKey()))
                lstrEquals = " LIKE ";
            else
                lstrEquals = " = ";

        if ((TypeDefGUIDs.T_Date.equals(mrefDef.getKey())) && (lstrValue.indexOf('%') >= 0))
            return " AND CONVERT(NVARCHAR, " + pstrTblAlias + "[" + (String)getAt(9) + "], 121) LIKE " + lstrValue; ;

        return " AND " + pstrTblAlias + "[" + (String)getAt(9) + "]" + lstrEquals + lstrValue;
    }

	public String ColumnForCreateTable(UUID pidNSpace)
		throws JewelEngineException, SQLException
	{
		return ", " + ColumnForAlterTable(pidNSpace);
	}

	public String ColumnForAlterTable(UUID pidNSpace)
		throws JewelEngineException, SQLException
	{
		String lstrAux;

		if (getAt(9) == null)
			return "";

		lstrAux = "[" + (String)getAt(9) + "] " + mrefDef.TypeForCreate();

		if (getAt(5) != null)
		{
			lstrAux += "(" + ((Integer)getAt(5)).toString();
			if (getAt(10) != null)
				lstrAux += "," + ((Integer)getAt(10)).toString();
			lstrAux += ")";
		}

		if ( (getAt(8) != null) && (Boolean)getAt(8) )
			lstrAux += " UNIQUE";

		if ( (getAt(7) != null) && !(Boolean)getAt(7) )
			lstrAux += " NOT NULL";

		if (getAt(6) != null)
			lstrAux += " FOREIGN KEY REFERENCES " + Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(6))).Storage() + " ([PK])";

		return lstrAux;
	}

    public String ColumnForModifyTable(/*UUID pidNSpace*/)
	{
		String lstrAux;

		if (getAt(9) == null)
			return "";

		lstrAux = "[" + (String)getAt(9) + "] " + mrefDef.TypeForCreate();

		if (getAt(5) != null )
		{
			lstrAux += "(" + ((Integer)getAt(5)).toString();
			if (getAt(10) != null)
				lstrAux += "," + ((Integer)getAt(10)).toString();
			lstrAux += ")";
		}

		if ( (getAt(7) != null) && !(Boolean)getAt(7) )
            lstrAux += " NOT NULL";

        //if (getAt(6) != null)
        //    lstrAux += " FOREIGN KEY REFERENCES " + Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(6))).Storage() + " ([PK])";

		return lstrAux;
	}

	public String ColumnForTableDrop()
	{
		if (getAt(9) == null)
			return "";

		return "[" + (String)getAt(9) + "]";
	}

	public String ColumnsForMultiSelect(UUID pidNSpace, String pstrColPrefix, int plngTblNum, R<Integer> plngNextTbl, boolean pbRecurse)
		throws JewelEngineException, SQLException
	{
		IEntity lrefEntity;
		String lstrAux;

		if (getAt(9) == null)
			return "";

		lstrAux = ", [t" + Integer.toString(plngTblNum) + "].[" + (String)getAt(9) + "] [" + pstrColPrefix + ":" + (String)getAt(2) + "]";

		if ( (getAt(7) != null) && !(Boolean)getAt(7) )
		{
			if (getAt(6) != null)
			{
				lrefEntity = (IEntity)Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(6)));
				lstrAux += lrefEntity.SQLColumnsForMultiSelect(pstrColPrefix + ":" + (String)getAt(2), plngNextTbl, pbRecurse);
			}
		}
		else if (pbRecurse)
		{
			if (getAt(6) != null)
			{
				lrefEntity = (IEntity)Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(6)));
				lstrAux += lrefEntity.SQLColumnsForMultiSelect(pstrColPrefix + ":" + (String)getAt(2), plngNextTbl, false);
			}
		}

		plngNextTbl.set(plngNextTbl.get() + 1);
		return lstrAux;
	}

	public String ColumnsForSimpleSelect(UUID pidNSpace, String pstrColPrefix, int plngTblNum, R<Integer> plngNextTbl)
		throws JewelEngineException, SQLException
	{
		String lstrAux;

		if (getAt(9) == null)
			return "";

		lstrAux = ", [t" + Integer.toString(plngTblNum) + "].[" + (String)getAt(9) + "] [" + pstrColPrefix + ":" + (String)getAt(2) + "]";

		plngNextTbl.set(plngNextTbl.get() + 1);
		return lstrAux;
	}

	public String TablesForMultiSelect(UUID pidNSpace, int plngTblNum, R<Integer> plngNextTbl, boolean pbRecurse)
		throws JewelEngineException, SQLException
	{
		IEntity lrefEntity;
		String lstrJoin;
		String lstrAux;

		if (getAt(9) == null )
			return "";

		lstrAux = "";

		if ( pbRecurse )
			lstrJoin = " INNER JOIN ";
		else
			lstrJoin = " LEFT JOIN ";

		if ( (getAt(7) != null) && !(Boolean)getAt(7) )
		{
			if (getAt(6) != null)
			{
				lrefEntity = (IEntity)Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(6)));
				lstrAux = lstrJoin + lrefEntity.Storage() + " [t" + plngNextTbl.toString() + "]" +
						" ON [t" + plngNextTbl.toString() + "].[PK] = " +
						"[t" + Integer.toString(plngTblNum) + "].[" + (String)getAt(9) + "]" +
						lrefEntity.SQLTablesForMultiSelect(plngNextTbl, pbRecurse);
			}
		}
		else if ( pbRecurse )
		{
			if (getAt(6) != null)
			{
				lrefEntity = (IEntity)Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(6)));
				lstrAux = " LEFT JOIN " + lrefEntity.Storage() + " [t" + plngNextTbl.toString() + "]" +
						" ON [t" + plngNextTbl.toString() + "].[PK] = " +
						"[t" + Integer.toString(plngTblNum) + "].[" + (String)getAt(9) + "]" +
						lrefEntity.SQLTablesForMultiSelect(plngNextTbl, false);
			}
		}

		plngNextTbl.set(plngNextTbl.get() + 1);
		return lstrAux;
    }

    public String ColumnForReportFilter(UUID pidNSpace, String pstrAlias, String pstrColPrefix, int plngTblNum, R<Integer> plngNextTbl, boolean pbRecurse)
    	throws JewelEngineException, SQLException
    {
        IEntity lrefEntity;
        String lstrAux;

        if (getAt(9) == null)
            return "";

        if (pstrAlias.equals("[" + pstrColPrefix + ":" + (String)getAt(2) + "]"))
            return "[t" + Integer.toString(plngTblNum) + "].[" + (String)getAt(9) + "]";

        lstrAux = "";
        if ((getAt(7) != null) && !(Boolean)getAt(7))
        {
            if (getAt(6) != null)
            {
                lrefEntity = (IEntity)Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(6)));
                lstrAux = lrefEntity.SQLColumnForReportFilter(pstrAlias, pstrColPrefix + ":" + (String)getAt(2), plngNextTbl, pbRecurse);
            }
        }
        else if (pbRecurse)
        {
            if (getAt(6) != null)
            {
                lrefEntity = (IEntity)Entity.GetInstance(Engine.FindEntity(pidNSpace, (UUID)getAt(6)));
                lstrAux = lrefEntity.SQLColumnForReportFilter(pstrAlias, pstrColPrefix + ":" + (String)getAt(2), plngNextTbl, false);
            }
        }

        plngNextTbl.set(plngNextTbl.get() + 1);
        return lstrAux;
    }

	public String getName()
	{
		return (String)getAt(2);
	}

	public int getNOrder()
	{
		return (Integer)getAt(1);
	}

	public UUID getRefersToObj()
	{
		return (UUID)getAt(6);
	}

    public boolean getCanBeNull()
    {
        if (getAt(7) != null)
            return (Boolean)getAt(7);
        return true;
    }

    public ITypeDef getTypeDefRef()
    {
        return mrefDef;
    }

    public void AddToAllObjects(java.lang.Object[] parrParams)
    	throws SQLException, JewelEngineException
    {
        MasterDB ldb;
        ResultSet lrs;
        int[] larrIndexes;
        java.lang.Object[] larrParams;
        Stack<UUID> lstAux;

        if (!getNameSpace().equals(NameSpaceGUIDs.N_MADDS))
            return;

        larrIndexes = new int[1];
        larrIndexes[0] = 1;
        larrParams = new java.lang.Object[1];
        larrParams[0] = parrParams[0];

        lstAux = new Stack<UUID>();

        ldb = new MasterDB();

        lrs = Entity.GetInstance(EntityGUIDs.E_Entity).SelectByMembers(ldb, larrIndexes, larrParams, new int[0]);
        while (lrs.next())
            lstAux.push(UUID.fromString(lrs.getString(1)));
        lrs.close();

        while (lstAux.size() > 0)
            Entity.GetInstance((UUID)lstAux.pop()).AddNewMember(ldb, parrParams);

        ldb.Disconnect();
    }

    public void ChangeInAllObjects(java.lang.Object[] parrParams)
    	throws SQLException, JewelEngineException
    {
        MasterDB ldb;
        ResultSet lrs;
        int[] larrIndexes;
        java.lang.Object[] larrParams;
        Stack<UUID> lstAux;

        if (!getNameSpace().equals(NameSpaceGUIDs.N_MADDS))
            return;

        larrIndexes = new int[1];
        larrIndexes[0] = 1;
        larrParams = new java.lang.Object[1];
        larrParams[0] = parrParams[0];

        lstAux = new Stack<UUID>();

        ldb = new MasterDB();

        lrs = Entity.GetInstance(EntityGUIDs.E_Entity).SelectByMembers(ldb, larrIndexes, larrParams, new int[0]);
        while (lrs.next())
            lstAux.push(UUID.fromString(lrs.getString(1)));
        lrs.close();

        while (lstAux.size() > 0)
            Entity.GetInstance((UUID)lstAux.pop()).ChangeMember(ldb, (Integer)parrParams[1] - 1);

        ldb.Disconnect();
    }

    public void DropFromAllObjects(java.lang.Object[] parrParams)
    	throws SQLException, JewelEngineException
    {
        MasterDB ldb;
        ResultSet lrs;
        int[] larrIndexes;
        java.lang.Object[] larrParams;
        Stack<UUID> lstAux;

        if (!getNameSpace().equals(NameSpaceGUIDs.N_MADDS))
            return;

        larrIndexes = new int[1];
        larrIndexes[0] = 1;
        larrParams = new java.lang.Object[1];
        larrParams[0] = parrParams[0];

        lstAux = new Stack<UUID>();

        ldb = new MasterDB();

        lrs = Entity.GetInstance(EntityGUIDs.E_Entity).SelectByMembers(ldb, larrIndexes, larrParams, new int[0]);
        while (lrs.next())
            lstAux.push(UUID.fromString(lrs.getString(1)));
        lrs.close();

        while (lstAux.size() > 0)
            Entity.GetInstance((UUID)lstAux.pop()).DropMember(ldb, (Integer)parrParams[1] - 1);

        ldb.Disconnect();
    }
}
