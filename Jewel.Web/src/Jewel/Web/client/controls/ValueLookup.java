package Jewel.Web.client.controls;

import Jewel.Web.client.*;
import Jewel.Web.client.events.*;
import Jewel.Web.interfaces.*;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Style.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class ValueLookup
	extends Composite
	implements IJewelWebCtl, JErrorEvent.HasEvent
{
	private ValueServiceAsync valueSvc;

	private Lookup mobjClass;
	private Lookup mobjValue;

	private HandlerManager mrefEventMgr;

	public ValueLookup()
	{
		HorizontalPanel louter;

		louter = new HorizontalPanel();
		louter.setStylePrimaryName("jewel-Control jewel-ValueLookup");

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

		mrefEventMgr = new HandlerManager(this);
	}

	private ValueServiceAsync getService()
	{
		if ( valueSvc == null )
			valueSvc = GWT.create(ValueService.class);

		return valueSvc;
	}

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

	public void setWidth(int plngWidth)
	{
		getElement().getStyle().setWidth(plngWidth - 15, Unit.PX);
		mobjClass.setWidth(plngWidth / 2 + 5);
		mobjValue.setWidth(plngWidth - (plngWidth / 2) + 5);
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
        getService().GetDisplayText(pstrEntity, pstrValue, callback);
	}

	public HandlerRegistration addJErrorHandler(JErrorEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(JErrorEvent.TYPE, handler);
	}
}
