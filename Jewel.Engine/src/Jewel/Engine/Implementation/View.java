package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class View
	extends ObjectBase
	implements IView
{
    private IObject mrefEditedObject;
	private IViewTab[] marrTabs;

	public static View GetInstance(UUID pidKey)
		throws JewelEngineException
	{
		return (View)Engine.GetCache(true).getAt(EntityGUIDs.E_View, pidKey);
	}

	public static View GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
		return (View)Engine.GetCache(true).getAt(EntityGUIDs.E_View, prsObject);
	}

	public void Initialize()
		throws JewelEngineException
	{
		MasterDB ldb;
		ResultSet lrsFields;
		Entity lrefFormField;
		int[] larrMembers;
		java.lang.Object[] larrParams;
		int[] larrSorts;
		ArrayList<IViewTab> larrList;

        if (getAt(0) != null)
            mrefEditedObject = (IObject)Object.GetInstance((UUID)getAt(0));

		larrList = new ArrayList<IViewTab>();

		larrMembers = new int[1];
		larrMembers[0] = Miscellaneous.FKOwner_In_ViewTab;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();
		larrSorts = new int[1];
		larrSorts[0] = Miscellaneous.NOrd_In_ViewTab;

		try
		{
			lrefFormField = Entity.GetInstance(EntityGUIDs.E_ViewTab);
	
			ldb = new MasterDB();
			lrsFields = lrefFormField.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
			while ( lrsFields.next() )
				larrList.add((IViewTab)ViewTab.GetInstance(lrsFields));
			lrsFields.close();
			ldb.Disconnect();
	
			marrTabs = (IViewTab[])larrList.toArray(new IViewTab[larrList.size()]);
		}
		catch (SQLException e)
		{
			throw new JewelEngineException("Unexpected database exception in View Initialize", e);
		}
	}

	public IObject getEditedObject()
	{
		return mrefEditedObject;
	}

	public IViewTab[] getTabs()
	{
		return marrTabs;
	}
}
