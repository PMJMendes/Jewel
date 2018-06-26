package Jewel.Web.client.controls;

import Jewel.Web.client.IJewelWebCtl;
import Jewel.Web.client.Jewel_Web;
import Jewel.Web.client.events.JErrorEvent;
import Jewel.Web.client.events.JErrorEvent.Handler;
import Jewel.Web.interfaces.TypifiedListService;
import Jewel.Web.interfaces.TypifiedListServiceAsync;
import Jewel.Web.shared.TypifiedListItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;

public class DropdownList
	extends ListBox
	implements IJewelWebCtl, JErrorEvent.HasEvent
{
	private TypifiedListServiceAsync listSvc;

	private String mstrNSpace;
	private String mstrObjID;
	private String mstrValue;
	private String mstrText;
	private boolean mbHasValues;

	private HandlerManager mrefEventMgr;

	@SuppressWarnings("deprecation")
	public DropdownList(String pstrNSpace, String pstrObjID)
	{
		super(false);

		clear();
		addItem("-", (String)null);

		setStylePrimaryName("jewel-Control jewel-ListDropDown");

		mrefEventMgr = new HandlerManager(this);

		mbHasValues = false;
		mstrNSpace = pstrNSpace;
		mstrObjID = pstrObjID;
		initValues();
	}

	private TypifiedListServiceAsync getService()
	{
		if ( listSvc == null )
			listSvc = GWT.create(TypifiedListService.class);

		return listSvc;
	}

	public String getJValue()
	{
		if ( mstrValue == null )
			return null;

		return mstrValue + "!" + mstrText;
	}

	public void setJValue(String pstrValue)
	{
		String[] larrAux;
		int i;

		if ( pstrValue == null )
		{
			mstrValue = null;
			mstrText = null;
			if ( !mbHasValues )
			{
				clear();
				addItem("-", (String)null);
			}
			setSelectedIndex(0);
			return;
		}

		larrAux = pstrValue.split("!", 2);
		mstrValue = larrAux[0];
		mstrText = larrAux[1];

		if ( !mbHasValues )
		{
			clear();
			addItem("-", (String)null);
			addItem(mstrText + " (?)", mstrValue);
			setSelectedIndex(1);
			return;
		}

		for ( i = 1; i < getItemCount(); i++ )
		{
			if ( mstrValue.equalsIgnoreCase(getValue(i)) )
			{
				setSelectedIndex(i);
				mstrText = getItemText(i);
				return;
			}
		}

		mstrValue = null;
		mstrText = null;
	}

	public void setWidth(int plngWidth)
	{
		getElement().getStyle().setWidth(plngWidth - 15, Unit.PX);
	}

	private void initValues()
	{
        AsyncCallback<TypifiedListItem[]> callback = new AsyncCallback<TypifiedListItem[]>()
        {
			public void onSuccess(TypifiedListItem[] result)
			{
				if (result != null)
				{
					fillValues(result);
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
		getService().getListItems(mstrNSpace, mstrObjID, callback);
	}

	private void fillValues(TypifiedListItem[] parrValues)
	{
		int i;

		clear();

		addItem("-", (String)null);

		for ( i = 0; i < parrValues.length; i++ )
			addItem(parrValues[i].value, parrValues[i].id);

		mbHasValues = true;

		setJValue(getJValue());

		addChangeHandler(new ChangeHandler()
		{
			public void onChange(ChangeEvent event)
			{
				if ( getSelectedIndex() == 0 )
				{
					mstrValue = null;
					mstrText = null;
					return;
				}

				mstrValue = getValue(getSelectedIndex());
				mstrText = getItemText(getSelectedIndex());
			}
		});
	}

	public HandlerRegistration addJErrorHandler(Handler handler)
	{
		return mrefEventMgr.addHandler(JErrorEvent.TYPE, handler);
	}
}
