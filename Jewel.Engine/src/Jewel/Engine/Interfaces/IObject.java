package Jewel.Engine.Interfaces;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import Jewel.Engine.SysObjects.*;

public interface IObject
	extends IJewelBase
{
		String Storage();
		String ColumnsForSelect(String pstrTblAlias);
		String ColumnsForInsert();
		String ValuesForInsert(Object[] parrData, ArrayList<Blob> parrParams) throws JewelEngineException;
		String ValuesForUpdate(Object[] parrData, ArrayList<Blob> parrParams) throws JewelEngineException;
		String FilterByMembers(String pstrTblAlias, int[] parrMembers, Object[] parrValues) throws JewelEngineException;
		String OrderByMembers(String pstrTblAlias, int[] parrMembers);
		String ColumnsForCreateTable(UUID pidNSpace) throws JewelEngineException, SQLException;
		String ColumnForAlterTable(UUID pidNSpace, int plngMember) throws JewelEngineException, SQLException;
        String ColumnForModifyTable(int plngMember);
        //String ColumnForModifyTable(UUID pidNSpace, int plngMember);
		String ColumnForTableDrop(int plngMember);
		String ColumnsForMultiSelect(UUID pidNSpace, String pstrColPrefix, R<Integer> plngNextTbl, boolean pbRecurse) throws JewelEngineException, SQLException;
		String ColumnsForSimpleSelect(UUID pidNSpace, String pstrColPrefix, R<Integer> plngNextTbl) throws JewelEngineException, SQLException;
		String TablesForMultiSelect(UUID pidNSpace, R<Integer> plngNextTbl, boolean pbRecurse) throws JewelEngineException, SQLException;
        String ColumnForReportFilter(UUID pidNSpace, String pstrAlias, String pstrColPrefix, R<Integer> plngNextTbl, boolean pbRecurse) throws JewelEngineException, SQLException;

		String getName();
        int getMemberCount();
		IObjMember[] getMembers();
		IDynaCode[] getActions() throws SQLException, JewelEngineException;
		int MemberByNOrd(int plngNOrd);
        IQueryDef DefaultQuery() throws SQLException, JewelEngineException;
        ObjectBase CreateNew() throws InvocationTargetException, JewelEngineException;
        IDynaCode GetActionAt(int plngIndex) throws SQLException, JewelEngineException;
        //void RunCustomCode(ObjectBase pobjSource, int plngIndex, Object[] parrParams);
        String getAssembly();
        String getClassName();
        Class<?> getClassType();
}
