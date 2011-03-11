package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class QueryField
	extends ObjectBase
	implements IQueryField
{
	public static QueryField GetInstance(UUID pidKey)
		throws JewelEngineException
	{
		return (QueryField)Engine.GetCache(true).getAt(EntityGUIDs.E_QueryField, pidKey);
	}

	public static QueryField GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
		return (QueryField)Engine.GetCache(true).getAt(EntityGUIDs.E_QueryField, prsObject);
	}

	public void Initialize()
	{
	}

	public String ColumnForSelect()
	{
		if (getAt(3) == null)
			return "";
		else
			return ", " + (String)getAt(3) + " [" + (String)getAt(2) + "]";
	}

	public int getWidth()
	{
		return (Integer)getAt(4);
	}

	public String getHeader()
	{
		return (String)getAt(2);
	}
}
