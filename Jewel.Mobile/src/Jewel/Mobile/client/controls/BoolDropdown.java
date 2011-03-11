package Jewel.Mobile.client.controls;

import Jewel.Mobile.client.*;

import com.google.gwt.user.client.ui.*;

public class BoolDropdown
	extends ListBox
	implements IJewelMobileCtl
{
	public BoolDropdown()
	{
		addItem("");
		addItem("True");
		addItem("False");
		setSelectedIndex(0);

		setStylePrimaryName("formControl boolDropdown");
	}

	public String getJValue()
	{
		switch(getSelectedIndex())
		{
		case 1:
			return "1";

		case 2:
			return "0";

		default:
			return null;
		}
	}

	public void setJValue(String pstrValue)
	{
		if ( pstrValue == null )
		{
			setSelectedIndex(0);
			return;
		}

		if ( pstrValue.equals("1") || pstrValue.equals("True") || pstrValue.equals("TRUE") )
		{
			setSelectedIndex(1);
			return;
		}

		if ( pstrValue.equals("0") || pstrValue.equals("False") || pstrValue.equals("FALSE") )
		{
			setSelectedIndex(2);
			return;
		}

		setSelectedIndex(0);
	}
}
