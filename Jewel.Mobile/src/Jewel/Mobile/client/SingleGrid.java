package Jewel.Mobile.client;

import Jewel.Mobile.client.events.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.ui.*;

public class SingleGrid
	extends Composite
	implements ClosableContent, InitEvent.HasEvent, SelectEvent.HasEvent, CancelEvent.HasEvent
{
	private boolean mbForPopup;
	private String mstrSearchFormID;
	private String mstrNameSpace;
	private boolean mbSearchInit;

	private VerticalPanel mpnMain;
	private SearchForm mfrmSearch;
	private SimpleGrid mgrdTable;
	private MultiEditor mdvEditor;

	private HandlerManager mrefEventMgr;

	public SingleGrid(boolean pbForPopup)
	{
		mbForPopup = pbForPopup;

		mpnMain = new VerticalPanel();
		mpnMain.setStylePrimaryName("singleGrid");

		mfrmSearch = new SearchForm();
		mpnMain.add(mfrmSearch);
		mfrmSearch.getElement().getParentElement().setClassName("singleGird-SearchForm-Wrapper");
		mfrmSearch.setVisible(false);
		mbSearchInit = false;

		mgrdTable = new SimpleGrid();
		mpnMain.add(mgrdTable);
		mgrdTable.getElement().getParentElement().setClassName("singleGrid-Grid-Wrapper");

		mdvEditor = null;

		initWidget(mpnMain);

		mfrmSearch.addOkHandler(new OkEvent.Handler()
		{
			public void onOk(OkEvent event)
			{
				doApplySearch();
			}
		});
		mgrdTable.addInitHandler(new InitEvent.Handler()
		{
			public void onInit(InitEvent event)
			{
				mrefEventMgr.fireEvent(event);
			}
		});
		mgrdTable.addOkHandler(new OkEvent.Handler()
		{
			public void onOk(OkEvent event)
			{
				doSearch();
			}
		});
		mgrdTable.addSelectHandler(new SelectEvent.Handler()
		{
			public void onSelect(SelectEvent event)
			{
				if ( !mbForPopup )
					LoadEditor(event.getResult());
				mrefEventMgr.fireEvent(event);
			}
		});

		mrefEventMgr = new HandlerManager(this);
	}

	public void InitGrid(String pstrSearchFormID, String pstrQueryID, String pstrNameSpace, boolean pbForceParam,
			String pstrParam, String pstrFormID, ParamInfo[] parrExtParams, String pstrInitialValue)
	{
        mstrSearchFormID = pstrSearchFormID;
        mstrNameSpace = pstrNameSpace;
        mgrdTable.InitGrid(pstrQueryID, pstrNameSpace, pbForceParam, pstrParam, pstrFormID, parrExtParams, pstrInitialValue);
	}

	public void ReloadAt(boolean pbForceParam, String pstrParam)
	{
		mgrdTable.ReloadAt(pbForceParam, pstrParam);
	}

	public void LoadEditor(DataObject pobjData)
	{
		if ( mdvEditor == null )
			BuildEditor(pobjData);
		else
			mdvEditor.LoadData(pobjData);

		mgrdTable.setVisible(false);
		mdvEditor.setVisible(true);
	}

	public int GetSelected()
	{
		return mgrdTable.GetSelected();
	}

	public boolean TryGoBack()
	{
		if ( mfrmSearch.isVisible() )
		{
			doCancelSearch();
			return true;
		}

		if ( (mdvEditor != null) && mdvEditor.isVisible() )
		{
			if ( !mdvEditor.TryGoBack() )
				HideEditor();

			return true;
		}

		return false;
	}

	private void doSearch()
	{
		if ( !mbSearchInit )
		{
			mfrmSearch.InitSearch(mstrSearchFormID, mstrNameSpace);
			mbSearchInit = true;
		}
		mfrmSearch.setVisible(true);
		mgrdTable.setVisible(false);
	}

	private void doApplySearch()
	{
		mgrdTable.ApplySearch(mstrSearchFormID, mfrmSearch.GetData());
		doCancelSearch();
	}

	private void doCancelSearch()
	{
		mfrmSearch.setVisible(false);
		mgrdTable.setVisible(true);
	}
	
	private void HideEditor()
	{
		mdvEditor.setVisible(false);
		mgrdTable.setVisible(true);
		mrefEventMgr.fireEvent(new CancelEvent());
	}

	private void DoAction(int plngOrder, int plngAction)
	{
		mgrdTable.DoAction(plngOrder, plngAction, mdvEditor.GetData());
	}

	private void SaveData()
	{
		mgrdTable.SaveData(mdvEditor.GetData());
		HideEditor();
	}

	private void DeleteRow()
	{
		mgrdTable.DeleteRow();
		HideEditor();
	}

	private void BuildEditor(DataObject pobjData)
	{
		if ( mdvEditor != null )
			return;

		mdvEditor = new MultiEditor();
		mpnMain.add(mdvEditor);
		mdvEditor.getElement().getParentElement().setClassName("singleGrid-Editor-Wrapper");
		mdvEditor.InitEditor(mgrdTable.GetEditorID(), mstrNameSpace, pobjData);

		mdvEditor.addActionHandler(new ActionEvent.Handler()
		{
			public void onAction(ActionEvent event)
			{
				DoAction(event.GetOrder(), event.GetAction());
			}
		});
		mdvEditor.addSaveRowHandler(new SaveEvent.Handler()
		{
			public void onSave(SaveEvent event)
			{
				SaveData();
			}
		});
		mdvEditor.addDeleteRowHandler(new DeleteEvent.Handler()
		{
			public void onDelete(DeleteEvent event)
			{
				DeleteRow();
			}
		});
	}

	private void UnloadEditor()
	{
		mgrdTable.setVisible(true);

		if ( mdvEditor == null )
			return;

		mdvEditor.DoClose();
		mpnMain.remove(mdvEditor);
		mdvEditor = null;
	}

	public void DoClose()
	{
		UnloadEditor();
		mfrmSearch.DoClose();
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
