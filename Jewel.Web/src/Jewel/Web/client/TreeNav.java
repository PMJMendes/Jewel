package Jewel.Web.client;

import Jewel.Web.client.events.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class TreeNav
	extends Tree
	implements JErrorEvent.HasEvent
{
	private TreeServiceAsync treeSvc;

	private Main mrefContainer;

	private HandlerManager mrefEventMgr;

	public TreeNav(Main prefCOntainer, String pstrUser)
	{
		TreeItem litem, litem2;

		mrefContainer = prefCOntainer;

		setStylePrimaryName("jewel-Tree");

		litem = new TreeItem();
		litem.setText(pstrUser + ": Options");
		litem.setUserObject("Sys00");
		addItem(litem);

		litem2 = new TreeItem();
		litem2.setText("Logout");
		litem2.setUserObject("Sys01");
		litem.addItem(litem2);

		litem2 = new TreeItem();
		litem2.setText("Change Password");
		litem2.setUserObject("Sys02");
		litem.addItem(litem2);

		litem.setState(false, false);

		addSelectionHandler(new SelectionHandler<TreeItem>()
		{
			public void onSelection(SelectionEvent<TreeItem> event)
			{
				DoSelection(event);
			}
		});

		mrefEventMgr = new HandlerManager(this);

		GetNodes();
	}

	private void DoSelection(SelectionEvent<TreeItem> event)
	{
		TreeItem litem;
		TreeNodeObj lobj;

		litem = event.getSelectedItem();

		if ( litem.getChildCount() > 0 )
		{
			litem.setState(!litem.getState(), false);
		}
		else if ( litem.getUserObject() instanceof String )
		{
			ClickSysNode((String)litem.getUserObject());
		}
		else
		{
			lobj = (TreeNodeObj)litem.getUserObject();
			if ( lobj.mbConfirm && !Window.confirm("Are you sure you want to run this action?") )
				return;
			
			ClickNode(lobj.mstrID, lobj.mstrNSpace);
		}

		setSelectedItem(null, false);
	}

	private TreeServiceAsync getService()
	{
		if ( treeSvc == null )
			treeSvc = GWT.create(TreeService.class);
		
		return treeSvc;
	}

	private void GetNodes()
	{
		AsyncCallback<TreeNodeObj[]> callback = new AsyncCallback<TreeNodeObj[]>()
        {
			public void onSuccess(TreeNodeObj[] result)
			{
				int i;

				if (result != null)
				{
					for ( i = 0; i < result.length; i++ )
						addItem(BuildItem(result[i]));
				}
				else
				{
					Jewel_Web.getReference().setLoginScreen();
				}
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				mrefEventMgr.fireEvent(new JErrorEvent(ex.getMessage()));
			}
        };

		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().GetNodes(callback);
	}

	private void ClickNode(String pstrID, String pstrNSpace)
	{
		AsyncCallback<TreeResponse> callback = new AsyncCallback<TreeResponse>()
        {
			public void onSuccess(TreeResponse result)
			{
				if (result != null)
				{
					if ( result.mlngType != TreeResponse.NONE)
						mrefContainer.AddPanel(result);
				}
				else
				{
					Jewel_Web.getReference().setLoginScreen();
				}
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				mrefEventMgr.fireEvent(new JErrorEvent(ex.getMessage()));
			}
        };

		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().ClickNode(pstrID, pstrNSpace, callback);
	}

	private void ClickSysNode(String pstrNode)
	{
		AsyncCallback<TreeResponse> callback = new AsyncCallback<TreeResponse>()
        {
			public void onSuccess(TreeResponse result)
			{
				if (result != null)
				{
					if ( result.mlngType != TreeResponse.NONE)
						mrefContainer.AddPanel(result);
				}
				else
				{
					Jewel_Web.getReference().setLoginScreen();
				}
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				mrefEventMgr.fireEvent(new JErrorEvent(ex.getMessage()));
			}
        };

		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().ClickSysNode(pstrNode, callback);
	}

	private TreeItem BuildItem(TreeNodeObj pobjNode)
	{
		TreeItem litem;
		int i;

		litem = new TreeItem();
		litem.setText(pobjNode.mstrText);

		for ( i = 0; i < pobjNode.marrChildren.length; i++ )
			litem.addItem(BuildItem(pobjNode.marrChildren[i]));
		pobjNode.marrChildren = null;

		litem.setState(pobjNode.mbExpanded);
		litem.setUserObject(pobjNode);

		return litem;
	}

	public HandlerRegistration addJErrorHandler(JErrorEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(JErrorEvent.TYPE, handler);
	}
}
