package Jewel.Web.client;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

public class Jewel_Web
	implements EntryPoint
{
	private static Jewel_Web gstrReference = null;

	public static Jewel_Web getReference()
	{
		return gstrReference;
	}

	public void onModuleLoad()
	{
		gstrReference = this;
		setLoginScreen();
	}

	public void setLoginScreen()
	{
		Login scrLogin;

		RootPanel.get().clear();

		scrLogin = new Login();
		RootPanel.get().add(scrLogin);
		scrLogin.DoResize();
	}

	public void setHomeScreen(String pstrUser)
	{
		Main homeScreen;

		RootPanel.get().clear();

		homeScreen = new Main(pstrUser);
		RootPanel.get().add(homeScreen);
		homeScreen.DoResize();
	}
}
