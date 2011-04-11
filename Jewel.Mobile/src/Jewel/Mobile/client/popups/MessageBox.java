package Jewel.Mobile.client.popups;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

public class MessageBox
	extends PopupPanel
{
	public MessageBox(String pstrMsg)
	{
		super(true);

		Label llbl;

		setStylePrimaryName("errorPopup");
		
		setGlassEnabled(true);
		setGlassStyleName("errorPopup-Glass");

		llbl = new Label(pstrMsg);
		llbl.setStylePrimaryName("errorPopup-Message");
		
		setWidget(llbl);
		llbl.getElement().getParentElement().setClassName("errorPopup-Message-Wrapper");

		llbl.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				hide();
	        }
	     });
	}
}
