package Jewel.Mobile.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.ui.*;

public class ClosableHeader
	extends Composite
	implements HasClickHandlers
{
	Button mbtnClose;

	public ClosableHeader(String pstrText)
	{
		HorizontalPanel linner;
		Label llbl;
		
		linner = new HorizontalPanel();
		linner.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		linner.setStylePrimaryName("closableHeader");

		llbl = new Label(pstrText);
		llbl.setStylePrimaryName("closableHeader-Label");
		llbl.setWordWrap(false);
		linner.add(llbl);
		llbl.getElement().getParentElement().setClassName("closableHeader-Label-Wrapper");

		mbtnClose = new Button();
		mbtnClose.setText("Close");
		mbtnClose.setStylePrimaryName("closableHeader-CloseButton");
		linner.add(mbtnClose);
		mbtnClose.getElement().getParentElement().setClassName("closableHeader-CloseButton-Wrapper");

		initWidget(linner);
	}

	public HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return mbtnClose.addClickHandler(handler);
	}
}
