package Jewel.Engine.Interfaces;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;

import Jewel.Engine.DataAccess.*;
import Jewel.Engine.SysObjects.*;

public interface IEntity
	extends IJewelBase
{
	String Storage();
	String SQLColumnsForMultiSelect(String pstrColPrefix, R<Integer> plngNextTbl, boolean pbRecurse) throws JewelEngineException, SQLException;
	String SQLTablesForMultiSelect(R<Integer> plngNextTbl, boolean pbRecurse) throws JewelEngineException, SQLException;
    String SQLColumnForReportFilter(String pstrAlias, String pstrColPrefix, R<Integer> plngNextTbl, boolean pbRecurse) throws JewelEngineException, SQLException;
	String SQLForSelectMulti() throws JewelEngineException, SQLException;
    String SQLForSelectMultiFiltered(int[] parrMembers, Object[] parrValues) throws JewelEngineException, SQLException;
    String SQLForSelectAll();
    String SQLForSelectByKey(UUID pidKey);
    String SQLForSelectByMembers(int[] parrMembers, Object[] parrValues, int[] parrSorts) throws JewelEngineException;
    String SQLForSelectForReports(String[] parrColAliases, String[] parrCriteria, String[] parrSorts) throws JewelEngineException, SQLException;

	UUID Insert(SQLServer pdb, Object[] parrData) throws SQLException, JewelEngineException;
	void Update(SQLServer pdb, UUID pidKey, Object[] parrData) throws SQLException, JewelEngineException;
	void Delete(SQLServer pdb, UUID pidKey) throws SQLException, JewelEngineException;
	ResultSet SelectAll(SQLServer pdb) throws SQLException;
	ResultSet SelectByKey(SQLServer pdb, UUID pidKey) throws SQLException;
	ResultSet SelectByMembers(SQLServer pdb, int[] parrMembers, Object[] parrValues, int[] parrSorts) throws SQLException, JewelEngineException;
    ResultSet SelectForReports(SQLServer pdb, String[] parrColAliases, String[] parrCriteria, String[] parrSorts) throws SQLException, JewelEngineException;
    void CreateTable(SQLServer pdb) throws SQLException, JewelEngineException;
    void DropTable(SQLServer pdb) throws SQLException;
	void AddNewMember(SQLServer pdb, Object[] parrParams) throws SQLException, JewelEngineException;
	void ChangeMember(SQLServer pdb, int plngMember) throws SQLException;
	void DropMember(SQLServer pdb, int plngMember) throws SQLException;

	String getName();
	IObject getDefObject();
    int getMemberCount();
	INameSpace getMemberOf();
    ObjectBase CreateNew() throws InvocationTargetException, JewelEngineException;
    IQueryDef getDefaultQuery() throws JewelEngineException, SQLException;
    IForm getDefaultSearchForm() throws JewelEngineException, SQLException;
}
