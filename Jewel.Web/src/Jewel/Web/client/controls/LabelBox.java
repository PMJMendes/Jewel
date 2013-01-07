package Jewel.Web.client.controls;

import Jewel.Web.client.IJewelWebCtl;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;

public class LabelBox
	extends Label
	implements IJewelWebCtl
{
	public LabelBox()
	{
		setStylePrimaryName("jewel-Control jewel-LabelBox");
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
