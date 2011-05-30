package Jewel.Mobile.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.ui.*;

public class ClosableHeader
	extends Composite
	implements HasClickHandlers
{
	private Button mbtnClose;
	private Label mlblHeader;

	public ClosableHeader(String pstrText)
	{
		HorizontalPanel linner;
		
		linner = new HorizontalPanel();
		linner.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		linner.setStylePrimaryName("closableHeader");

		mbtnClose = new Button();
		mbtnClose.setText("Close");
		mbtnClose.setStylePrimaryName("closableHeader-CloseButton");
		linner.add(mbtnClose);
		mbtnClose.getElement().getParentElement().setClassName("closableHeader-CloseButton-Wrapper");

		mlblHeader = new Label(pstrText);
		mlblHeader.setStylePrimaryName("closableHeader-Label");
		mlblHeader.setWordWrap(false);
		linner.add(mlblHeader);
		mlblHeader.getElement().getParentElement().setClassName("closableHeader-Label-Wrapper");

		initWidget(linner);
	}
	
	public void setEnabled(boolean enabled)
	{
		mbtnClose.setEnabled(enabled);
	}

	public void SetText(String pstrText)
	{
		mlblHeader.setText(pstrText);
	}

	public HandlerRegistration addClickHandler(ClickHandler handler)
	{
		return mbtnClose.addClickHandler(handler);
	}
}
