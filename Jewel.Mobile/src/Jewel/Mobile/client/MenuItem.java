package Jewel.Mobile.client;

import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

public class MenuItem
	extends Button
{
	private MenuServiceAsync treeSvc;

	private MenuNodeObj mobjDef;
	private Menu mmnuOwner;
	private Menu mmnuSub;

	public MenuItem(MenuNodeObj pobjDef, Menu pmnuOwner)
	{
		mobjDef = pobjDef;
		mmnuOwner = pmnuOwner;

		setText(mobjDef.mstrText);
		setStylePrimaryName("menuItem");
		if ( (mobjDef.marrChildren == null) || (mobjDef.marrChildren.length == 0) )
			mmnuSub = null;
		else
			mmnuSub = new Menu(mobjDef.marrChildren, this);

		addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				DoClick();
	        }
	     });
	}

	private MenuServiceAsync getService()
	{
		if ( treeSvc == null )
			treeSvc = GWT.create(MenuService.class);
		
		return treeSvc;
	}

	private void DoClick()
	{
		AsyncCallback<MenuResponse> callback = new AsyncCallback<MenuResponse>()
        {
			public void onSuccess(MenuResponse result)
			{
				if (result != null)
				{
					ShowResult(result);
				}
				else
				{
					Jewel_Mobile.getReference().setLoginScreen();
				}
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				Jewel_Mobile.getReference().showError(ex.getMessage());
			}
        };

        if ( mmnuSub != null )
        {
        	Jewel_Mobile.getReference().setMenuScreen(mmnuSub);
        	return;
        }

		if ( mobjDef.mbConfirm && !Window.confirm("Are you sure you want to run this action?") )
			return;

        getService().ClickNode(mobjDef.mstrID, mobjDef.mstrNSpace, callback);
	}

	private void ShowResult(MenuResponse pobjResp)
	{
		SingleForm lrefForm;

		switch ( pobjResp.mlngType )
		{
		case MenuResponse.FORM:
			lrefForm = new SingleForm(pobjResp.mstrTitle);
			lrefForm.InitForm(pobjResp.mstrID, pobjResp.mstrNSpace);
        	Jewel_Mobile.getReference().setSingleFormScreen(lrefForm);
			break;
/*
		case TreeResponse.SEARCH:
			lrefSearch = new DynaSearch();
			lrefSearch.addJErrorHandler(new JErrorEvent.Handler()
			{
				public void onJError(JErrorEvent event)
				{
					SetError(event.getError());
				}
			});
			lrefSearch.InitSearch(pobjResp.mstrID, pobjResp.mstrNSpace, null, null, null);
			mtabs.add(lrefSearch, new ClosableTab(pobjResp.mstrTitle, mtabs, lrefSearch));
			break;

		case TreeResponse.REPORT:
			lrefReport = new DynaReport();
			lrefReport.addJErrorHandler(new JErrorEvent.Handler()
			{
				public void onJError(JErrorEvent event)
				{
					SetError(event.getError());
				}
			});
			lrefReport.InitReport(pobjResp.mstrID, pobjResp.mstrNSpace);
			mtabs.add(lrefReport, new ClosableTab(pobjResp.mstrTitle, mtabs, lrefReport));
			break;

		default:
			lrefAux = new ClosableEmpty();
			mtabs.add(lrefAux, new ClosableTab(pobjResp.mstrTitle, mtabs, lrefAux));
*/
		}
	}

	public Menu GetOwner()
	{
		return mmnuOwner;
	}
}