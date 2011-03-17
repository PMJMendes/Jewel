package Jewel.Mobile.client.controls;

import Jewel.Mobile.client.*;
import Jewel.Mobile.client.popups.*;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

public class DateCtl
	extends Composite
	implements IJewelMobileCtl
{
	private TextBox mtxtDisplay;
	private DatePopup mdlgPopup;

	public DateCtl()
	{
		HorizontalPanel louter;
		Image limg;

		louter = new HorizontalPanel();
		louter.setStylePrimaryName("formControl datebox");

		mtxtDisplay = new TextBox();
		mtxtDisplay.setReadOnly(true);
		mtxtDisplay.setStylePrimaryName("datebox-Display");
		louter.add(mtxtDisplay);

		limg = new Image();
		limg.setUrl("images/iconlookup.bmp");
		limg.setStylePrimaryName("datebox-Button");
		louter.add(limg);

		initWidget(louter);

		limg.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				DoPopup();
	        }
	     });

		mdlgPopup = null;
	}

	public String getJValue()
	{
		if (mtxtDisplay.getText().equals(""))
			return null;

		return mtxtDisplay.getText();
	}

	public void setJValue(String pstrValue)
	{
		mtxtDisplay.setText(pstrValue);
	}

	public void DoPopup()
	{
		if ( mdlgPopup == null )
		{
			mdlgPopup = new DatePopup(this);
			mdlgPopup.setText("Calendar");
			mdlgPopup.center();
		}

		mdlgPopup.InitPopup(getJValue());
		mdlgPopup.show();
	}
}
