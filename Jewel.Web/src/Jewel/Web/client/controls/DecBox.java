package Jewel.Web.client.controls;

import Jewel.Web.client.*;

import com.google.gwt.dom.client.Style.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

public class DecBox
	extends TextBox
	implements IJewelWebCtl
{
	private String mstrValue;

	public DecBox()
	{
		setText("");

		setStylePrimaryName("jewel-Control jewel-DecBox");

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

	public void setWidth(int plngWidth)
	{
		getElement().getStyle().setWidth(plngWidth-15, Unit.PX);
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
