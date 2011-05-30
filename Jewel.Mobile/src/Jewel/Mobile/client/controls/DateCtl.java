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
		Button lbtn;

		mdlgPopup = null;

		louter = new HorizontalPanel();
		louter.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		louter.setStylePrimaryName("formControl datebox");

		mtxtDisplay = new TextBox();
		mtxtDisplay.setReadOnly(true);
		mtxtDisplay.setStylePrimaryName("datebox-Display");
		louter.add(mtxtDisplay);
		mtxtDisplay.getElement().getParentElement().setClassName("datebox-Display-Wrapper");

		lbtn = new Button("?");
		lbtn.setStylePrimaryName("datebox-Button");
		louter.add(lbtn);
		lbtn.getElement().getParentElement().setClassName("datebox-Button-Wrapper");

		initWidget(louter);

		lbtn.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				DoPopup();
	        }
	     });
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
			mdlgPopup = new DatePopup(this);

		mdlgPopup.InitPopup(getJValue());
		mdlgPopup.show();
	}
}
