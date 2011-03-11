package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class Form
	extends ObjectBase
	implements IForm
{
    private IFormField[] marrFields;
    private IFormAction[] marrActions;
    private IObject mrefEditedObject;
    private IQueryDef mrefResultsQuery;
    private IApplication mrefApplication;
    private Class<?> mrefType;

	public static Form GetInstance(UUID pidKey)
		throws JewelEngineException
	{
		return (Form)Engine.GetCache(true).getAt(EntityGUIDs.E_Form, pidKey);
    }

    public static Form GetInstance(ResultSet prsObject)
    	throws SQLException, JewelEngineException
    {
        return (Form)Engine.GetCache(true).getAt(EntityGUIDs.E_Form, prsObject);
    }

	public void Initialize()
		throws JewelEngineException
	{
		MasterDB ldb;
		ResultSet lrsFields, lrsActions;
		Entity lrefFormField, lrefFormAction;
		int[] larrMembers;
		java.lang.Object[] larrParams;
		int[] larrSorts;
		ArrayList<IFormField> larrList;
		ArrayList<IFormAction> larrList2;

        if (getAt(1) != null)
            mrefEditedObject = (IObject)Object.GetInstance((UUID)getAt(1));
        if (getAt(2) != null)
            mrefResultsQuery = (IQueryDef)QueryDef.GetInstance((UUID)getAt(2));
        if (getAt(3) != null)
            mrefApplication = (IApplication)Application.GetInstance((UUID)getAt(3));

        if ((mrefApplication != null) && (getAt(4) != null) && (getAt(4) != null))
			try
        	{
				mrefType = /*mrefApplication.Assembly.GetType*/Class.forName(((String)getAt(4)).replaceAll("MADDS", "Jewel"));
			}
        	catch (ClassNotFoundException e)
        	{
        		mrefType = null;
			}
		else
            mrefType = null;

		try
		{
			ldb = new MasterDB();

			larrList = new ArrayList<IFormField>();

			larrMembers = new int[1];
			larrMembers[0] = Miscellaneous.FKForm_In_FormField;
			larrParams = new java.lang.Object[1];
			larrParams[0] = getKey();
			larrSorts = new int[2];
			larrSorts[0] = Miscellaneous.Row_In_FormField;
			larrSorts[1] = Miscellaneous.Col_In_FormField;
	
			lrefFormField = Entity.GetInstance(EntityGUIDs.E_FormField);
	
			lrsFields = lrefFormField.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
			while ( lrsFields.next() )
				larrList.add((IFormField)FormField.GetInstance(lrsFields));
			lrsFields.close();

			marrFields = larrList.toArray(new IFormField[larrList.size()]);

			larrList2 = new ArrayList<IFormAction>();

			larrMembers = new int[1];
			larrMembers[0] = Miscellaneous.FKForm_In_FormAction;
			larrParams = new java.lang.Object[1];
			larrParams[0] = getKey();
			larrSorts = new int[1];
			larrSorts[0] = Miscellaneous.Order_In_FormAction;

			lrefFormAction = Entity.GetInstance(EntityGUIDs.E_FormAction);

			lrsActions = lrefFormAction.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
			while ( lrsActions.next() )
				larrList2.add((IFormAction)FormAction.GetInstance(lrsActions));
			lrsActions.close();

	        ldb.Disconnect();

			marrActions = larrList2.toArray(new IFormAction[larrList2.size()]);
		}
		catch (SQLException e)
		{
			throw new JewelEngineException("Unexpected database exception in Form Initialize", e);
		}
	}

	public String getName()
	{
		return (String)getAt(0);
	}

	public IObject getEditedObject()
	{
		return mrefEditedObject;
	}

	public IQueryDef getResultsQuery()
	{
		return mrefResultsQuery;
	}

	public IFormField[] getFields()
	{
		return marrFields;
	}

	public IFormAction[] getActions()
	{
		return marrActions;
    }

    public String getAssembly()
    {
        if (mrefEditedObject == null)
            return mrefApplication.getAssemblyName();
        return mrefEditedObject.getAssembly();
    }

    public String getClassName()
    {
        if (mrefEditedObject == null)
            return (String)getAt(4);
        return mrefEditedObject.getClassName();
    }

    public Class<?> getClassType()
    {
        return mrefType;
    }
}
