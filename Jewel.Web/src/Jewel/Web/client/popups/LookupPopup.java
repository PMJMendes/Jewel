package Jewel.Web.client.popups;

import Jewel.Web.client.*;
import Jewel.Web.client.controls.*;
import Jewel.Web.client.events.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class LookupPopup
	extends DialogBox
{
	private LookupServiceAsync lookupSvc;

	private Label mlblError;
	private DynaSearch msrcMain;

	private Lookup mrefOwner;
	private String mstrFormID;
	private String mstrNameSpace;
	private String mstrEntity;
	private boolean mbInit;
	private String mstrTmpValue;
	private String mstrTmpFormID;
	private ParamInfo[] marrTmpParams;

	public LookupPopup(Lookup prefOwner)
	{
		super(false, true);

		VerticalPanel lvert;

		mrefOwner = prefOwner;

		mstrFormID = null;
		mstrNameSpace = null;
		mstrEntity = null;
		mbInit = false;

		this.setStylePrimaryName("jewel-LookupPopup");

		lvert = new VerticalPanel();

		mlblError = new Label(" ");
		mlblError.setStylePrimaryName("messageLine");
		mlblError.addStyleName("alternate");
		lvert.add(mlblError);
		mlblError.getElement().getParentElement().setClassName("jewel-LookupPopup-Error-Wrapper");

		msrcMain = new DynaSearch();
		msrcMain.SetForPopup();
		lvert.add(msrcMain);

		setWidget(lvert);
		
		msrcMain.addInitHandler(new InitEvent.Handler()
		{
			public void onInit(InitEvent event)
			{
				center();
				mlblError.setWidth((getOffsetWidth() - 8) + "px");
			}
		});
		msrcMain.addSelectHandler(new SelectEvent.Handler()
		{
			public void onSelect(SelectEvent event)
			{
				mrefOwner.setJValue(event.getResult(), true);
				msrcMain.DoClose();
				hide();
			}
		});
		msrcMain.addCancelHandler(new CancelEvent.Handler()
		{
			public void onCancel(CancelEvent event)
			{
				msrcMain.DoClose();
				hide();
			}
		});
		msrcMain.addJErrorHandler(new JErrorEvent.Handler()
		{
			public void onJError(JErrorEvent event)
			{
				SetError(event.getError());
			}
		});
	}

	private LookupServiceAsync getService()
	{
		if ( lookupSvc == null )
			lookupSvc = GWT.create(LookupService.class);

		return lookupSvc;
	}

	public void InitPopup(String pstrFormID, String pstrNameSpace, String pstrInitialValue,
			String pstrParamFormID, ParamInfo[] parrExtParams)
	{
		AsyncCallback<LookupResponse> callback = new AsyncCallback<LookupResponse>()
        {
			public void onSuccess(LookupResponse result)
			{
				if (result != null)
				{
					setText(result.mstrFormName + " @ " + result.mstrNSpaceName);
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

			}
        };

		msrcMain.InitSearch(pstrFormID, pstrNameSpace, pstrInitialValue, pstrParamFormID, parrExtParams);
		if (mbInit && (pstrFormID.equals(mstrFormID)) && (pstrNameSpace.equals(mstrNameSpace)))
			return;
		mbInit = true;
		mstrFormID = pstrFormID;
		mstrNameSpace = pstrNameSpace;
        getService().OpenPopup(mstrFormID, mstrNameSpace, callback);
	}

	public void InitPopup(String pstrEntity, String pstrInitialValue, String pstrParamFormID, ParamInfo[] parrExtParams)
	{
		AsyncCallback<LookupResponse> callback = new AsyncCallback<LookupResponse>()
        {
			public void onSuccess(LookupResponse result)
			{
				if (result != null)
				{
					setText(result.mstrFormName + " @ " + result.mstrNSpaceName);
					mstrFormID = result.mstrFormID;
					mstrNameSpace = result.mstrNSpaceID;
					msrcMain.InitSearch(mstrFormID, mstrNameSpace, mstrTmpValue, mstrTmpFormID, marrTmpParams);
					mstrTmpValue = null;
					mstrTmpFormID = null;
					marrTmpParams = null;
				}
				else
				{
					Jewel_Web.getReference().setLoginScreen();
				}
			}

			public void onFailure(Throwable ex)
			{
			}
        };

		if (mbInit && (pstrEntity.equals(mstrEntity)))
		{
			msrcMain.InitSearch(mstrFormID, mstrNameSpace, pstrInitialValue, pstrParamFormID, parrExtParams);
			return;
		}
		mbInit = true;
		mstrEntity = pstrEntity;
		mstrTmpValue = pstrInitialValue;
		mstrTmpFormID = pstrParamFormID;
		marrTmpParams = parrExtParams;
        getService().OpenPopup(pstrEntity, callback);
	}
	
	private void SetError(String pstrError)
	{
		if ( (pstrError == null) || (pstrError.equals("")) )
			mlblError.setText(" ");
		else
			mlblError.setText(pstrError);
	}
}
