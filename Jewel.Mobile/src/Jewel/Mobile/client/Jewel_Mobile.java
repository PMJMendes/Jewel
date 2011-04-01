package Jewel.Mobile.client;

import Jewel.Mobile.client.popups.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.ui.*;

public class Jewel_Mobile
	implements EntryPoint
{
	private static Jewel_Mobile gstrReference = null;

	public static Jewel_Mobile getReference()
	{
		return gstrReference;
	}

	private ClosableContent mrefCurrent;

	public void onModuleLoad()
	{
		mrefCurrent = null;
		gstrReference = this;
		setLoginScreen();
	}

	public void setLoginScreen()
	{
		if ( mrefCurrent != null )
			mrefCurrent.DoClose();

		RootPanel.get().clear();
		RootPanel.get().add(new Login());
	}

	public void setMenuScreen(Menu pmnuCurrent)
	{
		if ( mrefCurrent != null )
			mrefCurrent.DoClose();

		RootPanel.get().clear();
		RootPanel.get().add(pmnuCurrent);
	}

	public void setSingleFormScreen(SingleForm pfrmCurrent)
	{
		if ( mrefCurrent != null )
			mrefCurrent.DoClose();
		mrefCurrent = pfrmCurrent;

		RootPanel.get().clear();
		RootPanel.get().add(pfrmCurrent);
	}

	public void showError(String pstrError)
	{
		new MessageBox(pstrError).center();
	}
}