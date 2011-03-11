package Jewel.Web.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

public class ClosableTab
	extends Composite
{
	private TabPanel mrefPanel;
	private Widget mrefWidget;

	public ClosableTab(String pstrText, TabPanel prefPanel, Widget prefWidget)
	{
		HorizontalPanel louter;
		Label llbl;
		Image limg;

		if ( !(prefWidget instanceof ClosableContent) )
		    throw new UnsupportedOperationException("A new widget must implement the ClosableWidget interface.");

		louter = new HorizontalPanel();

		llbl = new Label(pstrText);
		llbl.setStylePrimaryName("jewel-ClosableTabBar-Label");
		llbl.setWordWrap(false);
		louter.add(llbl);
		llbl.getElement().getParentElement().setClassName("jewel-ClosableTabBar-LabelWrapper");

		limg = new Image();
		limg.setUrl("images/closebox.png");
		limg.setStylePrimaryName("jewel-ClosableTabBar-CloseButton");
		louter.add(limg);
		limg.getElement().getParentElement().setClassName("jewel-ClosableTabBar-CloseButtonWrapper");

		mrefPanel = prefPanel;
		mrefWidget = prefWidget;

		initWidget(louter);

		limg.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mrefPanel.remove(mrefWidget);
	        }
	     });
	}
}
