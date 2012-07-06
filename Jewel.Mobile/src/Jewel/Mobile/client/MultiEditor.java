package Jewel.Mobile.client;

import Jewel.Mobile.client.events.ActionEvent;
import Jewel.Mobile.client.events.CancelEvent;
import Jewel.Mobile.client.events.DeleteEvent;
import Jewel.Mobile.client.events.InitEvent;
import Jewel.Mobile.client.events.SaveEvent;
import Jewel.Mobile.client.events.SelectEvent;
import Jewel.Mobile.interfaces.MultiEditorService;
import Jewel.Mobile.interfaces.MultiEditorServiceAsync;
import Jewel.Mobile.shared.DataObject;
import Jewel.Mobile.shared.EditorResponse;
import Jewel.Mobile.shared.TabObj;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MultiEditor
	extends Composite
	implements ClosableContent, ActionEvent.HasEvent, SaveEvent.HasEvent, DeleteEvent.HasEvent
{
	private MultiEditorServiceAsync editorSvc;

	private String mstrViewID;
	private String mstrNameSpace;
	private DataObject mobjDataCache;
	private int mlngInitCount;
	private boolean mbInit;
	private boolean mbEnabled;
	private int mlngSelected;

	HorizontalPanel mpnToolbar;
	private ListBox mlbxSelector;
    private Button mbtnSave;
    private Button mbtnCancel;
    private Button mbtnDelete;
	HorizontalPanel mpnContent;

	private HandlerManager mrefEventMgr;

	public MultiEditor()
	{
		VerticalPanel louter;

		mbInit = false;
		mbEnabled = true;

		louter = new VerticalPanel();
		louter.setStylePrimaryName("multiEditor");

		mpnToolbar = new HorizontalPanel();
		mpnToolbar.setStylePrimaryName("multiEditor-Toolbar");
		louter.add(mpnToolbar);
		mpnToolbar.getElement().getParentElement().setClassName("multiEditor-Toolbar-Wrapper");

		mbtnSave = new Button("Save");
		mbtnSave.setStylePrimaryName("multiEditor-Save");
		mpnToolbar.add(mbtnSave);
		mbtnSave.getElement().getParentElement().setClassName("multiEditor-Save-Wrapper");
		mbtnCancel = new Button("Revert");
		mbtnCancel.setStylePrimaryName("multiEditor-Cancel");
		mpnToolbar.add(mbtnCancel);
		mbtnCancel.getElement().getParentElement().setClassName("multiEditor-Cancel-Wrapper");
		mbtnDelete = new Button("Delete");
		mbtnDelete.setStylePrimaryName("multiEditor-Delete");
		mpnToolbar.add(mbtnDelete);
		mbtnDelete.getElement().getParentElement().setClassName("multiEditor-Delete-Wrapper");

		mlbxSelector = new ListBox();
		mlbxSelector.setStylePrimaryName("multiEditor-Selector");
		mlbxSelector.setVisible(false);
		louter.add(mlbxSelector);
		mlbxSelector.getElement().getParentElement().setClassName("multiEditor-SelectorWrapper");

		mpnContent = new HorizontalPanel();
		mpnContent.setStylePrimaryName("multiEditor-Content");
		louter.add(mpnContent);
		mpnContent.getElement().getParentElement().setClassName("multiEditor-Content-Wrapper");

		ResetButtons();

		initWidget(louter);
		
		mlbxSelector.addChangeHandler(new ChangeHandler()
		{
			public void onChange(ChangeEvent event)
			{
				DoChangeView();
			}
		});
		mbtnSave.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mbEnabled = false;
				ResetButtons();
				mrefEventMgr.fireEvent(new SaveEvent());
	        }
	    });
		mbtnCancel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				LoadData(mobjDataCache);
			}
		});
		mbtnDelete.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				if ( !Window.confirm("Are you sure you wish to delete this entry?") )
					return;

				mbEnabled = false;
				ResetButtons();
				mrefEventMgr.fireEvent(new DeleteEvent());
	        }
	    });

		mrefEventMgr = new HandlerManager(this);
	}

	private MultiEditorServiceAsync getService()
	{
		if ( editorSvc == null )
			editorSvc = GWT.create(MultiEditorService.class);

		return editorSvc;
	}

	public void InitEditor(String pstrViewID, String pstrNameSpace, DataObject pobjData)
	{
		AsyncCallback<EditorResponse> callback = new AsyncCallback<EditorResponse>()
        {
			public void onSuccess(EditorResponse result)
			{
				if (result != null)
				{
					BuildEditor(result, mobjDataCache);
				}
				else
				{
					Jewel_Mobile.getReference().setLoginScreen();
				}
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				Jewel_Mobile.getReference().showError(ex.getMessage());
			}
        };

        mstrViewID = pstrViewID;
        mstrNameSpace = pstrNameSpace;
        mobjDataCache = pobjData;
        getService().GetTabs(mstrViewID, callback);
	}

	public void LoadData(DataObject pobjData)
	{
		int i;
		
        mobjDataCache = pobjData;
		for ( i = 0; i < mpnContent.getWidgetCount(); i++ )
		{
			if ( mpnContent.getWidget(i) instanceof EditorForm )
				((EditorForm)mpnContent.getWidget(i)).SetData(pobjData.marrData[i]);
			if ( mpnContent.getWidget(i) instanceof SingleGrid )
				((SingleGrid)mpnContent.getWidget(i)).ReloadAt(true, pobjData.mstrID);
		}
	}

	public DataObject GetData()
	{
		DataObject lobjData;
		int i;

		lobjData = new DataObject();
		lobjData.mstrID = mobjDataCache.mstrID;
		lobjData.mstrNameSpace = mobjDataCache.mstrNameSpace;
		lobjData.marrData = new String[mpnContent.getWidgetCount()][];

		for ( i = 0; i < mpnContent.getWidgetCount(); i++ )
		{
			if ( mpnContent.getWidget(i) instanceof EditorForm )
				lobjData.marrData[i] = ((EditorForm)mpnContent.getWidget(i)).GetData();
		}

		return lobjData;
	}

	public void EnableButtons()
	{
		mbEnabled = true;
		ResetButtons();
	}

	public boolean TryGoBack()
	{
		if ( mpnToolbar.isVisible() )
			return false;

		((SingleGrid)mpnContent.getWidget(mlngSelected)).TryGoBack();
		
		return true;
	}

	private void BuildEditor(EditorResponse prefResult, DataObject pobjData)
	{
		EditorForm lform;
		SingleGrid lgrid;
		int i;

		if ( prefResult.marrTabs.length == 0 )
			return;

		mlngInitCount = prefResult.marrTabs.length;

		for ( i = 0; i < prefResult.marrTabs.length; i++ )
		{
			if ( prefResult.marrTabs[i].mlngType == TabObj.FORMTAB )
			{
				lform = new EditorForm();
				mpnContent.add(lform);
				lform.setVisible(i == 0);
				lform.InitForm(prefResult.marrTabs[i].mstrID, mstrNameSpace, pobjData.marrData[i], i);
				mlbxSelector.addItem(prefResult.marrTabs[i].mstrCaption);
				lform.addInitHandler(new InitEvent.Handler()
				{
					public void onInit(InitEvent event)
					{
						mlngInitCount--;
						if ( mlngInitCount <= 0 )
						{
							mbInit = true;
							ResetButtons();
						}
					}
				});
				lform.addActionHandler(new ActionEvent.Handler()
				{
					public void onAction(ActionEvent event)
					{
						mrefEventMgr.fireEvent(event);
					}
				});
			}
			if ( prefResult.marrTabs[i].mlngType == TabObj.GRIDTAB )
			{
				lgrid = new SingleGrid(false);
				mpnContent.add(lgrid);
				lgrid.setVisible(i == 0);
				lgrid.InitGrid(null, prefResult.marrTabs[i].mstrID, mstrNameSpace, true, pobjData.mstrID, null, null, null);
				mlbxSelector.addItem(prefResult.marrTabs[i].mstrCaption);
				lgrid.addInitHandler(new InitEvent.Handler()
				{
					public void onInit(InitEvent event)
					{
						mlngInitCount--;
						if ( mlngInitCount <= 0 )
						{
							mbInit = true;
							ResetButtons();
						}
					}
				});
				lgrid.addSelectHandler(new SelectEvent.Handler()
				{
					public void onSelect(SelectEvent event)
					{
						mpnToolbar.setVisible(false);
					}
				});
				lgrid.addCancelHandler(new CancelEvent.Handler()
				{
					public void onCancel(CancelEvent event)
					{
						mpnToolbar.setVisible(true);
					}
				});
			}
		}

		mlngSelected = 0;

		if ( prefResult.marrTabs.length > 1 )
		{
			mlbxSelector.setSelectedIndex(0);
			mlbxSelector.setVisible(true);
		}
	}

	private void ResetButtons()
	{
		mbtnSave.setEnabled(mbEnabled && mbInit);
		mbtnCancel.setEnabled(mbEnabled && mbInit);
		mbtnDelete.setEnabled(mbEnabled && mbInit);
	}

	private void DoChangeView()
	{
		if ( mlbxSelector.getSelectedIndex() == mlngSelected )
			return;
		
		mpnContent.getWidget(mlngSelected).setVisible(false);
		mlngSelected = mlbxSelector.getSelectedIndex();
		mpnContent.getWidget(mlngSelected).setVisible(true);
	}

	public void DoClose()
	{
		int i;

		for ( i = 0; i < mpnContent.getWidgetCount(); i++ )
		{
			if ( mpnContent.getWidget(i) instanceof ClosableContent )
				((ClosableContent)mpnContent.getWidget(i)).DoClose();
		}
	}

	public HandlerRegistration addActionHandler(ActionEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(ActionEvent.TYPE, handler);
	}

	public HandlerRegistration addSaveHandler(SaveEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(SaveEvent.TYPE, handler);
	}

	public HandlerRegistration addDeleteRowHandler(DeleteEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(DeleteEvent.TYPE, handler);
	}
	

	public boolean hasActions()
	{
		Widget lwdgtAux;
		
		for (int  i = 0; i < mpnContent.getWidgetCount(); i++ )
		{
			lwdgtAux = mpnContent.getWidget(i);
			if (  lwdgtAux instanceof EditorForm ) {
				if ( ((EditorForm) lwdgtAux).hasActions() ) { return true; }
			}
		}
		return false;
	}
}
