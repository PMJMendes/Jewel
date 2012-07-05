package Jewel.Mobile.client;

import Jewel.Mobile.client.events.DeleteEvent;
import Jewel.Mobile.client.events.InitEvent;
import Jewel.Mobile.client.events.OkEvent;
import Jewel.Mobile.client.events.SaveEvent;
import Jewel.Mobile.client.events.SelectEvent;
import Jewel.Mobile.interfaces.GridService;
import Jewel.Mobile.interfaces.GridServiceAsync;
import Jewel.Mobile.shared.DataObject;
import Jewel.Mobile.shared.GridActionResponse;
import Jewel.Mobile.shared.GridResponse;
import Jewel.Mobile.shared.GridSaveResponse;
import Jewel.Mobile.shared.ParamInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SimpleGrid
	extends Composite
	implements ClosableContent, InitEvent.HasEvent, SelectEvent.HasEvent, OkEvent.HasEvent, SaveEvent.HasEvent, DeleteEvent.HasEvent
{
	private GridServiceAsync gridSvc;

	private String mstrWorkspace;
	private String mstrEditorID;
	private int[] marrSort;
	private int mlngSelected;
	private int[] marrRows;
	private boolean mbInit;

	private VerticalPanel mpnMain;
	private Grid mgrdTable;
	private Button mbtnSearch;
	private Button mbtnNew;
	private Button mbtnFirst;
	private Button mbtnPrev;
	private Label mlblPage;
	private Button mbtnNext;
	private Button mbtnLast;

	private HandlerManager mrefEventMgr;

	public SimpleGrid()
	{
		HorizontalPanel lhorz;

		mbInit = false;

		mpnMain = new VerticalPanel();
		mpnMain.setStylePrimaryName("simpleGrid");

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("simpleGrid-Toolbar");
		mpnMain.add(lhorz);
		lhorz.getElement().getParentElement().setClassName("simpleGrid-Toolbar-Wrapper");
		mbtnSearch = new Button();
		mbtnSearch.setText("Search");
		mbtnSearch.setStylePrimaryName("simpleGrid-SearchButton");
		mbtnSearch.setEnabled(false);
		lhorz.add(mbtnSearch);
		mbtnSearch.getElement().getParentElement().setClassName("simpleGrid-SearchButton-Wrapper");
		mbtnNew = new Button();
		mbtnNew.setText("New");
		mbtnNew.setStylePrimaryName("simpleGrid-NewButton");
		mbtnNew.setEnabled(false);
		lhorz.add(mbtnNew);
		mbtnNew.getElement().getParentElement().setClassName("simpleGrid-NewButton-Wrapper");

		lhorz = new HorizontalPanel();
		lhorz.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		lhorz.setStylePrimaryName("simpleGrid-Pagebar");
		mpnMain.add(lhorz);
		lhorz.getElement().getParentElement().setClassName("simpleGrid-Pagebar-Wrapper");
		mbtnFirst = new Button();
		mbtnFirst.setText("<< First");
		mbtnFirst.setEnabled(false);
		mbtnFirst.setStylePrimaryName("simpleGrid-PageFirstButton");
		lhorz.add(mbtnFirst);
		mbtnFirst.getElement().getParentElement().setClassName("simpleGrid-PageFirstButton-Wrapper");
		mbtnPrev = new Button();
		mbtnPrev.setText("< Prev");
		mbtnPrev.setEnabled(false);
		mbtnPrev.setStylePrimaryName("simpleGrid-PagePrevButton");
		lhorz.add(mbtnPrev);
		mbtnPrev.getElement().getParentElement().setClassName("simpleGrid-PagePrevButton-Wrapper");
		mlblPage = new Label();
		mlblPage.setStylePrimaryName("simpleGrid-PageText");
		lhorz.add(mlblPage);
		mlblPage.getElement().getParentElement().setClassName("simpleGrid-PageText-Wrapper");
		mbtnNext = new Button();
		mbtnNext.setText("Next >");
		mbtnNext.setEnabled(false);
		mbtnNext.setStylePrimaryName("simpleGrid-PageNextButton");
		lhorz.add(mbtnNext);
		mbtnNext.getElement().getParentElement().setClassName("simpleGrid-PageNextButton-Wrapper");
		mbtnLast = new Button();
		mbtnLast.setText("Last >>");
		mbtnLast.setEnabled(false);
		mbtnLast.setStylePrimaryName("simpleGrid-PageLastButton");
		lhorz.add(mbtnLast);
		mbtnLast.getElement().getParentElement().setClassName("simpleGrid-PageLastButton-Wrapper");

		mgrdTable = new Grid();
		mgrdTable.setCellPadding(2);
		mgrdTable.setCellSpacing(0);
		mgrdTable.setStylePrimaryName("simpleGrid-Grid");
		mpnMain.add(mgrdTable);
		mgrdTable.getElement().getParentElement().setClassName("simpleGrid-Grid-Wrapper");

		initWidget(mpnMain);

		mbtnSearch.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				mrefEventMgr.fireEvent(new OkEvent());
			}
		});
		mbtnNew.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				doNew();
	        }
	     });
		mbtnNext.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				doNext();
	        }
	     });
		mbtnPrev.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				doPrev();
	        }
	     });
		mbtnFirst.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				doFirst();
	        }
	     });
		mbtnLast.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				doLast();
	        }
	     });
		mgrdTable.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				doTableClick(event);
			}
		});

		mrefEventMgr = new HandlerManager(this);
	}

	private GridServiceAsync getService()
	{
		if ( gridSvc == null )
			gridSvc = GWT.create(GridService.class);

		return gridSvc;
	}

	public void InitGrid(String pstrQueryID, String pstrNameSpace, boolean pbForceParam,
			String pstrParam, String pstrFormID, ParamInfo[] parrExtParams, String pstrInitialValue)
	{
		AsyncCallback<GridResponse> callback = new AsyncCallback<GridResponse>()
        {
			public void onSuccess(GridResponse result)
			{
				if (result != null)
				{
			        mstrWorkspace = result.mstrWorkspaceID;
			        mstrEditorID = result.mstrEditorID;
			        marrSort = new int[0];
			        mlngSelected = result.mlngCurrRow;
					RenderData(result);
					mbtnSearch.setEnabled(true);
					mbInit = true;
					mrefEventMgr.fireEvent(new InitEvent());
					if ( mlngSelected >= 0 )
						DoSelect(-1);
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

        getService().OpenQuery(pstrQueryID, pstrNameSpace, pbForceParam, pstrParam, pstrFormID, parrExtParams,
        		pstrInitialValue, callback);
	}

	public void ReloadAt(boolean pbForceParam, String pstrParam)
	{
		AsyncCallback<GridResponse> callback = new AsyncCallback<GridResponse>()
        {
			public void onSuccess(GridResponse result)
			{
				if (result != null)
				{
			        marrSort = new int[0];
			        mlngSelected = -1;
					RenderData(result);
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

        if ( !mbInit )
        	return;

        getService().ReloadAt(mstrWorkspace, pbForceParam, pstrParam, callback);
	}

	public void ApplySearch(String pstrFormID, String[] parrData)
	{
		AsyncCallback<GridResponse> callback = new AsyncCallback<GridResponse>()
        {
			public void onSuccess(GridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

        if ( !mbInit )
        	return;

        getService().ApplySearch(mstrWorkspace, pstrFormID, parrData, callback);
	}

	public void SaveData(DataObject pobjData)
	{
		AsyncCallback<GridSaveResponse> callback = new AsyncCallback<GridSaveResponse>()
		{
			public void onSuccess(GridSaveResponse result)
			{
				if (result != null)
				{
					RenderRow(result.mlngRow, result.marrRow);
					mrefEventMgr.fireEvent(new SaveEvent());
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

		if ( !mbInit )
			return;

		getService().SaveRow(mstrWorkspace, mlngSelected, pobjData, callback);
	}

	public void DeleteRow()
	{
		AsyncCallback<GridResponse> callback = new AsyncCallback<GridResponse>()
		{
			public void onSuccess(GridResponse result)
			{
				if (result != null)
				{
			        mlngSelected = -1;
					RenderData(result);
					mrefEventMgr.fireEvent(new DeleteEvent());
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

		if ( !mbInit )
			return;

		getService().DeleteRow(mstrWorkspace, mlngSelected, callback);
	}

	public void DoAction(int plngOrder, int plngAction, DataObject pobjData)
	{
		AsyncCallback<GridActionResponse> callback = new AsyncCallback<GridActionResponse>()
		{
			public void onSuccess(GridActionResponse result)
			{
				if (result != null)
				{
					if ( !"".equals(result.mstrResult) )
						Jewel_Mobile.getReference().showError(result.mstrResult);
					if ( result.mobjData != null )
						mrefEventMgr.fireEvent(new SelectEvent(result.mobjData));
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

		getService().DoAction(mstrWorkspace, mlngSelected, plngOrder, plngAction, pobjData, callback);
	}
	
	public String GetEditorID()
	{
		return mstrEditorID;
	}
	
	private void doNext()
	{
		AsyncCallback<GridResponse> callback = new AsyncCallback<GridResponse>()
        {
			public void onSuccess(GridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

        getService().PageForward(mstrWorkspace, callback);
	}

	private void doPrev()
	{
		AsyncCallback<GridResponse> callback = new AsyncCallback<GridResponse>()
        {
			public void onSuccess(GridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

        getService().PageBack(mstrWorkspace, callback);
	}

	private void doFirst()
	{
		AsyncCallback<GridResponse> callback = new AsyncCallback<GridResponse>()
        {
			public void onSuccess(GridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

        getService().PageFirst(mstrWorkspace, callback);
	}

	private void doLast()
	{
		AsyncCallback<GridResponse> callback = new AsyncCallback<GridResponse>()
        {
			public void onSuccess(GridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

        getService().PageLast(mstrWorkspace, callback);
	}

	private void doNew()
	{
		AsyncCallback<GridResponse> callback = new AsyncCallback<GridResponse>()
        {
			public void onSuccess(GridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
					DoSelect(result.marrData.length);
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

        getService().NewRow(mstrWorkspace, callback);
	}

	private void doTableClick(ClickEvent event)
	{
		HTMLTable.Cell lrefCell;

		lrefCell = mgrdTable.getCellForEvent(event);

		if ( lrefCell.getRowIndex() == 0 )
			DoSort(lrefCell.getCellIndex() + 1);
		else
			DoSelect(lrefCell.getRowIndex());
	}

	private void DoSort(int plngSortIndex)
	{
		int[] larrAux;
		int i, j;
		boolean lbFound;

		AsyncCallback<GridResponse> callback = new AsyncCallback<GridResponse>()
        {
			public void onSuccess(GridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

		lbFound = false;
		for ( i = 0; i < marrSort.length; i++ )
		{
			if ( marrSort[i] == -plngSortIndex )
			{
				larrAux = new int[marrSort.length - 1];
				for ( j = 0; j < i; j++ )
					larrAux[j] = marrSort[j];
				for ( j = i + 1; j < marrSort.length; j++ )
					larrAux[j - 1] = marrSort[j];
				marrSort = larrAux;
				lbFound = true;
				break;
			}
			if ( marrSort[i] == plngSortIndex )
			{
				larrAux = new int[marrSort.length];
				for ( j = 0; j < i; j++ )
					larrAux[j] = marrSort[j];
				for ( j = i + 1; j < marrSort.length; j++ )
					larrAux[j - 1] = marrSort[j];
				larrAux[marrSort.length - 1] = -plngSortIndex;
				marrSort = larrAux;
				lbFound = true;
				break;
			}
		}
		if ( !lbFound )
		{
			larrAux = new int[marrSort.length + 1];
			for ( j = 0; j < marrSort.length; j++ )
				larrAux[j] = marrSort[j];
			larrAux[marrSort.length] = plngSortIndex;
			marrSort = larrAux;
		}

        getService().ApplySort(mstrWorkspace, marrSort, callback);
	}

	private void DoSelect(int plngRowIndex)
	{
		int i;
		boolean b;

		AsyncCallback<DataObject> callback = new AsyncCallback<DataObject>()
		{
			public void onSuccess(DataObject result)
			{
				if (result != null)
				{
					mrefEventMgr.fireEvent(new SelectEvent(result));
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

		if ( plngRowIndex >= 0 )
		{
			for ( i = 0, b = true; i < marrRows.length; i++, b = !b )
			{
				if ( marrRows[i] == mlngSelected )
				{
					mgrdTable.getRowFormatter().setStyleName(i + 1,
							(b ? "simpleGrid-Grid-Row simpleGrid-Grid-OddRow" :
								 "simpleGrid-Grid-Row simpleGrid-Grid-EvenRow"));
					break;
				}
			}
			mlngSelected = marrRows[plngRowIndex - 1];
			mgrdTable.getRowFormatter().setStyleName(plngRowIndex, "simpleGrid-Grid-Row simpleGrid-Grid-Selected");
		}
		getService().GetRow(mstrWorkspace, mlngSelected, callback);
	}

	private void RenderData(GridResponse pobjResp)
	{
		int i, j;
		boolean b;
		Label lrefAux;

		mgrdTable.resizeRows(pobjResp.marrData.length + 1);
		if ( pobjResp.marrColumns != null )
		{
			mgrdTable.clear(true);

			if (pobjResp.marrColumns.length > mgrdTable.getColumnCount())
				mgrdTable.resizeColumns(pobjResp.marrColumns.length);
			else
				for ( i = pobjResp.marrColumns.length; i < mgrdTable.getColumnCount(); i++ )
					for ( j = 0; j <= pobjResp.marrData.length; j++ )
						mgrdTable.getCellFormatter().setVisible(j, i, false);

			for ( i = 0; i < pobjResp.marrColumns.length; i++ )
			{
				lrefAux = new Label(pobjResp.marrColumns[i].mstrName);
				lrefAux.setStylePrimaryName("simpleGrid-Grid-CellDiv");
				lrefAux.getElement().getStyle().setWidth(pobjResp.marrColumns[i].mlngWidth * 1.5, Unit.PX);
				mgrdTable.setWidget(0, i, lrefAux);
				mgrdTable.getCellFormatter().setStylePrimaryName(0, i, "simpleGrid-Grid-HeaderCell");
				for ( j = 0; j <= pobjResp.marrData.length; j++ )
					mgrdTable.getCellFormatter().setVisible(j, i, true);
			}

			mbtnNew.setEnabled(pobjResp.mbCanCreate);
		}

        marrRows = pobjResp.marrRows;

		for ( i = 0, b = true; i < pobjResp.marrData.length; i++, b = !b )
		{
			if ( marrRows[i] == mlngSelected )
				mgrdTable.getRowFormatter().setStyleName(i + 1, "simpleGrid-Grid-Row simpleGrid-Grid-Selected");
			else
				mgrdTable.getRowFormatter().setStyleName(i + 1,
						(b ? "simpleGrid-Grid-Row simpleGrid-Grid-OddRow" :
							 "simpleGrid-Grid-Row simpleGrid-Grid-EvenRow"));
			for ( j = 0; j < pobjResp.marrData[i].length; j ++ )
			{
				lrefAux = (Label)mgrdTable.getWidget(i + 1, j);
				if ( lrefAux == null )
				{
					lrefAux = new Label(pobjResp.marrData[i][j]);
					lrefAux.setStylePrimaryName("simpleGrid-Grid-CellDiv");
					lrefAux.getElement().getStyle().setProperty("width",
							((Label)mgrdTable.getWidget(0, j)).getElement().getStyle().getWidth());
					mgrdTable.setWidget(i + 1, j, lrefAux);
				}
				else
					lrefAux.setText(pobjResp.marrData[i][j]);
			}
		}

		if ( pobjResp.mlngPageSize > 0 )
			mlblPage.setText(Integer.toString(pobjResp.mlngCurrPage * pobjResp.mlngPageSize + 1) +
					" to " + Integer.toString(pobjResp.mlngCurrPage * pobjResp.mlngPageSize + pobjResp.marrData.length) +
					" of " + Integer.toString(pobjResp.mlngRecCount));
		else
			mlblPage.setText("All 1 to " + Integer.toString(pobjResp.mlngRecCount));

		if ( (pobjResp.mlngCurrPage + 1) >= pobjResp.mlngPageCount )
		{
			mbtnNext.setEnabled(false);
			mbtnLast.setEnabled(false);
		}
		else
		{
			mbtnNext.setEnabled(true);
			mbtnLast.setEnabled(true);
		}
		if ( pobjResp.mlngCurrPage <= 0 )
		{
			mbtnPrev.setEnabled(false);
			mbtnFirst.setEnabled(false);
		}
		else
		{
			mbtnPrev.setEnabled(true);
			mbtnFirst.setEnabled(true);
		}
	}
	
	private void RenderRow(int plngRow, String[] parrValues)
	{
		int i, j;
		Label lrefAux;

		for ( i = 0; i < marrRows.length; i++ )
		{
			if ( marrRows[i] == plngRow )
			{
				for ( j = 0; j < parrValues.length; j ++ )
				{
					lrefAux = (Label)mgrdTable.getWidget(i + 1, j);
					if ( lrefAux == null )
					{
						lrefAux = new Label(parrValues[j]);
						lrefAux.setStylePrimaryName("simpleGrid-Grid-CellDiv");
						lrefAux.getElement().getStyle().setProperty("width",
								((Label)mgrdTable.getWidget(0, j)).getElement().getStyle().getWidth());
						mgrdTable.setWidget(i + 1, j, lrefAux);
					}
					else
						lrefAux.setText(parrValues[j]);
				}
			}
		}
	}

	public void DoClose()
	{
		AsyncCallback<String> callback = new AsyncCallback<String>()
		{
			public void onSuccess(String result)
			{
				if (result != null)
				{
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

		getService().CloseQuery(mstrWorkspace, callback);
	}

	public HandlerRegistration addInitHandler(InitEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(InitEvent.TYPE, handler);
	}

	public HandlerRegistration addSelectHandler(SelectEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(SelectEvent.TYPE, handler);
	}

	public HandlerRegistration addOkHandler(OkEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(OkEvent.TYPE, handler);
	}

	public HandlerRegistration addSaveHandler(SaveEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(SaveEvent.TYPE, handler);
	}

	public HandlerRegistration addDeleteRowHandler(DeleteEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(DeleteEvent.TYPE, handler);
	}
}
