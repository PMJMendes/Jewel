package Jewel.Mobile.client;

import Jewel.Mobile.client.events.*;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.ui.*;

public class SearchForm
	extends Composite
	implements ClosableContent, OkEvent.HasEvent
{
	private SimpleForm mfrmSearch;

	private HandlerManager mrefEventMgr;

	public SearchForm()
	{
		VerticalPanel lpnOuter;
		HorizontalPanel lpnInner;
		Button lbtnClear;
		Button lbtnSearch;

		lpnOuter = new VerticalPanel();
		lpnOuter.setStylePrimaryName("searchForm");

		mfrmSearch = new SimpleForm();
		lpnOuter.add(mfrmSearch);
		mfrmSearch.getElement().getParentElement().setClassName("searchForm-Form-Wrapper");

		lpnInner = new HorizontalPanel();
		lpnInner.setStylePrimaryName("searchForm-Toolbar");
		lpnOuter.add(lpnInner);
		lpnInner.getElement().getParentElement().setClassName("searchForm-Toolbar-Wrapper");

		lbtnSearch = new Button();
		lbtnSearch.setText("Search");
		lbtnSearch.setStylePrimaryName("searchForm-SearchButton");
		lpnInner.add(lbtnSearch);
		lbtnSearch.getElement().getParentElement().setClassName("searchForm-SearchButton-Wrapper");
		lbtnClear = new Button();
		lbtnClear.setText("Clear");
		lbtnClear.setStylePrimaryName("searchForm-ClearButton");
		lpnInner.add(lbtnClear);
		lbtnClear.getElement().getParentElement().setClassName("searchForm-ClearButton-Wrapper");

		initWidget(lpnOuter);

		lbtnClear.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mfrmSearch.ClearData();
	        }
	     });
		lbtnSearch.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mrefEventMgr.fireEvent(new OkEvent());
	        }
	     });

		mrefEventMgr = new HandlerManager(this);
	}

	public void InitSearch(String pstrFormID, String pstrNameSpace)
	{
		mfrmSearch.InitForm(pstrFormID, pstrNameSpace, null);
	}

	public String[] GetData()
	{
		return mfrmSearch.GetData();
	}

	public void DoClose()
	{
		mfrmSearch.DoClose();
	}

	public HandlerRegistration addOkHandler(OkEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(OkEvent.TYPE, handler);
	}
}
