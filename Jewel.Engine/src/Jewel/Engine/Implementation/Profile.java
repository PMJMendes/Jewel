package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class Profile
	extends ObjectBase
	implements IProfile
{
	private IWorkspace[] marrWorkspaces;
    private IPermission[] marrPermissions;

    public static Profile GetInstance(UUID pidNameSpace, UUID pidKey)
    	throws JewelEngineException, SQLException
	{
        return (Profile)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_Profile), pidKey);
	}

	public void Initialize()
		throws JewelEngineException
	{
		MasterDB ldb;
		ResultSet lrsColumns;
		Entity lrefWorkspace, lrefPermission;
		int[] larrMembers;
		java.lang.Object[] larrParams;
        int[] larrSorts;
		ArrayList<IWorkspace> larrAux;
		ArrayList<IPermission> larrAux2;

		larrAux = new ArrayList<IWorkspace>();

		larrMembers = new int[1];
		larrMembers[0] = Miscellaneous.FKProfile_In_Workspace;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();

		try
		{
			lrefWorkspace = Entity.GetInstance(Engine.FindEntity(getDefinition().getMemberOf().getKey(), ObjectGUIDs.O_Workspace));

			ldb = new MasterDB();
			lrsColumns = lrefWorkspace.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
			while ( lrsColumns.next() )
	            larrAux.add((IWorkspace)Workspace.GetInstance(getDefinition().getMemberOf().getKey(), lrsColumns));
			lrsColumns.close();
			ldb.Disconnect();
		}
		catch (JewelEngineException e)
		{
			throw e;
		}
		catch (SQLException e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}

        marrWorkspaces = larrAux.toArray(new IWorkspace[larrAux.size()]);

        larrAux2 = new ArrayList<IPermission>();

        larrMembers = new int[1];
        larrMembers[0] = Miscellaneous.FKProfile_In_Permission;
        larrParams = new java.lang.Object[1];
        larrParams[0] = getKey();
        larrSorts = new int[1];
        larrSorts[0] = Miscellaneous.NOrd_In_Permission;

		try
		{
			lrefPermission = Entity.GetInstance(Engine.FindEntity(getDefinition().getMemberOf().getKey(), ObjectGUIDs.O_Permission));

	        ldb = new MasterDB();
	        lrsColumns = lrefPermission.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
	        while (lrsColumns.next())
	            larrAux2.add((IPermission)Permission.GetInstance(getDefinition().getMemberOf().getKey(), lrsColumns));
	        lrsColumns.close();
	        ldb.Disconnect();
		}
		catch (JewelEngineException e)
		{
			throw e;
		}
		catch (SQLException e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}

        marrPermissions = larrAux2.toArray(new IPermission[larrAux2.size()]);
	}

	public IWorkspace[] getWorkspaces()
	{
		return marrWorkspaces;
	}

    public IPermission[] getPermissions()
    {
        return marrPermissions;
    }
}
