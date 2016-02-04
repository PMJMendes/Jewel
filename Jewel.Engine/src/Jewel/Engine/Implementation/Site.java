package Jewel.Engine.Implementation;

import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.Security.*;

public class Site
	extends ObjectBase
	implements ISite
{
    public void Initialize()
    {
    }

    public void CreateMADDSLogin(java.lang.Object[] parrParams)
	{
        String lstrConnectString;
        SQLServer lsrv;

        lstrConnectString = (String)parrParams[1];
        if (parrParams[2] instanceof String)
            lstrConnectString += '\\' + (String)parrParams[2];
        if (parrParams[3] instanceof Integer)
            lstrConnectString += ',' + ((Integer)parrParams[3]).toString();

        String lstrDB = (System.getenv(DBConstants.Env_MasterDB) == null ? DBConstants.MasterDB : System.getenv(DBConstants.Env_MasterDB));
        try
        {
        	lsrv = new SQLServer(lstrConnectString,
        			lstrDB,
        			(String)parrParams[4], ((Password)parrParams[5]).GetClear());

	        lsrv.ExecuteSQL("EXEC sp_addlogin '" + DBConstants.User + "', @passwd = '" + DBConstants.Password + "', @defdb = '" + lstrDB + "'");
	        lsrv.ExecuteSQL("EXEC sp_addsrvrolemember '" + DBConstants.User + "', 'securityadmin'");

	        lsrv.Disconnect();
        }
        catch(Throwable e)
        {
        	throw new RuntimeException(e.getMessage(), e);
        }
    }
}
