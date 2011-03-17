package Jewel.Mobile.client.popups;

import java.sql.*;

import Jewel.Mobile.client.controls.*;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.datepicker.client.*;

public class DatePopup
	extends DialogBox
{
	private DatePicker mdtMain;
	private IntBox mtxtHours;
	private IntBox mtxtMinutes;
	private IntBox mtxtSeconds;
	private IntBox mtxtNanos;
	private Button mbtnEmpty;
	private Button mbtnOk;
	private Button mbtnCancel;

	private DateCtl mrefOwner;

	public DatePopup(DateCtl prefOwner)
	{
		super(false, true);

		VerticalPanel lvert;
		HorizontalPanel lhorz;
		HTML lhtml;

		mrefOwner = prefOwner;

		this.setStylePrimaryName("datePopup");

		lvert = new VerticalPanel();

		mdtMain = new DatePicker();
		mdtMain.setStylePrimaryName("datePopup-Datepicker");
		lvert.add(mdtMain);

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("datePopup-Timebar");
		mtxtHours = new IntBox();
		mtxtHours.setStylePrimaryName("formControl datePopup-Hours");
		lhorz.add(mtxtHours);
		lhtml = new HTML(":");
		lhtml.setStylePrimaryName("datePopup-Separator");
		lhorz.add(lhtml);
		lhtml.getElement().getParentElement().addClassName("datePopup-Separator-Wrapper");
		mtxtMinutes = new IntBox();
		mtxtMinutes.setStylePrimaryName("formControl datePopup-Minutes");
		lhorz.add(mtxtMinutes);
		lhtml = new HTML(":");
		lhtml.setStylePrimaryName("datePopup-Separator");
		lhorz.add(lhtml);
		lhtml.getElement().getParentElement().addClassName("datePopup-Separator-Wrapper");
		mtxtSeconds = new IntBox();
		mtxtSeconds.setStylePrimaryName("formControl datePopup-Seconds");
		lhorz.add(mtxtSeconds);
		lhtml = new HTML(".");
		lhtml.setStylePrimaryName("datePopup-Separator");
		lhorz.add(lhtml);
		lhtml.getElement().getParentElement().addClassName("datePopup-Separator-Wrapper");
		mtxtNanos = new IntBox();
		mtxtNanos.setStylePrimaryName("formControl datePopup-Nanos");
		lhorz.add(mtxtNanos);
		lvert.add(lhorz);

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("datePopup-Buttonbar");
		mbtnEmpty = new Button();
		mbtnEmpty.setText("Select (empty)");
		lhorz.add(mbtnEmpty);
		mbtnEmpty.getElement().getParentElement().addClassName("datePopup-Button-Wrapper");
		lhtml = new HTML("&nbsp;");
		lhtml.setStylePrimaryName("datePopup-Buttongap");
		lhorz.add(lhtml);
		lhtml.getElement().getParentElement().addClassName("datePopup-Buttongap-Wrapper");
		mbtnOk = new Button();
		mbtnOk.setText("Ok");
		lhorz.add(mbtnOk);
		mbtnOk.getElement().getParentElement().addClassName("datePopup-Button-Wrapper");
		mbtnCancel = new Button();
		mbtnCancel.setText("Cancel");
		lhorz.add(mbtnCancel);
		mbtnCancel.getElement().getParentElement().addClassName("datePopup-Button-Wrapper");
		lvert.add(lhorz);

		setWidget(lvert);

		mbtnEmpty.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mrefOwner.setJValue(null);
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
		mbtnCancel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
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
