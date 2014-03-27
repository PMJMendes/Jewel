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
	private Button mbtnSearch;
	private Button mbtnClear;

	private HandlerManager mrefEventMgr;

	public SearchForm()
	{
		VerticalPanel lpnOuter;
		HorizontalPanel lpnInner;

		lpnOuter = new VerticalPanel();
		lpnOuter.setStylePrimaryName("searchForm");

		mfrmSearch = new SimpleForm();
		lpnOuter.add(mfrmSearch);
		mfrmSearch.getElement().getParentElement().setClassName("searchForm-Form-Wrapper");

		lpnInner = new HorizontalPanel();
		lpnInner.setStylePrimaryName("searchForm-Toolbar");
		lpnOuter.add(lpnInner);
		lpnInner.getElement().getParentElement().setClassName("searchForm-Toolbar-Wrapper");

		mbtnSearch = new Button();
		mbtnSearch.setText("Search");
		mbtnSearch.setStylePrimaryName("searchForm-SearchButton");
		mbtnSearch.setEnabled(false);
		lpnInner.add(mbtnSearch);
		mbtnSearch.getElement().getParentElement().setClassName("searchForm-SearchButton-Wrapper");
		mbtnClear = new Button();
		mbtnClear.setText("Clear");
		mbtnClear.setStylePrimaryName("searchForm-ClearButton");
		mbtnClear.setEnabled(false);
		lpnInner.add(mbtnClear);
		mbtnClear.getElement().getParentElement().setClassName("searchForm-ClearButton-Wrapper");

		initWidget(lpnOuter);

		mfrmSearch.addInitHandler(new InitEvent.Handler()
		{
			public void onInit(InitEvent event)
			{
				mbtnSearch.setEnabled(true);
				mbtnClear.setEnabled(true);
			}
		});
		mbtnClear.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mfrmSearch.ClearData();
	        }
	     });
		mbtnSearch.addClickHandler(new ClickHandler()
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
		mfrmSearch.InitForm(pstrFormID, pstrNameSpace, null, true);
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
