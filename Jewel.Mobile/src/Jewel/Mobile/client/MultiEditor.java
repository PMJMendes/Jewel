package Jewel.Mobile.client;

import Jewel.Mobile.client.events.*;
import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class MultiEditor
	extends Composite
	implements ClosableContent, ActionEvent.HasEvent, SaveEvent.HasEvent, DeleteEvent.HasEvent
{
	private MultiEditorServiceAsync editorSvc;

	private String mstrViewID;
	private String mstrNameSpace;
	private DataObject mobjDataCache;
	private int mlngSelected;

	HorizontalPanel mpnToolbar;
	private ListBox mlbxSelector;
    private Button mbtnEdit;
    private Button mbtnSave;
    private Button mbtnCancel;
    private Button mbtnDelete;
	HorizontalPanel mpnContent;

	private HandlerManager mrefEventMgr;

	public MultiEditor()
	{
		VerticalPanel louter;

		louter = new VerticalPanel();
		louter.setStylePrimaryName("multiEditor");

		mpnToolbar = new HorizontalPanel();
		mpnToolbar.setStylePrimaryName("multiEditor-Toolbar");
		louter.add(mpnToolbar);
		mpnToolbar.getElement().getParentElement().setClassName("multiEditor-Toolbar-Wrapper");

		mbtnEdit = new Button("Edit");
		mbtnEdit.setStylePrimaryName("multiEditor-Edit");
		mpnToolbar.add(mbtnEdit);
		mbtnEdit.getElement().getParentElement().setClassName("multiEditor-Edit-Wrapper");
		mbtnSave = new Button("Save");
		mbtnSave.setStylePrimaryName("multiEditor-Save");
		mbtnSave.setEnabled(false);
		mpnToolbar.add(mbtnSave);
		mbtnSave.getElement().getParentElement().setClassName("multiEditor-Save-Wrapper");
		mbtnCancel = new Button("Cancel");
		mbtnCancel.setEnabled(false);
		mbtnCancel.setStylePrimaryName("multiEditor-Cancel");
		mpnToolbar.add(mbtnCancel);
		mbtnCancel.getElement().getParentElement().setClassName("multiEditor-Cancel-Wrapper");
		mbtnDelete = new Button("Delete");
		mbtnDelete.setStylePrimaryName("multiEditor-Delete");
		mbtnDelete.setEnabled(false);
		mpnToolbar.add(mbtnDelete);
		mbtnDelete.getElement().getParentElement().setClassName("multiEditor-Delete-Wrapper");

		mlbxSelector = new ListBox();
		mlbxSelector.setStylePrimaryName("multiEditor-Selector");
		louter.add(mlbxSelector);
		mlbxSelector.getElement().getParentElement().setClassName("multiEditor-SelectorWrapper");
		mlbxSelector.setVisible(false);

		mpnContent = new HorizontalPanel();
		mpnContent.setStylePrimaryName("multiEditor-Content");
		louter.add(mpnContent);
		mpnContent.getElement().getParentElement().setClassName("multiEditor-Content-Wrapper");

		initWidget(louter);
		
		mlbxSelector.addChangeHandler(new ChangeHandler()
		{
			public void onChange(ChangeEvent event)
			{
				DoChangeView();
			}
		});
		mbtnEdit.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mbtnEdit.setEnabled(false);
				mbtnSave.setEnabled(true);
				mbtnCancel.setEnabled(true);
				mbtnDelete.setEnabled(true);
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
				mbtnSave.setEnabled(false);
				mbtnCancel.setEnabled(false);
				mbtnDelete.setEnabled(false);
				LoadData(mobjDataCache);
				mbtnEdit.setEnabled(true);
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

	public void ResetButtons()
	{
		mbtnEdit.setEnabled(true);
		mbtnSave.setEnabled(false);
		mbtnCancel.setEnabled(false);
		mbtnDelete.setEnabled(false);
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

		for ( i = 0; i < prefResult.marrTabs.length; i++ )
		{
			if ( prefResult.marrTabs[i].mlngType == TabObj.FORMTAB )
			{
				lform = new EditorForm();
				mpnContent.add(lform);
				lform.setVisible(i == 0);
				lform.InitForm(prefResult.marrTabs[i].mstrID, mstrNameSpace, pobjData.marrData[i], i);
				mlbxSelector.addItem(prefResult.marrTabs[i].mstrCaption);
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
			mlbxSelector.setVisible(true);
			mlbxSelector.setSelectedIndex(0);
		}
	}
	
	private void DoChangeView()
	{
		if ( mlbxSelector.getSelectedIndex() == mlngSelected )
			return;
		
		mpnContent.getWidget(mlngSelected).setVisible(false);
		mlngSelected = mlbxSelector.getSelectedIndex();
		mpnContent.getWidget(mlngSelected).setVisible(true);
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

	public HandlerRegistration addSaveRowHandler(SaveEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(SaveEvent.TYPE, handler);
	}

	public HandlerRegistration addDeleteRowHandler(DeleteEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(DeleteEvent.TYPE, handler);
	}
}
