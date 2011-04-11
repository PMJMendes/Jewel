package Jewel.Mobile.client;

import Jewel.Mobile.client.events.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.ui.*;

public class PopupGrid
	extends Composite
	implements ClosableContent, InitEvent.HasEvent, SelectEvent.HasEvent, CancelEvent.HasEvent
{
	private DataObject mobjData;
	private boolean mbEdit;

	private SingleGrid mgrdTable;
	private VerticalPanel mpnMain;
	private VerticalPanel mpnInner;
	private Button mbtnSelect;
	private Button mbtnEmpty;
	private Button mbtnDetails;
	private Button mbtnCancel;

	private HandlerManager mrefEventMgr;

	public PopupGrid()
	{
		FlowPanel lpn;
		HorizontalPanel lhorz;

		mbEdit = false;

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

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("popupGrid-Resultsbar");
		lpn.add(lhorz);
		mbtnSelect = new Button();
		mbtnSelect.setText("Select");
		mbtnSelect.setStylePrimaryName("popupGrid-Select");
		mbtnSelect.setEnabled(false);
		lhorz.add(mbtnSelect);
		mbtnSelect.getElement().getParentElement().addClassName("popupGrid-Select-Wrapper");
		mbtnEmpty = new Button();
		mbtnEmpty.setText("Select (empty)");
		mbtnEmpty.setStylePrimaryName("popupGrid-Empty");
		lhorz.add(mbtnEmpty);
		mbtnEmpty.getElement().getParentElement().addClassName("popupGrid-Empty-Wrapper");
		mbtnDetails = new Button();
		mbtnDetails.setText("Details...");
		mbtnDetails.setStylePrimaryName("popupGrid-Details");
		mbtnDetails.setEnabled(false);
		lhorz.add(mbtnDetails);
		mbtnDetails.getElement().getParentElement().addClassName("popupGrid-Details-Wrapper");
		mbtnCancel = new Button();
		mbtnCancel.setText("Cancel");
		mbtnCancel.setStylePrimaryName("popupGrid-Cancel");
		lhorz.add(mbtnCancel);
		mbtnCancel.getElement().getParentElement().addClassName("popupGrid-Cancel-Wrapper");

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
				mbEdit = true;
				mgrdTable.LoadEditor(mobjData);
	        }
	    });
		mbtnCancel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mrefEventMgr.fireEvent(new CancelEvent());
	        }
	    });

		mgrdTable.addInitHandler(new InitEvent.Handler()
		{
			public void onInit(InitEvent event)
			{
				mbtnDetails.setEnabled(mgrdTable.GetSelected() > -1);
				mbtnSelect.setEnabled(mgrdTable.GetSelected() > -1);

				mrefEventMgr.fireEvent(event);
			}
		});
		mgrdTable.addSelectHandler(new SelectEvent.Handler()
		{
			public void onSelect(SelectEvent event)
			{
				mbtnDetails.setEnabled(true);
				mbtnSelect.setEnabled(true);
				mobjData = event.getResult();

				if ( mbEdit )
					mgrdTable.LoadEditor(mobjData);
			}
		});

		mrefEventMgr = new HandlerManager(this);
	}

	public void InitGrid(String pstrSearchFormID, String pstrQueryID, String pstrNameSpace, boolean pbForceParam,
			String pstrParam, String pstrFormID, ParamInfo[] parrExtParams, String pstrInitialValue)
	{
		mbEdit = false;

		mgrdTable.InitGrid(pstrSearchFormID, pstrQueryID, pstrNameSpace, pbForceParam, pstrParam, pstrFormID,
				parrExtParams, pstrInitialValue);
	}

	public void DoClose()
	{
		mgrdTable.DoClose();
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
}
