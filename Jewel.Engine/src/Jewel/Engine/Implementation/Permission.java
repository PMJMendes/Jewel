package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class Permission
	extends ObjectBase
	implements IPermission
{
    private IProfile mrefProfile;
    private ITreeNode mrefTreeNode;

    public static Permission GetInstance(UUID pidNameSpace, UUID pidKey)
    	throws JewelEngineException, SQLException
	{
		return (Permission)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_Permission), pidKey);
	}

    public static Permission GetInstance(UUID pidNameSpace, ResultSet prsObject)
    	throws SQLException, JewelEngineException
	{
		return (Permission)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_Permission), prsObject);
	}

	public void Initialize()
		throws JewelEngineException
	{
        try
        {
            mrefProfile = Profile.GetInstance(getDefinition().getMemberOf().getKey(), (UUID)getAt(0));
			mrefTreeNode = TreeNode.GetInstance(getDefinition().getMemberOf().getKey(), (UUID)getAt(1));
		}
        catch (JewelEngineException e)
        {
        	throw e;
		}
        catch (SQLException e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
		}
    }

    public IProfile getMemberOf()
    {
        return mrefProfile;
    }

    public ITreeNode getTreeNode()
    {
        return mrefTreeNode;
    }
}
