package Jewel.Web.client.controls;

import Jewel.Web.client.*;

import com.google.gwt.dom.client.Style.*;
import com.google.gwt.user.client.ui.*;

public class PasswordBox
	extends PasswordTextBox
	implements IJewelWebCtl
{
	public PasswordBox()
	{
		setStylePrimaryName("jewel-Control jewel-PasswordBox");
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
