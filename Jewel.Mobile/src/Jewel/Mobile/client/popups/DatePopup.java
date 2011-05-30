package Jewel.Mobile.client.popups;

import java.sql.*;

import Jewel.Mobile.client.ClosableHeader;
import Jewel.Mobile.client.controls.*;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.*;

public class DatePopup
	extends DialogBox
{
	private DateCtl mrefOwner;

	private ClosableHeader mheader;
	private DatePicker mdtMain;
	private IntBox mtxtHours;
	private IntBox mtxtMinutes;
	private IntBox mtxtSeconds;
	private IntBox mtxtNanos;
	private Button mbtnEmpty;
	private Button mbtnOk;

	public DatePopup(DateCtl prefOwner)
	{
		super(false, true);

		VerticalPanel lvert;
		HorizontalPanel lhorz;
		HTML lhtml;

		mrefOwner = prefOwner;

		setStylePrimaryName("datePopup");

		setGlassEnabled(true);
		setGlassStyleName("datePopup-Glass");

		lvert = new VerticalPanel();

		mheader = new ClosableHeader("Calendar");
		lvert.add(mheader);

		mdtMain = new DatePicker();
		mdtMain.setStylePrimaryName("datePopup-Datepicker");
		lvert.add(mdtMain);
		mdtMain.getElement().getParentElement().setClassName("datePopup-Datepicker-Wrapper");

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("datePopup-Timebar");
		lvert.add(lhorz);
		lhorz.getElement().getParentElement().setClassName("datePopup-Timebar-Wrapper");
		lhorz.getElement().getParentElement().removeAttribute("align");

		lhtml = new HTML("Time:&nbsp;");
		lhtml.setStylePrimaryName("datePopup-Separator");
		lhorz.add(lhtml);
		lhtml.getElement().getParentElement().setClassName("datePopup-Separator-Wrapper");
		mtxtHours = new IntBox();
		mtxtHours.setStylePrimaryName("datePopup-Hours");
		lhorz.add(mtxtHours);
		mtxtHours.getElement().getParentElement().setClassName("datePopup-Hours-Wrapper");
		lhtml = new HTML(":");
		lhtml.setStylePrimaryName("datePopup-Separator");
		lhorz.add(lhtml);
		lhtml.getElement().getParentElement().setClassName("datePopup-Separator-Wrapper");
		mtxtMinutes = new IntBox();
		mtxtMinutes.setStylePrimaryName("datePopup-Minutes");
		lhorz.add(mtxtMinutes);
		mtxtMinutes.getElement().getParentElement().setClassName("datePopup-Minutes-Wrapper");
		lhtml = new HTML(":");
		lhtml.setStylePrimaryName("datePopup-Separator");
		lhorz.add(lhtml);
		lhtml.getElement().getParentElement().setClassName("datePopup-Separator-Wrapper");
		mtxtSeconds = new IntBox();
		mtxtSeconds.setStylePrimaryName("datePopup-Seconds");
		lhorz.add(mtxtSeconds);
		mtxtSeconds.getElement().getParentElement().setClassName("datePopup-Seconds-Wrapper");
		lhtml = new HTML(".");
		lhtml.setStylePrimaryName("datePopup-Separator");
		lhorz.add(lhtml);
		lhtml.getElement().getParentElement().setClassName("datePopup-Separator-Wrapper");
		mtxtNanos = new IntBox();
		mtxtNanos.setStylePrimaryName("datePopup-Nanos");
		lhorz.add(mtxtNanos);
		mtxtNanos.getElement().getParentElement().setClassName("datePopup-Nanos-Wrapper");

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("datePopup-Buttonbar");
		lvert.add(lhorz);
		lhorz.getElement().getParentElement().setClassName("datePopup-Buttonbar-Wrapper");

		mbtnOk = new Button();
		mbtnOk.setText("Ok");
		mbtnOk.setStylePrimaryName("datePopup-Ok");
		lhorz.add(mbtnOk);
		mbtnOk.getElement().getParentElement().setClassName("datePopup-Ok-Wrapper");
		mbtnEmpty = new Button();
		mbtnEmpty.setText("Select (empty)");
		mbtnEmpty.setStylePrimaryName("datePopup-Empty");
		lhorz.add(mbtnEmpty);
		mbtnEmpty.getElement().getParentElement().setClassName("datePopup-Empty-Wrapper");

		setWidget(lvert);

		mheader.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				hide();
	        }
		});

		mbtnOk.addClickHandler(new ClickHandler()
		{
			@SuppressWarnings("deprecation")
			public void onClick(ClickEvent event)
	        {
				Timestamp ltAux;
				String lstrAux;
				int i;

				ltAux = new Timestamp(mdtMain.getValue().getTime());

				lstrAux = mtxtHours.getText();
				i = (lstrAux.equals("") ? 0 : Integer.parseInt(lstrAux));
				if ( i != 0 )
					ltAux.setHours(i);

				lstrAux = mtxtMinutes.getText();
				i = (lstrAux.equals("") ? 0 : Integer.parseInt(lstrAux));
				if ( i != 0 )
					ltAux.setMinutes(i);

				lstrAux = mtxtSeconds.getText();
				i = (lstrAux.equals("") ? 0 : Integer.parseInt(lstrAux));
				if ( i != 0 )
					ltAux.setSeconds(i);

				lstrAux = mtxtNanos.getText();
				i = (lstrAux.equals("") ? 0 : Integer.parseInt(lstrAux));
				if ( i != 0 )
					ltAux.setNanos(i * 1000000);

				if ( ltAux.getTime() == 0 )
					mrefOwner.setJValue(null);
				else
					mrefOwner.setJValue(ltAux.toString());
				hide();
	        }
		});
		mbtnEmpty.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mrefOwner.setJValue(null);
				hide();
	        }
		});
	}

	@SuppressWarnings("deprecation")
	public void InitPopup(String pstrValue)
	{
		Timestamp ltAux;

		if ( (pstrValue == null) || (pstrValue.equals("")) )
		{
			mdtMain.setValue(new Timestamp(0), false);
			mtxtHours.setText("00");
			mtxtMinutes.setText("00");
			mtxtSeconds.setText("00");
			mtxtNanos.setText("000");
		}
		else
		{
			ltAux = Timestamp.valueOf(pstrValue);
			mtxtHours.setText(Integer.toString(ltAux.getHours()));
			mtxtMinutes.setText(Integer.toString(ltAux.getMinutes()));
			mtxtSeconds.setText(Integer.toString(ltAux.getSeconds()));
			mtxtNanos.setText(Integer.toString(ltAux.getNanos() / 1000000));
			ltAux.setHours(0);
			ltAux.setMinutes(0);
			ltAux.setSeconds(0);
			ltAux.setNanos(0);
			mdtMain.setValue(ltAux, false);
		}
	}
}
