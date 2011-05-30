package Jewel.Mobile.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;

public class ClosableEmpty
	extends Composite
	implements ClosableContent
{
	public ClosableEmpty(String pstrText)
	{
		ClosableHeader lheader;

		lheader = new ClosableHeader(pstrText);
		initWidget(lheader);

		lheader.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				Jewel_Mobile.getReference().setMenuScreen(Menu.GetRoot());
	        }
	    });
	}

	public void DoClose()
	{
	}
}
