package Jewel.Engine.Extensions;

import java.util.*;

import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class QueryDef_Manager
{
    private QueryDef_Manager()
    {
    }

    public static void Duplicate(UUID pidNameSpace, java.lang.Object[] parrParams)
    {
        MasterDB ldb;
        ObjectMaster lobjQuery, lobjQField, lobjQParam;
        IQueryField[] larrFields;
        IQueryParam[] larrParams;
        int i;

        try
        {
	        ldb = new MasterDB();

	        lobjQuery = new ObjectMaster();
	        lobjQuery.LoadAt(EntityGUIDs.E_QueryDef, QueryDef.GetInstance((UUID)parrParams[0]));

	        if (lobjQuery.getAt(8) instanceof String)
	            lobjQuery.setAt(8, ((String)lobjQuery.getAt(8) + " - Copy"));
	        else
	            lobjQuery.setAt(8, "Copy");

		    lobjQuery.SaveToDb(ldb);

	        larrFields = QueryDef.GetInstance((UUID)parrParams[0]).getFields();

	        for (i = 0; i < larrFields.length; i++)
	        {
	            lobjQField = new ObjectMaster();
	            lobjQField.LoadAt(EntityGUIDs.E_QueryField, QueryField.GetInstance(larrFields[i].getKey()));

	            lobjQField.setAt(Miscellaneous.FKOwner_In_QueryField, lobjQuery.getKey());

		        lobjQField.SaveToDb(ldb);
		    }

	        larrParams = QueryDef.GetInstance((UUID)parrParams[0]).getParams();

	        for (i = 0; i < larrParams.length; i++)
	        {
	            lobjQParam = new ObjectMaster();
	            lobjQParam.LoadAt(EntityGUIDs.E_QueryParam, QueryField.GetInstance(larrParams[i].getKey()));

		        lobjQParam.setAt(Miscellaneous.FKOwner_In_QueryParam, lobjQuery.getKey());

	            lobjQParam.SaveToDb(ldb);
	        }

	        ldb.Disconnect();
        }
        catch (Throwable e)
        {
        	throw new RuntimeException(e.getMessage(), e);
        }
    }
}
