package Jewel.Mobile.client;

import Jewel.Mobile.client.events.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.ui.*;

public class PopupGrid
	extends Composite
	implements ClosableContent, SelectEvent.HasEvent
{
	private DataObject mobjData;

	private SingleGrid mgrdTable;
	private VerticalPanel mpnMain;
	private VerticalPanel mpnInner;
	private HorizontalPanel mhorzResults;
	private Button mbtnSelect;
	private Button mbtnEmpty;
	private Button mbtnDetails;

	private HandlerManager mrefEventMgr;

	public PopupGrid()
	{
		FlowPanel lpn;

		mpnMain = new VerticalPanel();
		mpnMain.setStylePrimaryName("popupGrid");

		mpnInner = new VerticalPanel();
		mpnInner.setStylePrimaryName("popupGrid-Main");
		mpnMain.add(mpnInner);

		mgrdTable = new SingleGrid(true);
		mgrdTable.setStylePrimaryName("popupGrid-Grid");
		mpnInner.add(mgrdTable);

		lpn = new FlowPanel();
		lpn.setStyleName("popupGrid-Resultsbar-Wrapper");
		mpnInner.add(lpn);

		mhorzResults = new HorizontalPanel();
		mhorzResults.setStylePrimaryName("popupGrid-Resultsbar");
		lpn.add(mhorzResults);
		mbtnSelect = new Button();
		mbtnSelect.setText("Select");
		mbtnSelect.setStylePrimaryName("popupGrid-Select");
		mbtnSelect.setEnabled(false);
		mhorzResults.add(mbtnSelect);
		mbtnSelect.getElement().getParentElement().addClassName("popupGrid-Select-Wrapper");
		mbtnEmpty = new Button();
		mbtnEmpty.setText("Select (empty)");
		mbtnEmpty.setStylePrimaryName("popupGrid-Empty");
		mbtnEmpty.setEnabled(false);
		mhorzResults.add(mbtnEmpty);
		mbtnEmpty.getElement().getParentElement().addClassName("popupGrid-Empty-Wrapper");
		mbtnDetails = new Button();
		mbtnDetails.setText("Details...");
		mbtnDetails.setStylePrimaryName("popupGrid-Details");
		mbtnDetails.setEnabled(false);
		mhorzResults.add(mbtnDetails);
		mbtnDetails.getElement().getParentElement().addClassName("popupGrid-Details-Wrapper");

		initWidget(mpnMain);

		mbtnSelect.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mrefEventMgr.fireEvent(new SelectEvent(mobjData));
	        }
	    });
		mbtnEmpty.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mrefEventMgr.fireEvent(new SelectEvent(null));
	        }
	    });
		mbtnDetails.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mgrdTable.LoadEditor(mobjData);
				mhorzResults.setVisible(false);
	        }
	    });

		mgrdTable.addInitHandler(new InitEvent.Handler()
		{
			public void onInit(InitEvent event)
			{
				mbtnEmpty.setEnabled(true);
			}
		});
		mgrdTable.addSelectHandler(new SelectEvent.Handler()
		{
			public void onSelect(SelectEvent event)
			{
				mobjData = event.getResult();
				mbtnDetails.setEnabled(true);
				mbtnSelect.setEnabled(true);
			}
		});

		mrefEventMgr = new HandlerManager(this);
	}

	public void InitGrid(String pstrSearchFormID, String pstrQueryID, String pstrNameSpace, boolean pbForceParam,
			String pstrParam, String pstrFormID, ParamInfo[] parrExtParams, String pstrInitialValue)
	{
		mgrdTable.InitGrid(pstrSearchFormID, pstrQueryID, pstrNameSpace, pbForceParam, pstrParam, pstrFormID,
				parrExtParams, pstrInitialValue);
	}

	public boolean TryGoBack()
	{
		mhorzResults.setVisible(true);
		return mgrdTable.TryGoBack();
	}

	public void DoClose()
	{
		mgrdTable.DoClose();
	}

	public HandlerRegistration addSelectHandler(SelectEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(SelectEvent.TYPE, handler);
	}
}
