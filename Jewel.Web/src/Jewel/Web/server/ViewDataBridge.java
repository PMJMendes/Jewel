package Jewel.Web.server;

import java.util.*;

import Jewel.Engine.Constants.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Web.shared.*;

public class ViewDataBridge
{
	public static void ClientToServer(UUID pidView, ObjectBase pobjDest, DataObject prefData)
		throws JewelWebException
	{
		IView lrefView;
		IViewTab[] larrTabs;
		int i;

		try
		{
			lrefView = View.GetInstance(pidView);
		}
		catch (Throwable e)
		{
        	throw new JewelWebException(e.getMessage(), e);
		}

		larrTabs = lrefView.getTabs();

		for ( i = 0; i < larrTabs.length; i++ )
		{
			if ( ViewTypeGUIDs.VT_Form.equals(larrTabs[i].getType()) )
			{
				FormDataBridge.WriteToObject(larrTabs[i].getFormID(), pobjDest, prefData.marrData[i]);
			}
			if ( ViewTypeGUIDs.VT_Grid.equals(larrTabs[i].getType()) )
			{
			}
			if ( ViewTypeGUIDs.VT_Preview.equals(larrTabs[i].getType()) )
			{
			}
		}
	}

	public static DataObject ServerToClient(UUID pidView, ObjectBase prefSource, UUID pidNSpace)
		throws JewelWebException
	{
		IView lrefView;
		IViewTab[] larrTabs;
		DataObject lobjRes;
		int i;

		try
		{
			lrefView = View.GetInstance(pidView);
		}
		catch (Throwable e)
		{
        	throw new JewelWebException(e.getMessage(), e);
		}

		larrTabs = lrefView.getTabs();

		lobjRes = new DataObject();
		if ( prefSource.getKey() != null )
			lobjRes.mstrID = prefSource.getKey().toString();
		lobjRes.mstrNameSpace = prefSource.getNameSpace().toString();
		lobjRes.marrData = new String[larrTabs.length][];
		lobjRes.mstrDisplayName = prefSource.getLabel();

		for ( i = 0; i < larrTabs.length; i++ )
		{
			if ( ViewTypeGUIDs.VT_Form.equals(larrTabs[i].getType()) )
			{
				lobjRes.marrData[i] = FormDataBridge.ReadFromObject(larrTabs[i].getFormID(), prefSource, pidNSpace);
			}
			if ( ViewTypeGUIDs.VT_Grid.equals(larrTabs[i].getType()) )
			{
				lobjRes.marrData[i] = null;
			}
			if ( ViewTypeGUIDs.VT_Preview.equals(larrTabs[i].getType()) )
			{
				lobjRes.marrData[i] = null;
			}
		}

		return lobjRes;
	}

	public static java.lang.Object[] GetNonObjectParams(UUID pidView, int plngOrder, ObjectBase pobjSource, DataObject prefValues)
		throws JewelWebException
	{
		IView lrefView;
	
		try
		{
			lrefView = View.GetInstance(pidView);
		}
		catch (Throwable e)
		{
	    	throw new JewelWebException(e.getMessage(), e);
		}
		
		return FormDataBridge.GetNonObjectParams(lrefView.getTabs()[plngOrder].getFormID(), pobjSource.getData(),
				prefValues.marrData[plngOrder]);
	}

	public static void SetNonObjectParams(UUID pidView, int plngOrder, ObjectBase pobjSource, DataObject prefValues,
			java.lang.Object[] parrParams, UUID pidNSpace)
		throws JewelWebException
	{
		IView lrefView;
		
		try
		{
			lrefView = View.GetInstance(pidView);
		}
		catch (Throwable e)
		{
	    	throw new JewelWebException(e.getMessage(), e);
		}
		
		FormDataBridge.SetNonObjectParams(lrefView.getTabs()[plngOrder].getFormID(), pobjSource.getData(),
				prefValues.marrData[plngOrder], parrParams, pidNSpace);
	}
}
