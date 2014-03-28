package Jewel.Web.client;

import Jewel.Web.client.controls.DateCtl;
import Jewel.Web.client.controls.DecBox;
import Jewel.Web.client.controls.FileCtl;
import Jewel.Web.client.controls.IntBox;
import Jewel.Web.client.controls.LabelBox;
import Jewel.Web.client.controls.DropdownList;
import Jewel.Web.client.controls.Lookup;
import Jewel.Web.client.controls.PasswordBox;
import Jewel.Web.client.controls.StringBox;
import Jewel.Web.client.controls.TriStateCheckboxCtl;
import Jewel.Web.client.controls.ValueLookup;
import Jewel.Web.client.events.InitEvent;
import Jewel.Web.client.events.JErrorEvent;
import Jewel.Web.interfaces.DynaFormService;
import Jewel.Web.interfaces.DynaFormServiceAsync;
import Jewel.Web.shared.FormCtlObj;
import Jewel.Web.shared.ParamInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DynaForm
	extends Composite
	implements ClosableContent, InitEvent.HasEvent, JErrorEvent.HasEvent
{
	private DynaFormServiceAsync formSvc;

	protected String mstrFormID;
	protected String mstrNameSpace;
	private String[] marrTmpData;
	private ParamInfo[] marrExtParams;
	private String[] marrDefaults;
	private boolean mbUseDefaults;

	protected VerticalPanel mpnOuter;
	private FlexTable mtblForm;
	private IJewelWebCtl[] marrControls;

	protected HandlerManager mrefEventMgr;

	public DynaForm()
	{
		mpnOuter = new VerticalPanel();
		mpnOuter.setStylePrimaryName("jewel-DynaForm");

		mtblForm = new FlexTable();
		mtblForm.setCellSpacing(5);
		mtblForm.setCellPadding(0);
		mtblForm.setStylePrimaryName("jewel-DynaForm-Form");
		mpnOuter.add(mtblForm);

		initWidget(mpnOuter);

		mrefEventMgr = new HandlerManager(this);
	}

	public void InitForm(String pstrFormID, String pstrNameSpace, String[] parrData, boolean pbUseDefaults)
	{
		AsyncCallback<FormCtlObj[]> callback = new AsyncCallback<FormCtlObj[]>()
        {
			public void onSuccess(FormCtlObj[] result)
			{
				if (result != null)
				{
					BuildForm(result);
					if ( marrTmpData != null )
					{
						SetData(marrTmpData, mbUseDefaults);
						marrTmpData = null;
					}
					else if ( mbUseDefaults )
						SetData(new String[marrControls.length], mbUseDefaults);
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

		mstrFormID = pstrFormID;
		mstrNameSpace = pstrNameSpace;
		marrTmpData = parrData;
		mrefEventMgr.fireEvent(new JErrorEvent(null));
		mbUseDefaults = pbUseDefaults;
        getService().GetControls(mstrFormID, mstrNameSpace, callback);
	}

	public String[] GetData()
	{
		String[] marrAux;
		int i;

		marrAux = new String[marrControls.length];

		for ( i = 0; i < marrControls.length; i++ )
			marrAux[i] = marrControls[i].getJValue();

		return marrAux;
	}

	public void SetData(String[] parrData)
	{
		SetData(parrData, mbUseDefaults);
	}

	public void SetData(String[] parrData, boolean pbUseDefaults)
	{
		int i;

		mbUseDefaults = pbUseDefaults;

		for ( i = 0; i < marrControls.length; i++ )
			marrControls[i].setJValue(((mbUseDefaults && (parrData[i] == null)) ? marrDefaults[i] : parrData[i]));
	}

	public ParamInfo[] GetExternalParams()
	{
		int i;

		if ( marrExtParams == null )
			return null;

		for ( i = 0; i < marrExtParams.length; i++ )
			marrExtParams[i].mstrValue = marrControls[marrExtParams[i].mlngIndex].getJValue();

		return marrExtParams;
	}

	public boolean HasDefaults()
	{
		int i;

		for ( i = 0; i < marrDefaults.length; i++ )
			if ( marrDefaults[i] != null )
				return true;

		return false;
	}
	
	private DynaFormServiceAsync getService()
	{
		if ( formSvc == null )
			formSvc = GWT.create(DynaFormService.class);

		return formSvc;
	}

	private void BuildForm(FormCtlObj[] parrCtls)
	{
		int llngTblCols, llngParamCount;
		int i, r, c, x, p;
		Label llblAux;
		Widget lctlAux;

		mtblForm.clear(true);
		mtblForm.removeAllRows();
		mtblForm.insertRow(0);

		marrControls = new IJewelWebCtl[parrCtls.length];
		marrDefaults = new String[parrCtls.length];

		llngTblCols = 0;
		llngParamCount = 0;
		for ( i = 0; i < parrCtls.length; i++ )
		{
			if ( (parrCtls[i].mlngColumn + parrCtls[i].mlngColSpan + 1) > llngTblCols )
				llngTblCols = parrCtls[i].mlngColumn + parrCtls[i].mlngColSpan;
			if ( parrCtls[i].mstrParamTag != null )
				llngParamCount++;
		}

		if ( llngParamCount > 0 )
			marrExtParams = new ParamInfo[llngParamCount];
		else
			marrExtParams = null;

		r = 0;
		c = 0;
		x = 0;
		p = 0;

		for ( i = 0; i < parrCtls.length; i++ )
		{
			if ( parrCtls[i].mstrParamTag != null )
			{
				marrExtParams[p] = new ParamInfo();
				marrExtParams[p].mstrTag = parrCtls[i].mstrParamTag;
				marrExtParams[p].mlngIndex = i;
				p++;
			}

			while ( parrCtls[i].mlngRow > r )
			{
				if ( x <= llngTblCols )
				{
					mtblForm.setHTML(r, c, "&nbsp;");
					mtblForm.getFlexCellFormatter().setColSpan(r, c, llngTblCols - x + 1);
					mtblForm.getFlexCellFormatter().getElement(r, c).getStyle().setWidth(((int)((llngTblCols - x + 1) * 100 - 5)),
							Unit.PX);
				}
				x = 0;
				c = 0;
				r++;
				mtblForm.insertRow(r);
			}

			if ( x < parrCtls[i].mlngColumn )
			{
				mtblForm.setHTML(r, c, "&nbsp;");
				mtblForm.getFlexCellFormatter().setColSpan(r, c, parrCtls[i].mlngColumn - x);
				mtblForm.getFlexCellFormatter().getElement(r, c).getStyle().setWidth(((int)((parrCtls[i].mlngColumn - x) * 100 - 5)),
						Unit.PX);
				x = parrCtls[i].mlngColumn;
				c++;
			}

			llblAux = new Label();
			llblAux.setText(parrCtls[i].mstrCaption);
			llblAux.setWidth("95px");
			mtblForm.setWidget(r, c, llblAux);
			mtblForm.getFlexCellFormatter().getElement(r, c).getStyle().setWidth(95, Unit.PX);
			x++;
			c++;

			lctlAux = BuildControl(parrCtls[i], i);
			mtblForm.setWidget(r, c, lctlAux);
			mtblForm.getFlexCellFormatter().setColSpan(r, c, parrCtls[i].mlngColSpan);
			mtblForm.getFlexCellFormatter().getElement(r, c).getStyle().setWidth(((int)(parrCtls[i].mlngColSpan * 100 - 5)), Unit.PX);
			((IJewelWebCtl)lctlAux).setWidth(((int)(parrCtls[i].mlngColSpan * 100 - 5)));
			x += parrCtls[i].mlngColSpan;
			c++;
		}
		if ( x <= llngTblCols )
		{
			mtblForm.setHTML(r, c, "&nbsp;");
			mtblForm.getFlexCellFormatter().setColSpan(r, c, llngTblCols - x + 1);
		}
	}

	private Widget BuildControl(FormCtlObj prefControl, int plngIndex)
	{
		Widget lctlAux;

		switch ( prefControl.mlngType )
		{
		case FormCtlObj.TEXTBOX:
			lctlAux = new StringBox();
			break;

		case FormCtlObj.INTBOX:
			lctlAux = new IntBox();
			break;

		case FormCtlObj.DECBOX:
			lctlAux = new DecBox();
			break;

		case FormCtlObj.PWDBOX:
			lctlAux = new PasswordBox();
			break;
	
		case FormCtlObj.LABELBOX:
			lctlAux = new LabelBox();
			break;
			
		case FormCtlObj.TRICHECKBOX:
			lctlAux = new TriStateCheckboxCtl();
			break;

		case FormCtlObj.LOOKUP:
			lctlAux = new Lookup(prefControl.mstrFormID, mstrNameSpace, mstrFormID);
			break;

		case FormCtlObj.DROPDOWN:
			lctlAux = new DropdownList(mstrNameSpace, prefControl.mstrObjID);
			break;

		case FormCtlObj.CALENDAR:
			lctlAux = new DateCtl();
			break;

		case FormCtlObj.VALUELOOKUP:
			lctlAux = new ValueLookup();
			((ValueLookup)lctlAux).addJErrorHandler(new JErrorEvent.Handler()
			{
				public void onJError(JErrorEvent event)
				{
					mrefEventMgr.fireEvent(event);
				}
			});
			break;

		case FormCtlObj.FILEXFER:
			lctlAux = new FileCtl();
			((FileCtl)lctlAux).addJErrorHandler(new JErrorEvent.Handler()
			{
				public void onJError(JErrorEvent event)
				{
					mrefEventMgr.fireEvent(event);
				}
			});
			break;

		default:
			lctlAux = null;
		}

		marrControls[plngIndex] = (IJewelWebCtl)lctlAux;
		marrDefaults[plngIndex] = prefControl.mstrDefault;
		return lctlAux;
	}

	public void DoClose()
	{
		int i;

		if ( marrControls == null )
			return;

		for ( i = 0; i < marrControls.length; i++ )
			if ( marrControls[i] instanceof FileCtl )
				((FileCtl)marrControls[i]).DiscardValue();
	}

	public HandlerRegistration addInitHandler(InitEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(InitEvent.TYPE, handler);
	}

	public HandlerRegistration addJErrorHandler(JErrorEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(JErrorEvent.TYPE, handler);
	}
}
