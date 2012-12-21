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
	private Timestamp now;
	private DateTimeFormat formatter;

	@SuppressWarnings("deprecation" )
	public DateCtl()
	{
		formatter = DateTimeFormat.getFormat(ENHANCED_DATE_FORMAT);
		
		HorizontalPanel louter;
		Image limg;

		louter = new HorizontalPanel();
		louter.setStylePrimaryName("jewel-Control jewel-Datebox");

		mtxtDisplay = new TextBox();
		mtxtDisplay.setReadOnly(true);
		now = new Timestamp(new java.util.Date().getTime());
		now.setHours(0);
		now.setMinutes(0);
		now.setSeconds(0);
		mtxtDisplay.setText(formatter.format(now));
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
		if (mtxtDisplay.getText().equals(""))
			return null;

		Date tmpDate;
		Timestamp tmp;
		
		try{
			tmpDate = formatter.parse(mtxtDisplay.getText());
			tmp = new Timestamp(tmpDate.getTime());
			return tmp.toString();
		}catch(IllegalArgumentException e){
			return mtxtDisplay.getText();
		}
	}

	@SuppressWarnings("deprecation")
	public void setJValue(String pstrValue)
	{
		if (null == pstrValue){
			now = new Timestamp(new java.util.Date().getTime());
			now.setHours(0);
			now.setMinutes(0);
			now.setSeconds(0);
			mtxtDisplay.setText(formatter.format(now));
		}
		else{
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
