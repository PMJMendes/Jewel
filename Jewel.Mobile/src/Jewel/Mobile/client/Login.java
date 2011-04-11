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

	private LoginDomain[] larrDomains;
	private boolean mbShowBox;

	private VerticalPanel mpnMain;
	private Label mlblError;
	private TextBox mtxtLogin;
	private PasswordTextBox mtxtPassword;
	private ListBox mllbxDomains;
	private Button mbtnLogin;

	public Login(boolean pbShowBox)
	{
		Grid lgrid;
		Label llbl;

		mbShowBox = pbShowBox;

		mpnMain = new VerticalPanel();
		mpnMain.setStylePrimaryName("login");
		mpnMain.setVisible(false);

		mlblError = new Label(" ");
		mlblError.setStylePrimaryName("login-Error");
		mpnMain.add(mlblError);
		mlblError.getElement().getParentElement().setClassName("login-Error-Wrapper");

		lgrid = new Grid();
		lgrid.setStylePrimaryName("login-Form");
		lgrid.resize((pbShowBox ? 3 : 2), 2);
		lgrid.setCellSpacing(5);
		lgrid.setCellPadding(0);
		mpnMain.add(lgrid);
		lgrid.getElement().getParentElement().setClassName("login-Form-Wrapper");

		llbl = new Label("Username");
		llbl.setStylePrimaryName("login-Label");
		lgrid.setWidget(0, 0, llbl);
		llbl.getElement().getParentElement().setClassName("login-Label-Wrapper");
		mtxtLogin = new TextBox();
		mtxtLogin.setStylePrimaryName("login-Field");
		lgrid.setWidget(0, 1, mtxtLogin);
		mtxtLogin.getElement().getParentElement().setClassName("login-Field-Wrapper");

		llbl = new Label("Password");
		llbl.setStylePrimaryName("login-Label");
		lgrid.setWidget(1, 0, llbl);
		llbl.getElement().getParentElement().setClassName("login-Label-Wrapper");
		mtxtPassword = new PasswordTextBox();
		mtxtPassword.setStylePrimaryName("login-Field");
		lgrid.setWidget(1, 1, mtxtPassword);
		mtxtPassword.getElement().getParentElement().setClassName("login-Field-Wrapper");

		if ( pbShowBox )
		{
			llbl = new Label("Domain");
			llbl.setStylePrimaryName("login-Label");
			lgrid.setWidget(2, 0, llbl);
			llbl.getElement().getParentElement().setClassName("login-Label-Wrapper");
			mllbxDomains = new ListBox();
			mllbxDomains.setStylePrimaryName("login-Field");
			lgrid.setWidget(2, 1, mllbxDomains);
			mllbxDomains.getElement().getParentElement().setClassName("login-Field-Wrapper");
		}

		mbtnLogin=new Button("Login");
		mbtnLogin.setEnabled(false);
		mbtnLogin.setStylePrimaryName("login-Button");
		mpnMain.add(mbtnLogin);
		mbtnLogin.getElement().getParentElement().setClassName("login-Button-Wrapper");

		initWidget(mpnMain);

		mbtnLogin.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				checkLogin(mtxtLogin.getText(), mtxtPassword.getText(),
						(mbShowBox ? mllbxDomains.getValue(mllbxDomains.getSelectedIndex()) : larrDomains[0].mstrKey));
				mtxtPassword.setText("");
	        }
	     });

		Menu.DropRoot();
		checkSession();
	}

	private LoginServiceAsync getService()
	{
		if ( loginSvc == null )
			loginSvc = GWT.create(LoginService.class);

		return loginSvc;
	}

	private void checkSession()
	{
		AsyncCallback<String> callback = new AsyncCallback<String>()
        {
			public void onSuccess(String result)
			{
				if ( result == null )
					getDomains();
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

		getService().CheckLogin(callback);
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
					larrDomains = result;
					if ( mbShowBox )
					{
						setErrorText(" ");
						mllbxDomains.clear();
						for ( i = 0; i < result.length; i++ )
							mllbxDomains.addItem(result[i].mstrName, result[i].mstrKey);
					}
					mbtnLogin.setEnabled(true);
					mpnMain.setVisible(true);
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
        resp.mstrDomain = /*pstrDomain*/"6B6FB3AF-EBC8-4D62-862C-891B6E59B951"; //TODO: !!
        getService().CheckLogin(resp, callback);
	}

	private void setErrorText(String errorMessage)
	{
		mlblError.setText(errorMessage);
	}
}
