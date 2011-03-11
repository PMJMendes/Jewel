package Jewel.Mobile.client.controls;

import Jewel.Mobile.client.*;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

public class DecBox
	extends TextBox
	implements IJewelMobileCtl
{
	private String mstrValue;

	public DecBox()
	{
		setText("");

		setStylePrimaryName("formControl decBox");

		addKeyUpHandler(new KeyUpHandler()
		{
			public void onKeyUp(KeyUpEvent event)
			{
				String lstrAux;

				lstrAux = getText();

				if ((lstrAux == null) || (lstrAux.equals("")))
				{
					mstrValue = null;
					return;
				}

				try
				{
					Double.parseDouble(lstrAux);
					mstrValue = lstrAux;
				}
				catch(NumberFormatException e)
				{
					innerSetText(mstrValue);
				}
			}
		});
	}

	public String getJValue()
	{
		return mstrValue;
	}

	public void setJValue(String pstrValue)
	{
		setText(pstrValue);
	}

	public void setText(String pstrValue)
	{
		if ((pstrValue == null) || (pstrValue.equals("")))
		{
			mstrValue = null;
			innerSetText("");
			return;
		}

		try
		{
			Double.parseDouble(pstrValue);
			mstrValue = pstrValue;
			innerSetText(mstrValue);
		}
		catch(NumberFormatException e)
		{
		}
	}

	private void innerSetText(String pstrValue)
	{
		super.setText(pstrValue);
	}
}
