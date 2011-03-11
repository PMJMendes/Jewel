package Jewel.Web.client.controls;

import Jewel.Web.client.*;

import com.google.gwt.dom.client.Style.*;
import com.google.gwt.user.client.ui.*;

public class BoolDropdown
	extends ListBox
	implements IJewelWebCtl
{
	public BoolDropdown()
	{
		addItem("");
		addItem("True");
		addItem("False");
		setSelectedIndex(0);

		setStylePrimaryName("jewel-Control jewel-BoolDropdown");
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

	public void setWidth(int plngWidth)
	{
		getElement().getStyle().setWidth(plngWidth-15, Unit.PX);
	}
}
