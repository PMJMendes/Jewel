package Jewel.Web.client.controls;

import java.sql.Timestamp;
import java.util.Date;

import Jewel.Web.client.IJewelWebCtl;
import Jewel.Web.client.popups.DatePopup;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;

public class DateCtl
	extends Composite
	implements IJewelWebCtl, IDateCtl
{
	public static String ENHANCED_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private TextBox mtxtDisplay;
	private DatePopup mdlgPopup;
	private DateTimeFormat formatter;

	public DateCtl()
	{
		formatter = DateTimeFormat.getFormat(ENHANCED_DATE_FORMAT);
		
		HorizontalPanel louter;
		Image limg;

		louter = new HorizontalPanel();
		louter.setStylePrimaryName("jewel-Control jewel-Datebox");

		mtxtDisplay = new TextBox();
		mtxtDisplay.setReadOnly(true);
		mtxtDisplay.setStylePrimaryName("jewel-Datebox-Display");
		louter.add(mtxtDisplay);

		limg = new Image();
		limg.setUrl("images/calendar.png");
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
		Date tmpDate;
		Timestamp tmp;
		
		if (mtxtDisplay.getText().equals(""))
			return null;

		try
		{
			tmpDate = formatter.parse(mtxtDisplay.getText());
			tmp = new Timestamp(tmpDate.getTime());
			return tmp.toString();
		}
		catch(IllegalArgumentException e)
		{
			return mtxtDisplay.getText();
		}
	}

	public void setJValue(String pstrValue)
	{
		if ( (null == pstrValue) || (pstrValue.equals("blank")) )
		{
			mtxtDisplay.setText("");
		}
		else
		{
			String tmp = formatter.format(Timestamp.valueOf(pstrValue));
			mtxtDisplay.setText(tmp);
		}
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
		}

		mdlgPopup.InitPopup(getJValue());
		mdlgPopup.show();
	}

	@Override
	public int getXPos() {
		return this.getAbsoluteLeft();
	}

	@Override
	public int getYPos() {
		return this.getAbsoluteTop() + this.getOffsetHeight();
	}
}
