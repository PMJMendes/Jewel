package Jewel.Engine.Interfaces;

import java.sql.*;
import java.util.*;

import Jewel.Engine.DataAccess.*;
import Jewel.Engine.SysObjects.*;

public interface IQueryDef
	extends IJewelBase
{
	ResultSet SelectAll(SQLServer pdb, UUID pidNSpace, Hashtable<String, Object> parrValues) throws SQLException, JewelEngineException;
	ResultSet SelectByParam(SQLServer pdb, UUID pidNSpace, Object pobjParamValue, Hashtable<String, Object> parrValues) throws SQLException, JewelEngineException;
	ResultSet SelectByKey(SQLServer pdb, UUID pidNSpace, UUID pidKey) throws SQLException, JewelEngineException;
	ResultSet SelectByMembers(SQLServer pdb, UUID pidNSpace, int[] parrMembers, Object[] parrValues, Hashtable<String, Object> parrPValues) throws SQLException, JewelEngineException;

	IObject getDriverObject();
	UUID getParamType();
	int getParamAppliesTo();
	UUID getEditorViewID();
	int Width(int plngIndex);
	boolean getReadOnly();
	UUID getReportID();
	IQueryField[] getFields();
	IQueryParam[] getParams();
	boolean getCanCreate();
	boolean getCanEdit();
	boolean getCanDelete();
	UUID getQueryType();
}
