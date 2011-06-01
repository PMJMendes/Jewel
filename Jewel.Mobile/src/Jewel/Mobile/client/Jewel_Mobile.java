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
		RootPanel.get().clear();
		RootPanel.get().add(new Login(false));
	}

	public void setLoginScreen()
	{
		if ( mrefCurrent != null )
			mrefCurrent.DoClose();

		RootPanel.get().clear();
		RootPanel.get().add(new Login(/*true*/false)); //TODO: !!
	}

	public void setMenuScreen(Menu pmnuCurrent)
	{
		if ( mrefCurrent != null )
			mrefCurrent.DoClose();
		mrefCurrent = null;

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

	public void setComplexScreen(ComplexScreen pscrnCurrent)
	{
		if ( mrefCurrent != null )
			mrefCurrent.DoClose();
		mrefCurrent = pscrnCurrent;

		RootPanel.get().clear();
		RootPanel.get().add(pscrnCurrent);
	}

	public void setSimpleReportScreen(SimpleReport pscrnCurrent)
	{
		if ( mrefCurrent != null )
			mrefCurrent.DoClose();
		mrefCurrent = pscrnCurrent;

		RootPanel.get().clear();
		RootPanel.get().add(pscrnCurrent);
	}

	public void setEmptyScreen(ClosableEmpty pscrnCurrent)
	{
		if ( mrefCurrent != null )
			mrefCurrent.DoClose();
		mrefCurrent = pscrnCurrent;

		RootPanel.get().clear();
		RootPanel.get().add(pscrnCurrent);
	}

	public void showError(String pstrError)
	{
		new MessageBox(pstrError).show();
	}
}
