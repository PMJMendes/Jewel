package Jewel.Web.client;

import Jewel.Web.client.events.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Style.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class DynaGrid
	extends Composite
	implements ClosableContent, InitEvent.HasEvent, SelectEvent.HasEvent, CancelEvent.HasEvent, JErrorEvent.HasEvent
{
	private DynaGridServiceAsync gridSvc;

	private String mstrWorkspace;
	private String mstrEditorID;
	private int[] marrSort;
	private int mlngSelected;
	private int[] marrRows;
	private String mstrNameSpace;
	private boolean mbAlternateColor;
	private boolean mbForPopup;
	private boolean mbNewRow;
	private DataObject mobjData;
	private boolean mbWithParam;
	private boolean mbReadOnly;
	private boolean mbCanCreate;
	private boolean mbCanEdit;
	private boolean mbCanDelete;

	private VerticalPanel mpnMain;
	private VerticalPanel mpnInner;
	private Grid mgrdTable;
	private Label mlblPage;
	private Button mbtnUnsort;
	private Button mbtnRefresh;
	private Button mbtnNew;
	private Button mbtnNext;
	private Button mbtnPrev;
	private Button mbtnFirst;
	private Button mbtnLast;
	private DynaView mdvEditor;
	private Button mbtnSelect;
	private Button mbtnEmpty;
	private Button mbtnDetails;
	private Button mbtnCancel;

	private HandlerManager mrefEventMgr;

	public DynaGrid()
	{
		HorizontalPanel lhorz;

		mbForPopup = false;
		mbNewRow = false;

		mpnMain = new VerticalPanel();
		mpnMain.setStylePrimaryName("jewel-DynaGrid");

		mpnInner = new VerticalPanel();
		mpnInner.setStylePrimaryName("jewel-DynaGrid-Main");
		mpnMain.add(mpnInner);

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("jewel-DynaGrid-Toolbar");
		mpnInner.add(lhorz);
		mbtnUnsort = new Button();
		mbtnUnsort.setText("Unsort");
		mbtnUnsort.setEnabled(false);
		lhorz.add(mbtnUnsort);
		mbtnUnsort.getElement().getParentElement().addClassName("jewel-DynaGrid-Unsort-Wrapper");
		mbtnRefresh = new Button();
		mbtnRefresh.setText("Refresh");
		mbtnRefresh.setEnabled(false);
		lhorz.add(mbtnRefresh);
		mbtnRefresh.getElement().getParentElement().addClassName("jewel-DynaGrid-Refresh-Wrapper");
		mbtnNew = new Button();
		mbtnNew.setText("New");
		lhorz.add(mbtnNew);
		mbtnNew.getElement().getParentElement().addClassName("jewel-DynaGrid-New-Wrapper");

		mgrdTable = new Grid();
		mgrdTable.setCellPadding(2);
		mgrdTable.setCellSpacing(0);
		mgrdTable.setStylePrimaryName("jewel-DynaGrid-Grid");
		mpnInner.add(mgrdTable);

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("jewel-DynaGrid-Pagebar");
		mpnInner.add(lhorz);
		mbtnFirst = new Button();
		mbtnFirst.setText("<< First");
		mbtnFirst.setEnabled(false);
		lhorz.add(mbtnFirst);
		mbtnFirst.getElement().getParentElement().setClassName("jewel-DynaGrid-First-Wrapper");
		mbtnPrev = new Button();
		mbtnPrev.setText("< Prev");
		mbtnPrev.setEnabled(false);
		lhorz.add(mbtnPrev);
		mbtnPrev.getElement().getParentElement().setClassName("jewel-DynaGrid-Prev-Wrapper");
		mlblPage = new Label();
		mlblPage.setStylePrimaryName("jewel-DynaGrid-PageText");
		lhorz.add(mlblPage);
		mlblPage.getElement().getParentElement().setClassName("jewel-DynaGrid-PageText-Wrapper");
		mbtnNext = new Button();
		mbtnNext.setText("Next >");
		mbtnNext.setEnabled(false);
		lhorz.add(mbtnNext);
		mbtnNext.getElement().getParentElement().setClassName("jewel-DynaGrid-Next-Wrapper");
		mbtnLast = new Button();
		mbtnLast.setText("Last >>");
		mbtnLast.setEnabled(false);
		lhorz.add(mbtnLast);
		mbtnLast.getElement().getParentElement().setClassName("jewel-DynaGrid-Last-Wrapper");

		mdvEditor = null;

		initWidget(mpnMain);

		mbtnUnsort.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				doUnsort();
	        }
	     });
		mbtnRefresh.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				doRefresh();
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

	public void SetForPopup()
	{
		FlowPanel lpn;
		HorizontalPanel lhorz;

		mbForPopup = true;

		lpn = new FlowPanel();
		lpn.setStyleName("jewel-DynaGrid-Resultsbar-Wrapper");
		mpnInner.add(lpn);

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("jewel-DynaGrid-Resultsbar");
		lpn.add(lhorz);
		mbtnSelect = new Button();
		mbtnSelect.setText("Select");
		mbtnSelect.setEnabled(false);
		lhorz.add(mbtnSelect);
		mbtnSelect.getElement().getParentElement().addClassName("jewel-DynaGrid-Select-Wrapper");
		mbtnEmpty = new Button();
		mbtnEmpty.setText("Select (empty)");
		lhorz.add(mbtnEmpty);
		mbtnEmpty.getElement().getParentElement().addClassName("jewel-DynaGrid-Empty-Wrapper");
		mbtnDetails = new Button();
		mbtnDetails.setText("Details...");
		mbtnDetails.setEnabled(false);
		lhorz.add(mbtnDetails);
		mbtnDetails.getElement().getParentElement().addClassName("jewel-DynaGrid-Details-Wrapper");
		mbtnCancel = new Button();
		mbtnCancel.setText("Cancel");
		lhorz.add(mbtnCancel);
		mbtnCancel.getElement().getParentElement().addClassName("jewel-DynaGrid-Cancel-Wrapper");

		mbtnSelect.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mrefEventMgr.fireEvent(new SelectEvent(mobjData.mstrID + "!" + mobjData.mstrDisplayName));
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
				if ( mobjData == null )
				{
					mbNewRow = true;
					DoSelect(-1);
				}
				else
					LoadEditor(mobjData, true);
	        }
	     });
		mbtnCancel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mrefEventMgr.fireEvent(new CancelEvent());
	        }
	     });
	}

	private DynaGridServiceAsync getService()
	{
		if ( gridSvc == null )
			gridSvc = GWT.create(DynaGridService.class);

		return gridSvc;
	}

	public void InitGrid(String pstrQueryID, String pstrNameSpace, boolean pbForceParam, String pstrParam,
			String pstrFormID, ParamInfo[] parrExtParams, String pstrInitialValue, boolean pbAlternate)
	{
		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
			        mstrWorkspace = result.mstrWorkspaceID;
			        mstrEditorID = result.mstrEditorID;
			        marrSort = new int[0];
					UnloadEditor();
			        mlngSelected = result.mlngCurrRow;
					RenderData(result);
					mbtnRefresh.setEnabled(true);
					if ( mbForPopup && (mlngSelected > -1) )
					{
						mbtnDetails.setEnabled(true);
						mbtnSelect.setEnabled(true);
					}
					mrefEventMgr.fireEvent(new InitEvent());
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

        mstrNameSpace = pstrNameSpace;
		mbAlternateColor = pbAlternate;
        mbWithParam = pbForceParam;
		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().OpenQuery(pstrQueryID, pstrNameSpace, pbForceParam, pstrParam, pstrFormID, parrExtParams, pstrInitialValue, callback);
	}

	public void ReloadAt(boolean pbForceParam, String pstrParam)
	{
		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
			        marrSort = new int[0];
			        UnloadEditor();
			        mlngSelected = -1;
					RenderData(result);
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

        mbWithParam = pbForceParam;
		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().ReloadAt(mstrWorkspace, pbForceParam, pstrParam, callback);
	}

	public void ApplySearch(String pstrFormID, String[] parrData)
	{
		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

        UnloadEditor();
		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().ApplySearch(mstrWorkspace, pstrFormID, parrData, callback);
	}

	private void doUnsort()
	{
		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

        marrSort = new int[0];
		mbtnUnsort.setEnabled(false);
		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().ApplySort(mstrWorkspace, marrSort, callback);
	}

	private void doRefresh()
	{
		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
			        UnloadEditor();
			        mlngSelected = -1;
					RenderData(result);
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

		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().ForceRefresh(mstrWorkspace, mbWithParam, callback);
	}

	private void doNew()
	{
		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
					mbNewRow = true;
					DoSelect(result.marrData.length);
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

		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().NewRow(mstrWorkspace, callback);
	}

	private void doNext()
	{
		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().PageForward(mstrWorkspace, callback);
	}

	private void doPrev()
	{
		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().PageBack(mstrWorkspace, callback);
	}

	private void doFirst()
	{
		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().PageFirst(mstrWorkspace, callback);
	}

	private void doLast()
	{
		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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

		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().PageLast(mstrWorkspace, callback);
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

		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
					RenderData(result);
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
		mbtnUnsort.setEnabled(marrSort.length > 0);

		mrefEventMgr.fireEvent(new JErrorEvent(null));
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
					if ( mbNewRow )
					{
						LoadEditor(result, mbForPopup);
						mbNewRow = false;
					}
					else
						LoadEditor(result, false);
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

        if ( plngRowIndex >= 0 )
        {
			for ( i = 0, b = true; i < marrRows.length; i++, b = !b )
			{
				if ( marrRows[i] == mlngSelected )
				{
					mgrdTable.getRowFormatter().setStyleName(i + 1,
							(b ? "jewel-DynaGrid-Grid-Row jewel-DynaGrid-Grid-OddRow" :
								 "jewel-DynaGrid-Grid-Row jewel-DynaGrid-Grid-EvenRow"));
					break;
				}
			}
			mlngSelected = marrRows[plngRowIndex - 1];
			mgrdTable.getRowFormatter().setStyleName(plngRowIndex, "jewel-DynaGrid-Grid-Row jewel-DynaGrid-Grid-Selected");
        }

		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().GetRow(mstrWorkspace, mlngSelected, callback);
	}

	private void RenderData(DynaGridResponse pobjResp)
	{
		int i, j;
		boolean b;
		Label lrefAux;

		mbReadOnly = pobjResp.mbReadOnly;
		mbCanCreate = pobjResp.mbCanCreate;
		mbCanEdit = pobjResp.mbCanEdit;
		mbCanDelete = pobjResp.mbCanDelete;

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
				lrefAux.setStylePrimaryName("jewel-DynaGrid-Grid-CellDiv");
				lrefAux.getElement().getStyle().setWidth(pobjResp.marrColumns[i].mlngWidth, Unit.PX);
				mgrdTable.setWidget(0, i, lrefAux);
				mgrdTable.getCellFormatter().setStylePrimaryName(0, i, "jewel-DynaGrid-Grid-HeaderCell");
				for ( j = 0; j <= pobjResp.marrData.length; j++ )
					mgrdTable.getCellFormatter().setVisible(j, i, true);
			}

			if ( mbForPopup )
				mbtnDetails.setVisible(!mbReadOnly);
			mbtnNew.setVisible(!mbReadOnly);
			mbtnNew.setEnabled(mbCanCreate);
		}

        marrRows = pobjResp.marrRows;

		for ( i = 0, b = true; i < pobjResp.marrData.length; i++, b = !b )
		{
			if ( marrRows[i] == mlngSelected )
				mgrdTable.getRowFormatter().setStyleName(i + 1, "jewel-DynaGrid-Grid-Row jewel-DynaGrid-Grid-Selected");
			else
				mgrdTable.getRowFormatter().setStyleName(i + 1,
						(b ? "jewel-DynaGrid-Grid-Row jewel-DynaGrid-Grid-OddRow" :
							 "jewel-DynaGrid-Grid-Row jewel-DynaGrid-Grid-EvenRow"));
			for ( j = 0; j < pobjResp.marrData[i].length; j ++ )
			{
				lrefAux = (Label)mgrdTable.getWidget(i + 1, j);
				if ( lrefAux == null )
				{
					lrefAux = new Label(pobjResp.marrData[i][j]);
					lrefAux.setStylePrimaryName("jewel-DynaGrid-Grid-CellDiv");
					lrefAux.getElement().getStyle().setProperty("width",
							((Label)mgrdTable.getWidget(0, j)).getElement().getStyle().getWidth());
					mgrdTable.setWidget(i + 1, j, lrefAux);
				}
				else
					lrefAux.setText(pobjResp.marrData[i][j]);
			}
		}

		mlblPage.setText(Integer.toString(pobjResp.mlngCurrPage * pobjResp.mlngPageSize + 1) +
				" to " + Integer.toString(pobjResp.mlngCurrPage * pobjResp.mlngPageSize + pobjResp.marrData.length) +
				" of " + Integer.toString(pobjResp.mlngRecCount));

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
						lrefAux.setStylePrimaryName("jewel-DynaGrid-Grid-CellDiv");
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
	
	private void LoadEditor(DataObject pobjData, boolean pbForceBuild)
	{
		if ( mbReadOnly )
			return;

		if ( mbForPopup )
		{
			mobjData = pobjData;
			mbtnSelect.setEnabled(true);
			mbtnDetails.setEnabled(true);
			if ( mdvEditor != null )
			{
				mdvEditor.LoadData(pobjData, mbNewRow);
				mbtnDetails.setEnabled(false);
			}
			else
				if ( pbForceBuild )
				{
					BuildEditor(pobjData);
					mbtnDetails.setEnabled(false);
				}
		}
		else
		{
			if ( mdvEditor == null )
				BuildEditor(pobjData);
			else
				mdvEditor.LoadData(pobjData, mbNewRow);
		}
	}

	private void DoAction(int plngOrder, int plngAction)
	{
		AsyncCallback<GridActionResponse> callback = new AsyncCallback<GridActionResponse>()
        {
			public void onSuccess(GridActionResponse result)
			{
				if (result != null)
				{
					if ( result.marrRow != null )
						RenderRow(result.mlngRow, result.marrRow);
					if ( result.mstrResult != null )
						mrefEventMgr.fireEvent(new JErrorEvent(result.mstrResult));
					if ( result.mobjData != null )
						LoadEditor(result.mobjData, false);
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

		mrefEventMgr.fireEvent(new JErrorEvent(null));
		getService().DoAction(mstrWorkspace, mlngSelected, plngOrder, plngAction, mdvEditor.GetData(), callback);
	}

	private void SaveData()
	{
		AsyncCallback<DynaGridSaveResponse> callback = new AsyncCallback<DynaGridSaveResponse>()
        {
			public void onSuccess(DynaGridSaveResponse result)
			{
				if (result != null)
				{
					RenderRow(result.mlngRow, result.marrRow);
					LoadEditor(result.mobjData, false);
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

				ResetEditor();
			}
        };

		mrefEventMgr.fireEvent(new JErrorEvent(null));
		getService().SaveRow(mstrWorkspace, mlngSelected, mdvEditor.GetData(), callback);
	}
	
	private void DeleteRow()
	{
		AsyncCallback<DynaGridResponse> callback = new AsyncCallback<DynaGridResponse>()
        {
			public void onSuccess(DynaGridResponse result)
			{
				if (result != null)
				{
					UnloadEditor();
			        mlngSelected = -1;
					RenderData(result);
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

				ResetEditor();
			}
        };

		mrefEventMgr.fireEvent(new JErrorEvent(null));
		getService().DeleteRow(mstrWorkspace, mlngSelected, callback);
	}

	private void BuildEditor(DataObject pobjData)
	{
		if ( mdvEditor != null )
			return;

		mdvEditor = new DynaView();
		mpnMain.add(mdvEditor);
		mdvEditor.getElement().getParentElement().addClassName("jewel-DynaGrid-Editor");
		mdvEditor.getElement().getParentElement().addClassName(mbAlternateColor ? "normalbk" : "alternate");
		mdvEditor.InitView(mstrEditorID, mstrNameSpace, pobjData, !mbAlternateColor, mbCanEdit, mbCanDelete, mbNewRow);

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
		mdvEditor.addJErrorHandler(new JErrorEvent.Handler()
		{
			public void onJError(JErrorEvent event)
			{
				mrefEventMgr.fireEvent(event);
			}
		});
	}

	private void UnloadEditor()
	{
		if ( mbForPopup )
		{
			mobjData = null;
			mbtnSelect.setEnabled(false);
			mbtnDetails.setEnabled(false);
		}

		if ( mdvEditor == null )
			return;

		mdvEditor.DoClose();
		mpnMain.remove(mdvEditor);
		mdvEditor = null;
	}

	private void ResetEditor()
	{
		if ( mdvEditor != null )
			mdvEditor.ResetButtons();
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

        UnloadEditor();
		mrefEventMgr.fireEvent(new JErrorEvent(null));
		if ( mstrWorkspace != null )
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

	public HandlerRegistration addCancelHandler(CancelEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(CancelEvent.TYPE, handler);
	}

	public HandlerRegistration addJErrorHandler(JErrorEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(JErrorEvent.TYPE, handler);
	}
}
