package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class Workspace
	extends ObjectBase
	implements IWorkspace
{
    private IProfile mrefProfile;
	private INameSpace mrefNameSpace;

    public static Workspace GetInstance(UUID pidNameSpace, UUID pidKey)
    	throws JewelEngineException, SQLException
	{
		return (Workspace)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_Workspace), pidKey);
	}

    public static Workspace GetInstance(UUID pidNameSpace, ResultSet prsObject)
    	throws SQLException, JewelEngineException
	{
		return (Workspace)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_Workspace), prsObject);
	}

	public void Initialize()
		throws JewelEngineException
	{
        try
        {
			mrefProfile = Profile.GetInstance(getDefinition().getMemberOf().getKey(), (UUID)getAt(0));
	        mrefNameSpace = NameSpace.GetInstance((UUID)getAt(1));
		}
        catch (SQLException e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
		}
	}

	public INameSpace getWorkNameSpace()
	{
		return mrefNameSpace;
	}

    public IProfile getMemberOf()
    {
        return mrefProfile;
    }
}
