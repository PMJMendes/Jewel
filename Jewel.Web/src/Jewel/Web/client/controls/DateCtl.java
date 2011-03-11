package Jewel.Web.client.controls;

import Jewel.Web.client.*;
import Jewel.Web.client.popups.*;

import com.google.gwt.dom.client.Style.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

public class DateCtl
	extends Composite
	implements IJewelWebCtl
{
	private TextBox mtxtDisplay;
	private DatePopup mdlgPopup;

	public DateCtl()
	{
		HorizontalPanel louter;
		Image limg;

		louter = new HorizontalPanel();
		louter.setStylePrimaryName("jewel-Control jewel-Datebox");

		mtxtDisplay = new TextBox();
		mtxtDisplay.setReadOnly(true);
		mtxtDisplay.setStylePrimaryName("jewel-Datebox-Display");
		louter.add(mtxtDisplay);

		limg = new Image();
		limg.setUrl("images/iconlookup.bmp");
		limg.setStylePrimaryName("jewel-Datebox-Button");
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

	public void setWidth(int plngWidth)
	{
		getElement().getStyle().setWidth(plngWidth-15, Unit.PX);
		mtxtDisplay.getElement().getStyle().setWidth(plngWidth-35, Unit.PX);
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
