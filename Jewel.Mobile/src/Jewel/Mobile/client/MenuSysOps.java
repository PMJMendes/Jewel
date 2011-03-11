package Jewel.Mobile.client;

import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class MenuSysOps
	extends Menu
{
	private MenuServiceAsync treeSvc;

	public MenuSysOps()
	{
		super(null);

		VerticalPanel lvert;
		Button lbtnLogout;
		Button lbtnChangePass;
		Button lbtnBack;

		lvert = new VerticalPanel();
		lvert.setStylePrimaryName("menuWrapper");
		lvert.setSpacing(5);

		lbtnLogout = new Button();
		lbtnLogout.setText("Logout");
		lbtnLogout.setStylePrimaryName("menuItem");
		lvert.add(lbtnLogout);

		lbtnChangePass = new Button();
		lbtnChangePass.setText("Change Password");
		lbtnChangePass.setStylePrimaryName("menuItem");
		lvert.add(lbtnChangePass);

		lbtnBack = new Button();
		lbtnBack.setText("Back");
		lbtnBack.setStylePrimaryName("menuItem");
		lvert.add(lbtnBack);

		initWidget(lvert);

		lbtnLogout.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				DoClick("Sys01");
	        }
	     });

		lbtnChangePass.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				DoClick("Sys02");
	        }
	     });

		lbtnBack.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				Jewel_Mobile.getReference().setMenuScreen(Menu.GetRoot(null));
	        }
	     });
	}

	private MenuServiceAsync getService()
	{
		if ( treeSvc == null )
			treeSvc = GWT.create(MenuService.class);
		
		return treeSvc;
	}

	private void DoClick(String pstrId)
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

		getService().ClickSysNode(pstrId, callback);
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
		}
	}
}
