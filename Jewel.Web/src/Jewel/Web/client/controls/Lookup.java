package Jewel.Web.client.controls;

import Jewel.Web.client.*;
import Jewel.Web.client.events.*;
import Jewel.Web.client.popups.*;
import Jewel.Web.shared.*;

import com.google.gwt.dom.client.Style.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.ui.*;

public class Lookup
	extends Composite
	implements IJewelWebCtl, JChangeEvent.HasEvent
{
	private String mstrValue;
	private String mstrFormID;
	private String mstrNameSpace;
	private String mstrParentFormID;
	private String mstrEntity;

	private TextBox mtxtDisplay;
	private LookupPopup mdlgPopup;

	private HandlerManager mrefEventMgr;

	public Lookup(String pstrFormID, String pstrNameSpace, String pstrParentFormID)
	{
		HorizontalPanel louter;
		Image limg;

		mdlgPopup = null;

		mstrFormID = pstrFormID;
		mstrNameSpace = pstrNameSpace;
		mstrParentFormID = pstrParentFormID;
		mstrEntity = null;

		louter = new HorizontalPanel();
		louter.setStylePrimaryName("jewel-Control jewel-Lookup");

		mtxtDisplay = new TextBox();
		mtxtDisplay.setReadOnly(true);
		mtxtDisplay.setStylePrimaryName("jewel-Lookup-Display");
		louter.add(mtxtDisplay);

		limg = new Image();
		limg.setUrl("images/iconlookup.bmp");
		limg.setStylePrimaryName("jewel-Lookup-Button");
		louter.add(limg);

		initWidget(louter);

		limg.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				if ( ((mstrFormID != null) && (mstrNameSpace != null)) || (mstrEntity != null) )
					DoPopup();
	        }
	     });

		mrefEventMgr = new HandlerManager(this);
	}
	
	public void Retarget(String pstrEntity)
	{
		mstrEntity = pstrEntity;
		if (mstrValue != null) 
			mtxtDisplay.setText("{Value}");
	}

	public void SetDisplay(String pstrDisplay)
	{
		mtxtDisplay.setText(pstrDisplay);
	}

	public String getJValue()
	{
		if (mstrValue == null)
			return null;

		return mstrValue + "!" + mtxtDisplay.getText();
	}

	public void setJValue(String pstrValue)
	{
		String[] larrAux;

		if ( pstrValue == null )
		{
			mstrValue = null;
			mtxtDisplay.setText(null);
			return;
		}

		larrAux = pstrValue.split("!", 2);
		mstrValue = larrAux[0];
		mtxtDisplay.setText(larrAux[1]);
	}

	public void setJValue(String pstrValue, boolean pbFireEvents)
	{
		setJValue(pstrValue);

		if ( pbFireEvents )
			mrefEventMgr.fireEvent(new JChangeEvent());
	}

	public void setWidth(int plngWidth)
	{
		getElement().getStyle().setWidth(plngWidth-15, Unit.PX);
		mtxtDisplay.getElement().getStyle().setWidth(plngWidth-35, Unit.PX);
	}

	private void DoPopup()
	{
		if ( mdlgPopup == null )
			mdlgPopup = new LookupPopup(this);

		if ( mstrEntity != null )
			mdlgPopup.InitPopup(mstrEntity, mstrValue, mstrParentFormID, GetExtParams());
		else
			mdlgPopup.InitPopup(mstrFormID, mstrNameSpace, mstrValue, mstrParentFormID, GetExtParams());

		mdlgPopup.show();
	}

	private ParamInfo[] GetExtParams()
	{
		Widget lrefParent;

		if ( mstrParentFormID == null )
			return null;

		lrefParent = getParent();
		while ( lrefParent != null )
		{
			if ( lrefParent instanceof DynaForm )
				return ((DynaForm)lrefParent).GetExternalParams();

			lrefParent = lrefParent.getParent();
		}

		return null;
	}

	public HandlerRegistration addJChangeHandler(JChangeEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(JChangeEvent.TYPE, handler);
	}
}
