package Jewel.Engine.Extensions;

import java.util.*;

import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class Form_Manager
{
    private Form_Manager()
    {
    }

    public static void Duplicate(UUID pidNameSpace, java.lang.Object[] parrParams)
    {
        MasterDB ldb;
        ObjectMaster lobjForm, lobjFField;
        IFormField[] larrFields;
        int i;

        try
        {
	        ldb = new MasterDB();

	        lobjForm = new ObjectMaster();
	        lobjForm.LoadAt(EntityGUIDs.E_Form, Form.GetInstance((UUID)parrParams[0]));

	        if (lobjForm.getAt(0) instanceof String)
	            lobjForm.setAt(0, ((String)lobjForm.getAt(0) + " - Copy"));
	        else
	            lobjForm.setAt(0, "Copy");

	        lobjForm.SaveToDb(ldb);

	        larrFields = Form.GetInstance((UUID)parrParams[0]).getFields();

	        for (i = 0; i < larrFields.length; i++)
	        {
	            lobjFField = new ObjectMaster();
	            lobjFField.LoadAt(EntityGUIDs.E_FormField, FormField.GetInstance(larrFields[i].getKey()));

	            lobjFField.setAt(Miscellaneous.FKForm_In_FormField, lobjForm.getKey());

	            lobjFField.SaveToDb(ldb);
	        }

	        ldb.Disconnect();
        }
        catch (Throwable e)
        {
        	throw new RuntimeException(e.getMessage(), e);
        }
    }
}
