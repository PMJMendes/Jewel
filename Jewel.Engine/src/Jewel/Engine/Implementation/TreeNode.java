package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class TreeNode
	extends ObjectBase
	implements ITreeNode
{
	private ITreeNode mrefParent;
    private ITreeNode[] marrChildren;

	public static TreeNode GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelEngineException, SQLException
	{
		return (TreeNode)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_TreeNode), pidKey);
	}

    public static TreeNode GetInstance(UUID pidNameSpace, ResultSet prsObject)
    	throws SQLException, JewelEngineException
    {
        return (TreeNode)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_TreeNode), prsObject);
    }

	public void Initialize()
		throws JewelEngineException
	{
        MasterDB ldb;
        ResultSet lrsChildren;
        int[] larrMembers;
        java.lang.Object[] larrParams;
        int[] larrSorts;
        ArrayList<ITreeNode> larrAux;

        larrAux = new ArrayList<ITreeNode>();

        larrMembers = new int[1];
        larrMembers[0] = Miscellaneous.FKParent_In_TreeNode;
        larrParams = new java.lang.Object[1];
        larrParams[0] = getKey();
        larrSorts = new int[1];
        larrSorts[0] = Miscellaneous.NOrd_In_TreeNode;

        try
        {
	        ldb = new MasterDB();
	        lrsChildren = getDefinition().SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
	        while (lrsChildren.next())
	            larrAux.add((ITreeNode)TreeNode.GetInstance(getDefinition().getMemberOf().getKey(), UUID.fromString(lrsChildren.getString(1))));
	        lrsChildren.close();
	        ldb.Disconnect();

	        marrChildren = larrAux.toArray(new ITreeNode[larrAux.size()]);

	        if (getAt(2) != null)
	            mrefParent = TreeNode.GetInstance(Entity.GetInstance(getDefinition().getKey()).getMemberOf().getKey(), (UUID)getAt(2));
        }
        catch(JewelEngineException e)
        {
        	throw e;
        }
        catch(Exception e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
        }
	}

	public String getName()
	{
		return (String)getAt(0);
	}

	public UUID getType()
	{
		return (UUID)getAt(1);
	}

	public UUID getFormID()
	{
		return (UUID)getAt(4);
    }

    public UUID getReportID()
    {
        return (UUID)getAt(7);
    }

	public String getAssembly()
		throws JewelEngineException
	{
        return getNodeNameSpace().getAssembly();
	}

	public String getClassName()
		throws JewelEngineException
	{
        return getNodeNameSpace().getStaticClass();
	}

	public String getMethod()
	{
		return (String)getAt(5);
	}

	public INameSpace getNodeNameSpace()
		throws JewelEngineException
	{
        if (getAt(6) == null)
        {
            if (mrefParent == null)
                return null;
            return mrefParent.getNodeNameSpace();
        }

        return (INameSpace)NameSpace.GetInstance((UUID)getAt(6));
	}

    public ITreeNode[] getChildren()
    {
        return marrChildren;
    }
}
