package Jewel.Engine.Extensions;

import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Security.*;
import Jewel.Engine.SysObjects.*;

public class User_Manager
{
    public static void ChangePassword(UUID pidNameSpace, java.lang.Object[] parrParams)
    	throws JewelEngineException
    {
        Password lpwdOld, lpwdNew, lpwdConfirm;
        UUID lidUser, lidNSpace;
        User lobjUser, lobjNew;
        MasterDB ldb;

        lpwdNew = (parrParams[1] instanceof Password ? (Password)parrParams[1] : new Password());
        lpwdConfirm = (parrParams[2] instanceof Password ? (Password)parrParams[2] : new Password());

        if (!lpwdNew.GetEncrypted().equals(lpwdConfirm.GetEncrypted()))
            throw new JewelEngineException("New password and password confirmation are different.");

        lpwdOld = (parrParams[0] instanceof Password ? (Password)parrParams[0] : new Password());
        lidUser = Engine.getCurrentUser();
        lidNSpace = Engine.getCurrentNameSpace();

        try
        {
			lobjUser = User.GetInstance(lidNSpace, lidUser);
		}
        catch (Throwable e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
		}

        if (!lobjUser.CheckPassword(lpwdOld))
            throw new JewelEngineException("Invalid password.");

        lobjNew = new User();
        lobjNew.LoadAt(lobjUser);
        lobjNew.setAt(2, (lpwdNew.GetEncrypted().equals("") ? null : lpwdNew));

        try
        {
			ldb = new MasterDB();
	        lobjNew.SaveToDb(ldb);
	        ldb.Disconnect();
		}
        catch (Throwable e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
		}

        Engine.GetCache(true).DeleteAt(lobjUser.getDefinition().getKey(), lobjUser.getKey());

        throw new JewelEngineException("Password changed.");
    }
}
