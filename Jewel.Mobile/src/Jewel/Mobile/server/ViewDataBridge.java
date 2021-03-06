package Jewel.Mobile.server;

import java.util.UUID;

import Jewel.Engine.Constants.ViewTypeGUIDs;
import Jewel.Engine.Implementation.View;
import Jewel.Engine.Interfaces.IView;
import Jewel.Engine.Interfaces.IViewTab;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Mobile.shared.DataObject;
import Jewel.Mobile.shared.JewelMobileException;

public class ViewDataBridge
{
	public static void ClientToServer(UUID pidView, ObjectBase pobjDest, DataObject prefData)
		throws JewelMobileException
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
        	throw new JewelMobileException(e.getMessage(), e);
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
		}
	}

	public static DataObject ServerToClient(UUID pidView, ObjectBase prefSource, UUID pidNSpace)
		throws JewelMobileException
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
        	throw new JewelMobileException(e.getMessage(), e);
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
		}

		return lobjRes;
	}

	public static java.lang.Object[] GetNonObjectParams(UUID pidView, int plngOrder, ObjectBase pobjSource, DataObject prefValues)
		throws JewelMobileException
	{
		IView lrefView;
	
		try
		{
			lrefView = View.GetInstance(pidView);
		}
		catch (Throwable e)
		{
	    	throw new JewelMobileException(e.getMessage(), e);
		}
		
		return FormDataBridge.GetNonObjectParams(lrefView.getTabs()[plngOrder].getFormID(), pobjSource.getData(),
				prefValues.marrData[plngOrder]);
	}

	public static void SetNonObjectParams(UUID pidView, int plngOrder, ObjectBase pobjSource, DataObject prefValues,
			java.lang.Object[] parrParams, UUID pidNSpace)
		throws JewelMobileException
	{
		IView lrefView;
		
		try
		{
			lrefView = View.GetInstance(pidView);
		}
		catch (Throwable e)
		{
	    	throw new JewelMobileException(e.getMessage(), e);
		}
		
		FormDataBridge.SetNonObjectParams(lrefView.getTabs()[plngOrder].getFormID(), pobjSource.getData(),
				prefValues.marrData[plngOrder], parrParams, pidNSpace);
	}
}
