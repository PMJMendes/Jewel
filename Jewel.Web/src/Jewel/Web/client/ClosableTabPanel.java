package Jewel.Web.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.*;

public class ClosableTabPanel
	extends TabPanel
{
	private Button mbtnCloseAll;
	private boolean mbVisible;

	public ClosableTabPanel()
	{
		super();

		HorizontalPanel ltmpH;
		VerticalPanel ltmpV;
		TabBar ltmpBar;

		setStylePrimaryName("jewel-ClosableTabPanel");

		mbVisible = super.isVisible();

		ltmpBar = getTabBar();
		ltmpV = (VerticalPanel)ltmpBar.getParent();
		ltmpV.remove(ltmpBar);
		ltmpH = new HorizontalPanel();
		ltmpH.setStylePrimaryName("jewel-ClosableTabBar");
		ltmpH.add(ltmpBar);
		ltmpBar.getElement().getParentElement().setClassName("jewel-ClosableTabBar-TabBar-wrapper");
		mbtnCloseAll = new Button("Close All");
		mbtnCloseAll.setStylePrimaryName("jewel-ClosableTabBar-CloseAll");
		ltmpH.add(mbtnCloseAll);
		mbtnCloseAll.getElement().getParentElement().setClassName("jewel-ClosableTabBar-CloseAll-wrapper");
		ltmpV.insert(ltmpH, 0);

		mbtnCloseAll.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				clear();
	        }
	     });

		Window.addCloseHandler(new CloseHandler<Window>()
		{
			public void onClose(CloseEvent<Window> event)
			{
				clear();
			}
		});

		forceVisible(false);
	}

	protected boolean queryVisible()
	{
		return super.isVisible();
	}

	protected void forceVisible(boolean pbVisible)
	{
		super.setVisible(mbVisible && pbVisible);
	}

	public boolean isVisible()
	{
		return mbVisible;
	}
	
	public void setVisible(boolean pbVisible)
	{
		mbVisible = pbVisible;
		super.setVisible(pbVisible && (getWidgetCount() > 0));
	}
	
	public void insert(Widget widget, String tabText, boolean asHTML, int beforeIndex)
	{
	    throw new UnsupportedOperationException("A new tab must use the ClosableTab widget.");
	}

	public void insert(Widget widget, Widget tabWidget, int beforeIndex)
	{
		if ( !(tabWidget instanceof ClosableTab) )
		    throw new UnsupportedOperationException("A new tab must use the ClosableTab widget.");

		forceVisible(true);
		super.insert(widget, tabWidget, beforeIndex);
		selectTab(getWidgetCount() - 1);
	}

	public boolean remove(int plngIndex)
	{
		ClosableContent prefContent;
		int llngAux;
		boolean lb;

		try
		{
			prefContent = (ClosableContent)getWidget(plngIndex);
		}
		catch (Throwable e)
		{
			prefContent = null;
		}

		llngAux = getDeckPanel().getVisibleWidget();

		lb = super.remove(plngIndex);

		if (lb)
		{
			if (prefContent != null)
				prefContent.DoClose();

			if (getWidgetCount() < 1)
			{
				forceVisible(false);
			}
			else if (llngAux == plngIndex)
			{
				if ( llngAux >= getWidgetCount() )
					selectTab(getWidgetCount() - 1);
				else
					selectTab(llngAux);
			}
		}

		return lb;
	}

	public boolean remove(Widget prefWidget)
	{
		return remove(getWidgetIndex(prefWidget));
	}
}
