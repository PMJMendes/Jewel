package Jewel.Engine.Extensions;

import java.util.*;

import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class View_Manager
{
    private View_Manager()
    {
    }

    public static void Duplicate(UUID pidNameSpace, java.lang.Object[] parrParams)
    {
        MasterDB ldb;
        ObjectMaster lobjView, lobjViewTabs;
        IViewTab[] larrTabs;
        int i;

        try
        {
	        ldb = new MasterDB();

	        lobjView = new ObjectMaster();
	        lobjView.LoadAt(EntityGUIDs.E_View, View.GetInstance((UUID)parrParams[0]));

	        if (lobjView.getAt(2) instanceof String)
	            lobjView.setAt(2, ((String)lobjView.getAt(2) + " - Copy"));
	        else
	            lobjView.setAt(2, "Copy");

	        lobjView.SaveToDb(ldb);

	        larrTabs = View.GetInstance((UUID)parrParams[0]).getTabs();

	        for (i = 0; i < larrTabs.length; i++)
	        {
	            lobjViewTabs = new ObjectMaster();
	            lobjViewTabs.LoadAt(EntityGUIDs.E_ViewTab, ViewTab.GetInstance(larrTabs[i].getKey()));

	            lobjViewTabs.setAt(Miscellaneous.FKOwner_In_ViewTab, lobjView.getKey());

	            lobjViewTabs.SaveToDb(ldb);
	        }

	        ldb.Disconnect();
        }
        catch (Throwable e)
        {
        	throw new RuntimeException(e.getMessage(), e);
        }
    }
}
