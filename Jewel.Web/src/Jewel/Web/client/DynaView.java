package Jewel.Web.client;

import Jewel.Web.client.events.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class DynaView
	extends TabPanel
	implements ClosableContent, ActionEvent.HasEvent, SaveEvent.HasEvent, DeleteEvent.HasEvent, JErrorEvent.HasEvent
{
	private DynaViewServiceAsync viewSvc;

	private String mstrViewID;
	private String mstrNameSpace;
	private DataObject mobjDataCache;
	private boolean mbAlternateColor;
	private boolean mbCanEdit;
	private boolean mbCanDelete;
	private boolean mbIsNewRow;

    private Button mbtnEdit;
    private Button mbtnSave;
    private Button mbtnCancel;
    private Button mbtnDelete;

	private HandlerManager mrefEventMgr;

    public DynaView()
    {
		super();

		HorizontalPanel ltmpH;
		VerticalPanel ltmpV;
		TabBar ltmpBar;
		HorizontalPanel lpnToolbar;

		setStylePrimaryName("jewel-DynaView");

		ltmpBar = getTabBar();
		ltmpV = (VerticalPanel)ltmpBar.getParent();
		ltmpV.remove(ltmpBar);
		ltmpH = new HorizontalPanel();
		ltmpH.setStylePrimaryName("jewel-DynaViewTabBar");
		ltmpH.add(ltmpBar);
		ltmpBar.getElement().getParentElement().setClassName("jewel-DynaView-TabBar-wrapper");

		lpnToolbar = new HorizontalPanel();
		lpnToolbar.setStylePrimaryName("jewel-DynaViewToolbar");
		ltmpH.add(lpnToolbar);
		lpnToolbar.getElement().getParentElement().setClassName("jewel-DynaViewToolbar-Wrapper");

		mbtnEdit = new Button("Edit");
		lpnToolbar.add(mbtnEdit);
		mbtnSave = new Button("Save");
		mbtnSave.setEnabled(false);
		lpnToolbar.add(mbtnSave);
		mbtnCancel = new Button("Cancel");
		mbtnCancel.setEnabled(false);
		lpnToolbar.add(mbtnCancel);
		mbtnDelete = new Button("Delete");
		mbtnDelete.setEnabled(false);
		lpnToolbar.add(mbtnDelete);

		ltmpV.insert(ltmpH, 0);

		mbtnEdit.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mbtnEdit.setEnabled(false);
				mbtnSave.setEnabled(mbCanEdit || mbIsNewRow);
				mbtnCancel.setEnabled(true);
				mbtnDelete.setEnabled(mbCanDelete || mbIsNewRow);
	        }
	     });
		mbtnSave.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mbtnSave.setEnabled(false);
				mbtnCancel.setEnabled(false);
				mbtnDelete.setEnabled(false);
				SaveData();
	        }
	     });
		mbtnCancel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				LoadData(mobjDataCache, mbIsNewRow);
	        }
	     });
		mbtnDelete.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				if ( !Window.confirm("Are you sure you wish to delete this entry?") )
					return;

				mbtnSave.setEnabled(false);
				mbtnCancel.setEnabled(false);
				mbtnDelete.setEnabled(false);
				DeleteRow();
	        }
	     });

		mrefEventMgr = new HandlerManager(this);
    }

	private DynaViewServiceAsync getService()
	{
		if ( viewSvc == null )
			viewSvc = GWT.create(DynaViewService.class);

		return viewSvc;
	}

	public void InitView(String pstrViewID, String pstrNameSpace, DataObject pobjData, boolean pbAlternate,
			boolean pbCanEdit, boolean pbCanDelete, boolean pbIsNewRow)
	{
		AsyncCallback<DynaViewResponse> callback = new AsyncCallback<DynaViewResponse>()
        {
			public void onSuccess(DynaViewResponse result)
			{
				if (result != null)
				{
					BuildView(result, mobjDataCache);
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

        mbCanEdit = pbCanEdit;
        mbCanDelete = pbCanDelete;
        mbIsNewRow = pbIsNewRow;
        ResetButtons();

        mstrViewID = pstrViewID;
        mstrNameSpace = pstrNameSpace;
		mbAlternateColor = pbAlternate;
        mobjDataCache = pobjData;
		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().GetTabs(mstrViewID, callback);
	}

	public void LoadData(DataObject pobjData, boolean pbIsNewRow)
	{
		int i;

		mbIsNewRow = pbIsNewRow;

        mobjDataCache = pobjData;
		for ( i = 0; i < getWidgetCount(); i++ )
		{
			if ( getWidget(i) instanceof DynaForm )
				((DynaForm)getWidget(i)).SetData(pobjData.marrData[i], pobjData.mstrID == null);
			if ( getWidget(i) instanceof DynaGrid )
				((DynaGrid)getWidget(i)).ReloadAt(true, pobjData.mstrID);
		}
		
		ResetButtons();
	}

	public DataObject GetData()
	{
		DataObject lobjData;
		int i;

		lobjData = new DataObject();
		lobjData.mstrID = mobjDataCache.mstrID;
		lobjData.mstrNameSpace = mobjDataCache.mstrNameSpace;
		lobjData.marrData = new String[getWidgetCount()][];

		for ( i = 0; i < getWidgetCount(); i++ )
		{
			if ( getWidget(i) instanceof DynaForm )
				lobjData.marrData[i] = ((DynaForm)getWidget(i)).GetData();
		}

		return lobjData;
	}

	public void ResetButtons()
	{
		mbtnEdit.setEnabled(mbCanEdit || mbCanDelete || mbIsNewRow);
		mbtnSave.setEnabled(false);
		mbtnCancel.setEnabled(false);
		mbtnDelete.setEnabled(false);
	}

	private void BuildView(DynaViewResponse prefResult, DataObject pobjData)
	{
		DynaFormActive lform;
		DynaGrid lgrid;
		DynaReport lreport;
		int i;

		for ( i = 0; i < prefResult.marrTabs.length; i++ )
		{
			if ( prefResult.marrTabs[i].mlngType == TabObj.FORMTAB )
			{
				lform = new DynaFormActive(i);
				lform.addJErrorHandler(new JErrorEvent.Handler()
				{
					public void onJError(JErrorEvent event)
					{
						mrefEventMgr.fireEvent(event);
					}
				});
				lform.addActionHandler(new ActionEvent.Handler()
				{
					public void onAction(ActionEvent event)
					{
						mrefEventMgr.fireEvent(event);
					}
				});
				add(lform, prefResult.marrTabs[i].mstrCaption);
				lform.InitForm(prefResult.marrTabs[i].mstrID, mstrNameSpace, pobjData.marrData[i], pobjData.mstrID == null);
			}
			if ( prefResult.marrTabs[i].mlngType == TabObj.GRIDTAB )
			{
				lgrid = new DynaGrid();
				lgrid.addJErrorHandler(new JErrorEvent.Handler()
				{
					public void onJError(JErrorEvent event)
					{
						mrefEventMgr.fireEvent(event);
					}
				});
				add(lgrid, prefResult.marrTabs[i].mstrCaption);
				lgrid.InitGrid(prefResult.marrTabs[i].mstrID, mstrNameSpace, true, pobjData.mstrID, null, null, null, mbAlternateColor);
			}
			if ( prefResult.marrTabs[i].mlngType == TabObj.PREVIEWTAB )
			{
				lreport = new DynaReport();
				lreport.addJErrorHandler(new JErrorEvent.Handler()
				{
					public void onJError(JErrorEvent event)
					{
						mrefEventMgr.fireEvent(event);
					}
				});
				add(lreport, prefResult.marrTabs[i].mstrCaption);
				lreport.InitReport(prefResult.marrTabs[i].mstrID, mstrNameSpace, pobjData.mstrID);
			}
		}

		if ( prefResult.marrTabs.length > 0 )
			selectTab(0);
	}

	private void SaveData()
	{
		mrefEventMgr.fireEvent(new SaveEvent());
	}

	private void DeleteRow()
	{
		mrefEventMgr.fireEvent(new DeleteEvent());
	}

	public void DoClose()
	{
		int i;
		
		for ( i = 0; i < getWidgetCount(); i++ )
		{
			if ( getWidget(i) instanceof ClosableContent )
				((ClosableContent)getWidget(i)).DoClose();
		}
	}

	public HandlerRegistration addActionHandler(ActionEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(ActionEvent.TYPE, handler);
	}

	public HandlerRegistration addSaveRowHandler(SaveEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(SaveEvent.TYPE, handler);
	}

	public HandlerRegistration addDeleteRowHandler(DeleteEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(DeleteEvent.TYPE, handler);
	}

	public HandlerRegistration addJErrorHandler(JErrorEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(JErrorEvent.TYPE, handler);
	}
}
