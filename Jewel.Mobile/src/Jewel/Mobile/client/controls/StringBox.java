package Jewel.Mobile.client.controls;

import Jewel.Mobile.client.*;

import com.google.gwt.user.client.ui.*;

public class StringBox
	extends TextBox
	implements IJewelMobileCtl
{
	public StringBox()
	{
		setStylePrimaryName("formControl stringBox");
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
}
