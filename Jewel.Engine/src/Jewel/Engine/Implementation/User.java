package Jewel.Engine.Implementation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.Security.*;
import Jewel.Engine.SysObjects.*;

public class User
	extends ObjectBase
	implements IUser
{
	private IProfile mrefProfile;

    public static User GetInstance(UUID pidNameSpace, UUID pidKey)
    	throws JewelEngineException
	{
        return (User)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_User), pidKey);
	}

    public static User GetInstance(UUID pidNameSpace, ResultSet prsObject)
    	throws JewelEngineException, SQLException
	{
        return (User)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_User), prsObject);
	}

	public void Initialize()
		throws JewelEngineException
	{
        try
        {
			mrefProfile = Profile.GetInstance(getDefinition().getMemberOf().getKey(), (UUID)getAt(3));
		}
        catch (SQLException e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
		}
	}

	public IProfile getProfile()
	{
		if ( mrefProfile == null )
		{
			try
			{
				Initialize();
			}
			catch (Throwable e)
			{
			}
		}

		return mrefProfile;
    }

    public String getFullName()
    {
        if (getAt(0) != null)
            return (String)getAt(0);
        else
            return "";
    }

    public String getDisplayName()
    {
        if (getAt(0) != null)
            return (String)getAt(0);
        else
            return (String)getAt(1);
    }

	public String getUserName()
	{
        return (String)getAt(1);
	}

	public Password getPassword()
		throws JewelEngineException
	{
		java.lang.Object lobjPwd;

		lobjPwd = getAt(Miscellaneous.Password_In_User);

		if ( lobjPwd == null )
			return null;

		if ( lobjPwd instanceof Password )
			return (Password)lobjPwd;

		if ( lobjPwd instanceof String )
			return new Password((String)lobjPwd, true);

		throw new JewelEngineException("Unexpected incompatible type in getPassword");
	}

    public boolean CheckPassword(Password pobjPwd)
    	throws JewelEngineException
    {
        if (getAt(2) != null)
        {
        	if ( getAt(2) instanceof Password )
				return ((Password)getAt(2)).equals(pobjPwd) || ((Password)getAt(2)).equalsShort(pobjPwd);

            return ((String)getAt(2)).equals(pobjPwd.GetEncrypted()) || ((String)getAt(2)).equals(pobjPwd.getShort());
        }

        return pobjPwd.GetEncrypted().equals("");
    }
}
