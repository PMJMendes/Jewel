package Jewel.Mobile.client.controls;

import Jewel.Mobile.client.*;
import Jewel.Mobile.client.events.*;
import Jewel.Mobile.interfaces.*;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Style.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class ValueLookup
	extends Composite
	implements IJewelMobileCtl
{
//	private ValueServiceAsync valueSvc;

	private Lookup mobjClass;
	private Lookup mobjValue;

	public ValueLookup()
	{
		HorizontalPanel louter;

		louter = new HorizontalPanel();
		louter.setStylePrimaryName("formControl valueLookup");

		mobjClass = new Lookup(null, null, null);
		mobjClass.Retarget("94AB0A74-25A1-11DA-91C2-000B6ABC6AE9");
		louter.add(mobjClass);

		mobjValue = new Lookup(null, null, null);
		louter.add(mobjValue);

		initWidget(louter);

		mobjClass.addJChangeHandler(new JChangeEvent.Handler()
		{
			public void onJChange(JChangeEvent event)
			{
				String lstrAux, lstrAux2;
				
				lstrAux = mobjClass.getJValue();
				if ( lstrAux == null )
					mobjValue.Retarget(null);
				else
				{
					lstrAux = lstrAux.split("!", 2)[0];
					mobjValue.Retarget(lstrAux);
					lstrAux2 = getJValue();
					if ( lstrAux2 != null )
						CheckDisplay(lstrAux, lstrAux2);
				}
			}
	     });
	}

//	private ValueServiceAsync getService()
//	{
//		if ( valueSvc == null )
//			valueSvc = GWT.create(ValueService.class);

//		return valueSvc;
//	}

	public String getJValue()
	{
		String lstrAux;
		
		lstrAux = mobjValue.getJValue();
		if ( lstrAux == null )
			return null;

		return lstrAux.split("!", 2)[0];
	}

	public void setJValue(String pstrValue)
	{
		String lstrEntity;

		if ( pstrValue == null )
		{
			mobjValue.setJValue(null);
			return;
		}

		mobjValue.setJValue(pstrValue + "!{Value}");

		lstrEntity = mobjClass.getJValue();
		if ( lstrEntity == null )
			return;
		CheckDisplay(lstrEntity.split("!", 2)[0], pstrValue);
	}

	private void CheckDisplay(String pstrEntity, String pstrValue)
	{
		AsyncCallback<String> callback = new AsyncCallback<String>()
        {
			public void onSuccess(String result)
			{
				if (result != null)
				{
					if ( result.equals("") )
						mobjValue.SetDisplay("{Mismatch}");
					else
						mobjValue.SetDisplay(result);
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

//        getService().GetDisplayText(pstrEntity, pstrValue, callback);
	}
}
