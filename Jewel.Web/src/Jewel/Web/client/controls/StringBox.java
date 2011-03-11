package Jewel.Web.client.controls;

import Jewel.Web.client.*;

import com.google.gwt.dom.client.Style.*;
import com.google.gwt.user.client.ui.*;

public class StringBox
	extends TextBox
	implements IJewelWebCtl
{
	public StringBox()
	{
		setStylePrimaryName("jewel-Control jewel-StringBox");
	}

	public String getJValue()
	{
		if (getText().equals(""))
			return null;

		return getText();
	}

	public void setJValue(String pstrValue)
	{
		if (pstrValue == null)
			setText("");
		else
			setText(pstrValue);
	}

	public void setWidth(int plngWidth)
	{
		getElement().getStyle().setWidth(plngWidth - 15, Unit.PX);
	}
}
