package Jewel.Mobile.client.controls;

import Jewel.Mobile.client.*;
import Jewel.Mobile.client.events.*;
import Jewel.Mobile.client.popups.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.ui.*;

public class Lookup
	extends Composite
	implements IJewelMobileCtl, JChangeEvent.HasEvent
{
	private String mstrValue;
	private String mstrFormID;
	private String mstrNameSpace;
	private String mstrParentFormID;
	private String mstrEntity;

	private TextBox mtxtDisplay;
	private LookupPopup mdlgPopup;

	private HandlerManager mrefEventMgr;

	public Lookup(String pstrFormID, String pstrNameSpace, String pstrParentFormID, boolean pbComponent)
	{
		HorizontalPanel louter;
		Button lbtn;

		mdlgPopup = null;

		mstrFormID = pstrFormID;
		mstrNameSpace = pstrNameSpace;
		mstrParentFormID = pstrParentFormID;
		mstrEntity = null;

		louter = new HorizontalPanel();
		louter.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		if (pbComponent)
			louter.setStylePrimaryName("lookup");
		else
			louter.setStylePrimaryName("formControl lookup");

		mtxtDisplay = new TextBox();
		mtxtDisplay.setReadOnly(true);
		mtxtDisplay.setStylePrimaryName("lookup-Display");
		louter.add(mtxtDisplay);
		mtxtDisplay.getElement().getParentElement().setClassName("lookup-Display-Wrapper");

		lbtn = new Button("?");
		lbtn.setStylePrimaryName("lookup-Button");
		louter.add(lbtn);
		lbtn.getElement().getParentElement().setClassName("lookup-Button-Wrapper");

		initWidget(louter);

		lbtn.addClickHandler(new ClickHandler()
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
			if ( lrefParent instanceof SimpleForm )
				return ((SimpleForm)lrefParent).GetExternalParams();

			lrefParent = lrefParent.getParent();
		}

		return null;
	}

	public HandlerRegistration addJChangeHandler(JChangeEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(JChangeEvent.TYPE, handler);
	}
}
