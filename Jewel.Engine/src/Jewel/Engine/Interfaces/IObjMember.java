package Jewel.Engine.Interfaces;

import java.sql.*;
import java.util.*;

import Jewel.Engine.SysObjects.*;

public interface IObjMember
	extends IJewelBase
{
	String ColumnForSelect(String pstrTblAlias);
	String ColumnForInsert();
	String ColumnForSort(String pstrAscDesc);
	String ValueForInsert(Object pobjValue, ArrayList<Blob> parrParams) throws JewelEngineException;
	String ValueForUpdate(Object pobjValue, ArrayList<Blob> parrParams) throws JewelEngineException;
    String ValueForFilter(String pstrTblAlias, Object pobjValue) throws JewelEngineException;
	String ColumnForCreateTable(UUID pidNSpace) throws JewelEngineException, SQLException;
	String ColumnForAlterTable(UUID pidNSpace) throws JewelEngineException, SQLException;
	String ColumnForModifyTable();
    //String ColumnForModifyTable(UUID pidNSpace);
	String ColumnForTableDrop();
	String ColumnsForMultiSelect(UUID pidNSpace, String pstrColPrefix, int plngTblNum, R<Integer> plngNextTbl, boolean pbRecurse) throws JewelEngineException, SQLException;
	String ColumnsForSimpleSelect(UUID pidNSpace, String pstrColPrefix, int plngTblNum, R<Integer> plngNextTbl) throws JewelEngineException, SQLException;
    String TablesForMultiSelect(UUID pidNSpace, int plngTblNum, R<Integer> plngNextTbl, boolean pbRecurse) throws JewelEngineException, SQLException;
    String ColumnForReportFilter(UUID pidNSpace, String pstrAlias, String pstrColPrefix, int plngTblNum, R<Integer> plngNextTbl, boolean pbRecurse) throws JewelEngineException, SQLException;

	String getName();
	int getNOrder();
	UUID getRefersToObj();
    boolean getCanBeNull();
    ITypeDef getTypeDefRef();
}
