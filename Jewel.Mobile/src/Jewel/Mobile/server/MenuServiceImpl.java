package Jewel.Mobile.server;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

public class MenuServiceImpl
	extends EngineImplementor
	implements MenuService
{
	private static final long serialVersionUID = 1L;

	public MenuNodeObj[] GetNodes()
		throws JewelMobileException
	{
		UUID lidUser;
		UUID lidNSpace;
        IUser lrefUser;
        IProfile lrefProfile;
        ArrayList<MenuNodeObj> larrBuffer;
        int i;

		lidUser = Engine.getCurrentUser();
		if ( lidUser == null )
			return null;

		lidNSpace = Engine.getCurrentNameSpace();
		if ( lidNSpace == null )
			throw new JewelMobileException("Unexpected: Unknown NameSpace.");

		try
		{
			lrefUser = User.GetInstance(lidNSpace, lidUser);
		}
		catch (Throwable e)
		{
			throw new JewelMobileException(e.getMessage(), e);
		}

		lrefProfile = lrefUser.getProfile();

		larrBuffer = new ArrayList<MenuNodeObj>();

        for (i = 0; i < lrefProfile.getWorkspaces().length; i++)
            LoadTree(lrefProfile.getWorkspaces()[i].getWorkNameSpace().getKey(), larrBuffer);

        for (i = 0; i < lrefProfile.getPermissions().length; i++)
            LoadTree(lidNSpace, lrefProfile.getPermissions()[i].getTreeNode().getKey(), larrBuffer);

		return larrBuffer.toArray(new MenuNodeObj[larrBuffer.size()]);
	}

	public MenuResponse ClickNode(String pstrID, String pstrNSpace)
		throws JewelMobileException
	{
        TreeNode lobjNode;
        MenuResponse lobjResp;
        String lstrError;

		if ( Engine.getCurrentUser() == null )
			return null;

        try
        {
			lobjNode = TreeNode.GetInstance(UUID.fromString(pstrNSpace), UUID.fromString(pstrID));

	        if (NodeTypeGUIDs.NT_Standard.equals(lobjNode.getType()))
	        {
	        	lobjResp = new MenuResponse();
	        	lobjResp.mlngType = MenuResponse.SEARCH;
	        	lobjResp.mstrTitle = "Search: " + lobjNode.getName();
	        	lobjResp.mstrID = lobjNode.getFormID().toString();
	        	lobjResp.mstrNSpace = lobjNode.getNodeNameSpace().getKey().toString();
	        	return lobjResp;
	        }

	        if (NodeTypeGUIDs.NT_Form.equals(lobjNode.getType()))
	        {
	        	lobjResp = new MenuResponse();
	        	lobjResp.mlngType = MenuResponse.FORM;
	        	lobjResp.mstrTitle = "Form: " + lobjNode.getName();
	        	lobjResp.mstrID = lobjNode.getFormID().toString();
	        	lobjResp.mstrNSpace = lobjNode.getNodeNameSpace().getKey().toString();
	        	return lobjResp;
	        }

	        if (NodeTypeGUIDs.NT_Report.equals(lobjNode.getType()))
	        {
	        	lobjResp = new MenuResponse();
	        	lobjResp.mlngType = MenuResponse.REPORT;
	        	lobjResp.mstrTitle = "Report: " + lobjNode.getName();
	        	lobjResp.mstrID = lobjNode.getReportID().toString();
	        	lobjResp.mstrNSpace = lobjNode.getNodeNameSpace().getKey().toString();
	        	return lobjResp;
	        }

	        if (NodeTypeGUIDs.NT_Action.equals(lobjNode.getType()))
	        {
	            lstrError = CodeExecuter.ExecuteStatic(lobjNode.getAssembly(), lobjNode.getClassName(), lobjNode.getMethod(), lobjNode.getNodeNameSpace().getKey());
	            if ( !lstrError.equals("") )
	            	throw new JewelMobileException(lstrError);
	        	lobjResp = new MenuResponse();
	        	lobjResp.mlngType = MenuResponse.NONE;
	            return lobjResp;
	        }

	        if (NodeTypeGUIDs.NT_Folder.equals(lobjNode.getType()))
	        	throw new JewelMobileException("Unexpected: Folder click in server side.");
		}
        catch (JewelMobileException e) 
        {
        	throw e;
		}
        catch (Throwable e)
        {
        	throw new JewelMobileException(e.getMessage(), e);
		}

    	lobjResp = new MenuResponse();
    	lobjResp.mlngType = MenuResponse.NONE;
        return lobjResp;
	}

	public MenuResponse ClickSysNode(String pstrNode)
	{
		MenuResponse lobjResp;

		if ( Engine.getCurrentUser() == null )
			return null;

		if ( pstrNode.equals("Sys01") )
		{
			getSession().invalidate();
	        return null;
		}

		if ( pstrNode.equals("Sys02") )
		{
        	lobjResp = new MenuResponse();
        	lobjResp.mlngType = MenuResponse.FORM;
        	lobjResp.mstrTitle = "System: Change Password";
        	lobjResp.mstrID = "158B422E-B3A4-44E6-BF15-7F208BAD352C";
        	lobjResp.mstrNSpace = NameSpaceGUIDs.N_MADDS.toString();
        	return lobjResp;
		}

    	lobjResp = new MenuResponse();
    	lobjResp.mlngType = MenuResponse.NONE;
        return lobjResp;
	}

    private void LoadTree(UUID pidNSpace, ArrayList<MenuNodeObj> parrBuffer)
    	throws JewelMobileException
    {
        try
        {
			LoadNode(parrBuffer, Entity.GetInstance(Engine.FindEntity(pidNSpace, ObjectGUIDs.O_TreeNode)));
		}
        catch (JewelMobileException e)
        {
        	throw e;
		}
        catch (Throwable e)
        {
        	throw new JewelMobileException(e.getMessage(), e);
		}
    }

    private void LoadTree(UUID pidNSpace, UUID pidTreeNode, ArrayList<MenuNodeObj> parrBuffer)
    	throws JewelMobileException
    {
    	MenuNodeObj ltrn;
        ITreeNode lrefTNode;
        ArrayList<MenuNodeObj> larrChildren;

        try
        {
			lrefTNode = TreeNode.GetInstance(pidNSpace, pidTreeNode);
		}
        catch (Throwable e)
        {
        	throw new JewelMobileException(e.getMessage(), e);
		}

        larrChildren = new ArrayList<MenuNodeObj>();
        LoadNode(larrChildren, lrefTNode.getChildren(), pidNSpace);

        ltrn = BuildNode(lrefTNode.getName(), lrefTNode.getKey(), pidNSpace, true, (NodeTypeGUIDs.NT_Action.equals(lrefTNode.getType())), larrChildren);
        parrBuffer.add(ltrn);

    }

    private void LoadNode(ArrayList<MenuNodeObj> pcolNodes, IEntity prefEntity)
    	throws JewelMobileException
    {
        MasterDB ldb;
        ResultSet lrs;
        int[] larrMembers;
        java.lang.Object[] larrParams;
        int[] larrSorts;
        MenuNodeObj ltrn;
        ITreeNode lrefTNode;
        ArrayList<MenuNodeObj> larrChildren;

        larrMembers = new int[1];
        larrMembers[0] = Miscellaneous.FKParent_In_TreeNode;
        larrParams = new java.lang.Object[1];
        larrParams[0] = null;
        larrSorts = new int[1];
        larrSorts[0] = Miscellaneous.NOrd_In_TreeNode;

        try
        {
			ldb = new MasterDB();
	        lrs = prefEntity.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
	        while (lrs.next())
	        {
	            lrefTNode = TreeNode.GetInstance(prefEntity.getMemberOf().getKey(), lrs);
	            larrChildren = new ArrayList<MenuNodeObj>();
	            LoadNode(larrChildren, lrefTNode.getChildren(), prefEntity.getMemberOf().getKey());
	            ltrn = BuildNode(lrefTNode.getName(), lrefTNode.getKey(), prefEntity.getMemberOf().getKey(), true,
	            		(NodeTypeGUIDs.NT_Action.equals(lrefTNode.getType())), larrChildren);
	            pcolNodes.add(ltrn);
	        }
	        lrs.close();
	        ldb.Disconnect();
		}
        catch (Throwable e)
        {
			throw new JewelMobileException(e.getMessage(), e);
		}
    }

    private void LoadNode(ArrayList<MenuNodeObj> pcolNodes, ITreeNode[] parrNodes, UUID pidSourceSpace)
    {
    	MenuNodeObj ltrn;
        ArrayList<MenuNodeObj> larrChildren;
        int i;

        for (i = 0; i < parrNodes.length; i++)
        {
            larrChildren = new ArrayList<MenuNodeObj>();
            LoadNode(larrChildren, parrNodes[i].getChildren(), pidSourceSpace);
            ltrn = BuildNode(parrNodes[i].getName(), parrNodes[i].getKey(), pidSourceSpace, false,
            		(NodeTypeGUIDs.NT_Action.equals(parrNodes[i].getType())), larrChildren);
            pcolNodes.add(ltrn);
        }
    }

    private MenuNodeObj BuildNode(String pstrName, UUID pidKey, UUID pidSourceSpace, boolean pbExpand, boolean pbAddJScript, ArrayList<MenuNodeObj> parrChildren)
    {
    	MenuNodeObj ltrn;

        ltrn = new MenuNodeObj();
        ltrn.mstrText = pstrName;
        ltrn.mstrID = pidKey.toString();
        ltrn.mstrNSpace = pidSourceSpace.toString();
        ltrn.mbExpanded = pbExpand;
        ltrn.mbConfirm = pbAddJScript;
        ltrn.marrChildren = parrChildren.toArray(new MenuNodeObj[parrChildren.size()]);

        return ltrn;
    }
}
