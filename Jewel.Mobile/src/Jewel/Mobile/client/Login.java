package Jewel.Mobile.client;

import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class Login
	extends Composite
{
	private LoginServiceAsync loginSvc;

	private Label mlblError;
	private TextBox mtxtLogin;
	private PasswordTextBox mtxtPassword;
	private ListBox mllbxDomains;
	private Button mbtnLogin;

	public Login()
	{
		VerticalPanel lvert;
		Grid lgrid;
		Label llbl;

		lvert = new VerticalPanel();
		lvert.setStylePrimaryName("loginWrapper");

		mlblError = new Label(" ");
		mlblError.setStylePrimaryName("loginError");
		lvert.add(mlblError);

		lgrid = new Grid();
		lgrid.setStylePrimaryName("loginForm");
		lgrid.resize(3, 2);
		lgrid.setCellSpacing(5);
		lgrid.setCellPadding(0);
		lvert.add(lgrid);

		llbl = new Label("Username");
		llbl.setStylePrimaryName("loginLabel");
		lgrid.setWidget(0, 0, llbl);
		mtxtLogin = new TextBox();
		mtxtLogin.setStylePrimaryName("loginInput");
		lgrid.setWidget(0, 1, mtxtLogin);

		llbl = new Label("Password");
		llbl.setStylePrimaryName("loginLabel");
		lgrid.setWidget(1, 0, llbl);
		mtxtPassword = new PasswordTextBox();
		mtxtPassword.setStylePrimaryName("loginInput");
		lgrid.setWidget(1, 1, mtxtPassword);

		llbl = new Label("Domain");
		llbl.setStylePrimaryName("loginLabel");
		lgrid.setWidget(2, 0, llbl);
		mllbxDomains = new ListBox();
		mllbxDomains.setStylePrimaryName("loginDomains");
		lgrid.setWidget(2, 1, mllbxDomains);

		mbtnLogin=new Button("Login");
		mbtnLogin.setEnabled(false);
		lvert.add(mbtnLogin);
		mbtnLogin.getElement().getParentElement().setClassName("loginButton");

		initWidget(lvert);

		mbtnLogin.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				checkLogin(mtxtLogin.getText(), mtxtPassword.getText(), mllbxDomains.getValue(mllbxDomains.getSelectedIndex()));
				mtxtPassword.setText("");
	        }
	     });

		Menu.DropRoot();
		getDomains();
	}

	private LoginServiceAsync getService()
	{
		if ( loginSvc == null )
			loginSvc = GWT.create(LoginService.class);

		return loginSvc;
	}

	private void getDomains()
	{
		AsyncCallback<LoginDomain[]> callback = new AsyncCallback<LoginDomain[]>()
        {
			public void onSuccess(LoginDomain[] result)
			{
				int i;

				if (result != null)
				{
					setErrorText(" ");
					mllbxDomains.clear();
					for ( i = 0; i < result.length; i++ )
						mllbxDomains.addItem(result[i].mstrName, result[i].mstrKey);
					mbtnLogin.setEnabled(true);
				}
				else
				{
					setErrorText("Unexpected: Domain list is null.");
				}
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				setErrorText("Error: " + ex.getMessage());
			}
        };

        getService().GetDomains(callback);
	}

	private void checkLogin(String pstrUser, String pstrPasswd, String pstrDomain)
	{
		LoginResponse resp;
		
		AsyncCallback<String> callback = new AsyncCallback<String>()
        {
			public void onSuccess(String result)
			{
				if ( result == null )
					setErrorText("Unexpected: User name is null.");
				else
					Jewel_Mobile.getReference().setMenuScreen(Menu.GetRoot(result));
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				setErrorText("Error: " + ex.getMessage());
			}
        };

        resp = new LoginResponse();
        resp.mstrUser = pstrUser;
        resp.mstrPasswd = pstrPasswd;
        resp.mstrDomain = pstrDomain;
        getService().CheckLogin(resp, callback);
	}

	private void setErrorText(String errorMessage)
	{
		mlblError.setText(errorMessage);
	}
}
