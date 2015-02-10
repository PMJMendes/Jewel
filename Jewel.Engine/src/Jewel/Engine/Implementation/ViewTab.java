package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class ViewTab
	extends ObjectBase
	implements IViewTab
{
	public static ViewTab GetInstance(UUID pidKey)
		throws JewelEngineException
	{
		return (ViewTab)Engine.GetCache(true).getAt(EntityGUIDs.E_ViewTab, pidKey);
	}

	public static ViewTab GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
		return (ViewTab)Engine.GetCache(true).getAt(EntityGUIDs.E_ViewTab, prsObject);
	}

	public void Initialize()
	{
	}

	public String getName()
	{
		return (String)getAt(2);
	}

	public UUID getType()
	{
		return (UUID)getAt(3);
	}

	public UUID getFormID()
	{
		return (UUID)getAt(4);
	}

	public UUID getQueryID()
	{
		return (UUID)getAt(5);
	}

	public UUID getReportID()
	{
		return (UUID)getAt(6);
	}
}
