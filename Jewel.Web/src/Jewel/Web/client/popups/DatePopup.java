package Jewel.Web.client.popups;

import java.sql.Timestamp;

import Jewel.Web.client.controls.DateCtl;
import Jewel.Web.client.controls.IntBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DatePopup
	extends DialogBox
{
	private DatePicker mdtMain;
	private IntBox mtxtHours;
	private IntBox mtxtMinutes;
	private IntBox mtxtSeconds;
	private Button mbtnEmpty;
	private Button mbtnOk;
	private Button mbtnCancel;

	private DateCtl mrefOwner;
	
	private int mlngNanos;

	public DatePopup(DateCtl prefOwner)
	{
		super(false, true);
		
		VerticalPanel lvert;
		HorizontalPanel lhorz;
		HTML lhtml;

		mrefOwner = prefOwner;

		this.setStylePrimaryName("jewel-enhanced-DatePopup");
		this.setAutoHideEnabled(true);
		this.setPopupPosition(prefOwner.getXPos() + 5, prefOwner.getYPos());

		lvert = new VerticalPanel();

		mdtMain = new DatePicker();
		mdtMain.setStylePrimaryName("jewel-enhanced-DatePicker");
		lvert.add(mdtMain);

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("jewel-DatePopup-Timebar");
		mtxtHours = new IntBox();
		mtxtHours.setStylePrimaryName("jewel-enhanced-Control jewel-enhanced-DatePopup-Hours");
		lhorz.add(mtxtHours);
		lhtml = new HTML(":");
		lhtml.setStylePrimaryName("jewel-enhanced-DatePopup-Separator");
		lhorz.add(lhtml);
		lhtml.getElement().getParentElement().addClassName("jewel-DatePopup-Separator-Wrapper");
		mtxtMinutes = new IntBox();
		mtxtMinutes.setStylePrimaryName("jewel-enhanced-Control jewel-enhanced-DatePopup-Minutes");
		lhorz.add(mtxtMinutes);
		lvert.add(lhorz);
		lhtml = new HTML(":");
		lhtml.setStylePrimaryName("jewel-enhanced-DatePopup-Separator");
		lhorz.add(lhtml);
		lhtml.getElement().getParentElement().addClassName("jewel-DatePopup-Separator-Wrapper");
		mtxtSeconds = new IntBox();
		mtxtSeconds.setStylePrimaryName("jewel-enhanced-Control jewel-enhanced-DatePopup-Seconds");
		lhorz.add(mtxtSeconds);

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("jewel-DatePopup-Buttonbar");
		mbtnEmpty = new Button();
		mbtnEmpty.setStylePrimaryName("google-button google-button-blue");
		mbtnEmpty.setText("Limpar");
		lhorz.add(mbtnEmpty);
		mbtnEmpty.getElement().getParentElement().addClassName("jewel-DatePopup-Button-Wrapper");
		lhtml = new HTML("&nbsp;");
		lhtml.setStylePrimaryName("jewel-DatePopup-Buttongap");
		lhorz.add(lhtml);
		lhtml.getElement().getParentElement().addClassName("jewel-DatePopup-Buttongap-Wrapper");
		mbtnOk = new Button();
		mbtnOk.setStylePrimaryName("google-button google-button-blue");
		mbtnOk.setText("Ok");
		lhorz.add(mbtnOk);
		mbtnOk.getElement().getParentElement().addClassName("jewel-DatePopup-Button-Wrapper");
		mbtnCancel = new Button();
		mbtnCancel.setStylePrimaryName("google-button google-button-blue");
		mbtnCancel.setText("Cancelar");
		lhorz.add(mbtnCancel);
		mbtnCancel.getElement().getParentElement().addClassName("jewel-DatePopup-Button-Wrapper");
		lvert.add(lhorz);

		setWidget(lvert);

		mbtnEmpty.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mrefOwner.setJValue("blank");
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
				ltAux.setHours(i);

				lstrAux = mtxtMinutes.getText();
				i = (lstrAux.equals("") ? 0 : Integer.parseInt(lstrAux));
				ltAux.setMinutes(i);

				lstrAux = mtxtSeconds.getText();
				i = (lstrAux.equals("") ? 0 : Integer.parseInt(lstrAux));
				ltAux.setSeconds(i);

				ltAux.setNanos(mlngNanos);

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
			ltAux = new Timestamp(new java.util.Date().getTime());
			mdtMain.setValue(ltAux, false);
			mtxtHours.setText("00");
			mtxtMinutes.setText("00");
			mtxtSeconds.setText("00");
			mlngNanos = 0;
		}
		else
		{
			ltAux = Timestamp.valueOf(pstrValue);
			mtxtHours.setText(Integer.toString(ltAux.getHours()));
			mtxtMinutes.setText(Integer.toString(ltAux.getMinutes()));
			mtxtSeconds.setText(Integer.toString(ltAux.getSeconds()));
			mlngNanos = ltAux.getNanos() / 1000000;
			mdtMain.setValue(ltAux, false);
		}
	}
}
