package Jewel.Mobile.client;

import Jewel.Mobile.client.controls.*;
import Jewel.Mobile.client.events.*;
import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class SimpleForm
	extends Composite
	implements ClosableContent, InitEvent.HasEvent
{
	private SimpleFormServiceAsync formSvc;

	private String mstrFormID;
	private String mstrNameSpace;
	private String[] marrTmpData;
	private ParamInfo[] marrExtParams;
	private boolean mbInit;
	private String[] marrDefaults;
	private boolean mbUseDefaults;

	private Grid mtblForm;
	private IJewelMobileCtl[] marrControls;

	private HandlerManager mrefEventMgr;

	public SimpleForm()
	{
		mbInit = false;

		mtblForm = new Grid();
		mtblForm.setCellSpacing(5);
		mtblForm.setCellPadding(0);
		mtblForm.setStylePrimaryName("simpleForm");

		initWidget(mtblForm);

		mrefEventMgr = new HandlerManager(this);
	}

	private SimpleFormServiceAsync getService()
	{
		if ( formSvc == null )
			formSvc = GWT.create(SimpleFormService.class);

		return formSvc;
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
					mbInit = true;
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

		mstrFormID = pstrFormID;
		mstrNameSpace = pstrNameSpace;
		marrTmpData = parrData;
		mbUseDefaults = pbUseDefaults;
        getService().GetControls(mstrFormID, mstrNameSpace, callback);
	}

	public String[] GetData()
	{
		String[] marrAux;
		int i;

		if ( !mbInit )
			return new String[0];

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

		if ( !mbInit )
			return;

		for ( i = 0; i < marrControls.length; i++ )
			marrControls[i].setJValue(((mbUseDefaults && (parrData[i] == null)) ? marrDefaults[i] : parrData[i]));
	}

	public void ClearData()
	{
		int i;

		if ( !mbInit )
			return;

		for ( i = 0; i < marrControls.length; i++ )
			marrControls[i].setJValue(null);
	}

	public ParamInfo[] GetExternalParams()
	{
		int i;

		if ( !mbInit )
			return null;

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

	private void BuildForm(FormCtlObj[] parrCtls)
	{
		int llngParamCount;
		int i, p;
		Label llblAux;
		Widget lctlAux;

		mtblForm.clear();
		mtblForm.resize(parrCtls.length, 2);

		marrControls = new IJewelMobileCtl[parrCtls.length];
		marrDefaults = new String[parrCtls.length];

		llngParamCount = 0;
		for ( i = 0; i < parrCtls.length; i++ )
		{
			if ( parrCtls[i].mstrParamTag != null )
				llngParamCount++;
		}

		if ( llngParamCount > 0 )
			marrExtParams = new ParamInfo[llngParamCount];
		else
			marrExtParams = null;

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

			llblAux = new Label();
			llblAux.setText(parrCtls[i].mstrCaption);
			llblAux.setStylePrimaryName("simpleForm-Label");
			mtblForm.setWidget(i, 0, llblAux);
			llblAux.getElement().getParentElement().setClassName("simpleForm-Label-Wrapper");

			lctlAux = BuildControl(parrCtls[i], i);
			mtblForm.setWidget(i, 1, lctlAux);
			lctlAux.getElement().getParentElement().setClassName("simpleForm-Control-Wrapper");
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

		case FormCtlObj.BOOLDROPDOWN:
			lctlAux = new BoolDropdown();
			break;

		case FormCtlObj.LOOKUP:
			lctlAux = new Lookup(prefControl.mstrFormID, mstrNameSpace, mstrFormID, false);
			break;

		case FormCtlObj.CALENDAR:
			lctlAux = new DateCtl();
			break;

		case FormCtlObj.VALUELOOKUP:
			lctlAux = new ValueLookup();
			break;

		case FormCtlObj.FILEXFER:
			lctlAux = new FileCtl();
			break;

		default:
			lctlAux = null;
		}

		marrControls[plngIndex] = (IJewelMobileCtl)lctlAux;
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
}
