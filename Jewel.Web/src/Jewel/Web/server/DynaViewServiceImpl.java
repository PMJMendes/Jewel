package Jewel.Web.server;

import java.util.*;

import Jewel.Engine.Engine;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

public class DynaViewServiceImpl
	extends EngineImplementor
	implements DynaViewService
{
	private static final long serialVersionUID = 1L;

	public DynaViewResponse GetTabs(String pstrViewID)
		throws JewelWebException
	{
		IView lrefView;
		IViewTab[] larrTabs;
		int llngCount;
		DynaViewResponse lobjRes;
		int i;

		if ( Engine.getCurrentUser() == null )
			return null;

		if (pstrViewID == null)
			throw new JewelWebException("Unexpected: unidentified View in GetTabs.");

		try
		{
			lrefView = View.GetInstance(UUID.fromString(pstrViewID));
		}
		catch(Throwable e)
		{
        	throw new JewelWebException(e.getMessage(), e);
		}

		larrTabs = lrefView.getTabs();
		llngCount = larrTabs.length;

		lobjRes = new DynaViewResponse();

		lobjRes.marrTabs = new TabObj[llngCount];
		for ( i = 0; i < llngCount; i++ )
		{
			lobjRes.marrTabs[i] = new TabObj();
			lobjRes.marrTabs[i].mstrCaption = larrTabs[i].getName();
			if ( ViewTypeGUIDs.VT_Form.equals(larrTabs[i].getType()) )
			{
				lobjRes.marrTabs[i].mlngType = TabObj.FORMTAB;
				lobjRes.marrTabs[i].mstrID = larrTabs[i].getFormID().toString();
			}
			if ( ViewTypeGUIDs.VT_Grid.equals(larrTabs[i].getType()) )
			{
				lobjRes.marrTabs[i].mlngType = TabObj.GRIDTAB;
				lobjRes.marrTabs[i].mstrID = larrTabs[i].getQueryID().toString();
			}
		}

		return lobjRes;
	}
}
