package Jewel.Web.client;

import Jewel.Web.client.events.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class DynaSearch
	extends Composite
	implements ClosableContent, InitEvent.HasEvent, SelectEvent.HasEvent, CancelEvent.HasEvent, JErrorEvent.HasEvent
{
	private DynaSearchServiceAsync searchSvc;

	private String mstrFormID;
	private String mstrQueryID;
	private String mstrNameSpace;
	private String mstrTmpInitValue;
	private String mstrTmpFormID;
	private ParamInfo[] marrTmpParams;
	private boolean mbFormInit;
	private boolean mbGridInit;

	private VerticalPanel mpnMain;
	private DynaForm mobjSearchForm;
	private DynaGrid mobjResultsGrid;

	private HandlerManager mrefEventMgr;

	public DynaSearch()
	{
		Button lbtn;

		mbGridInit = false;
		mbFormInit = false;
		mstrQueryID = null;

		mpnMain = new VerticalPanel();
		mpnMain.setStylePrimaryName("jewel-DynaSearch");

		mobjSearchForm = new DynaForm();
		mpnMain.add(mobjSearchForm);

		lbtn = new Button();
		lbtn.setText("Search");
		lbtn.addStyleName("jewel-DynaSearch-Search");
		mpnMain.add(lbtn);

		mobjResultsGrid = new DynaGrid();
		mpnMain.add(mobjResultsGrid);
		mobjResultsGrid.getElement().getParentElement().addClassName("jewel-DynaSearch-Results");
		mobjResultsGrid.getElement().getParentElement().addClassName("alternate");

		initWidget(mpnMain);

		lbtn.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				doSearch();
	        }
	     });

		mobjSearchForm.addInitHandler(new InitEvent.Handler()
		{
			public void onInit(InitEvent event)
			{
				if (mbGridInit)
					mrefEventMgr.fireEvent(new InitEvent());
				mbFormInit = true;
			}
		});
		mobjSearchForm.addJErrorHandler(new JErrorEvent.Handler()
		{
			public void onJError(JErrorEvent event)
			{
				mrefEventMgr.fireEvent(event);
			}
		});

		mobjResultsGrid.addInitHandler(new InitEvent.Handler()
		{
			public void onInit(InitEvent event)
			{
				if (mbFormInit)
					mrefEventMgr.fireEvent(new InitEvent());
				mbGridInit = true;
			}
		});
		mobjResultsGrid.addJErrorHandler(new JErrorEvent.Handler()
		{
			public void onJError(JErrorEvent event)
			{
				mrefEventMgr.fireEvent(event);
			}
		});

		mrefEventMgr = new HandlerManager(this);
	}

	public void SetForPopup()
	{
		mobjResultsGrid.addSelectHandler(new SelectEvent.Handler()
		{
			public void onSelect(SelectEvent event)
			{
				mrefEventMgr.fireEvent(new SelectEvent(event.getResult()));
			}
		});
		mobjResultsGrid.addCancelHandler(new CancelEvent.Handler()
		{
			public void onCancel(CancelEvent event)
			{
				mrefEventMgr.fireEvent(new CancelEvent());
			}
		});

		mobjResultsGrid.SetForPopup();
	}

	private DynaSearchServiceAsync getService()
	{
		if ( searchSvc == null )
			searchSvc = GWT.create(DynaSearchService.class);

		return searchSvc;
	}

	public void InitSearch(String pstrFormID, String pstrNameSpace, String pstrInitialValue,
			String pstrParamFormID, ParamInfo[] parrExtParams)
	{
		AsyncCallback<String> callback = new AsyncCallback<String>()
        {
			public void onSuccess(String result)
			{
				if (result != null)
				{
					mstrQueryID = result;
					mobjResultsGrid.InitGrid(mstrQueryID, mstrNameSpace, false, null, mstrTmpFormID, marrTmpParams, mstrTmpInitValue, true);
					mstrTmpInitValue = null;
					mstrTmpFormID = null;
					marrTmpParams = null;
				}
				else
				{
					Jewel_Web.getReference().setLoginScreen();
				}
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				mrefEventMgr.fireEvent(new JErrorEvent(ex.getMessage()));
			}
        };

        if ( (mstrQueryID == null) || (!mstrFormID.equals(pstrFormID)) || (mstrNameSpace.equals(pstrNameSpace)) )
        {
        	mbFormInit = false;
        	mbGridInit = false;
	        mstrFormID = pstrFormID;
	        mstrNameSpace = pstrNameSpace;
			mobjSearchForm.InitForm(mstrFormID, pstrNameSpace, null);
			mstrTmpInitValue = pstrInitialValue;
			mstrTmpFormID = pstrParamFormID;
			marrTmpParams = parrExtParams;
			mrefEventMgr.fireEvent(new JErrorEvent(null));
	        getService().GetQueryID(mstrFormID, callback);
        }
        else
        {
        	mbGridInit = false;
			mobjResultsGrid.InitGrid(mstrQueryID, mstrNameSpace, false, null, pstrParamFormID, parrExtParams, pstrInitialValue, true);
        }
	}

	private void doSearch()
	{
		mobjResultsGrid.ApplySearch(mstrFormID, mobjSearchForm.GetData());
    }

	public void DoClose()
	{
		mobjSearchForm.DoClose();
		mobjResultsGrid.DoClose();
	}

	public HandlerRegistration addInitHandler(InitEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(InitEvent.TYPE, handler);
	}

	public HandlerRegistration addSelectHandler(SelectEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(SelectEvent.TYPE, handler);
	}

	public HandlerRegistration addCancelHandler(CancelEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(CancelEvent.TYPE, handler);
	}

	public HandlerRegistration addJErrorHandler(JErrorEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(JErrorEvent.TYPE, handler);
	}
}
