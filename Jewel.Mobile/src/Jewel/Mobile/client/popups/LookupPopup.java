package Jewel.Mobile.client.popups;

import Jewel.Mobile.client.*;
import Jewel.Mobile.client.controls.*;
import Jewel.Mobile.client.events.*;
import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class LookupPopup
	extends DialogBox
{
	private LookupServiceAsync lookupSvc;

	private Lookup mrefOwner;
	private String mstrFormID;
	private String mstrNameSpace;
	private String mstrTmpValue;
	private String mstrTmpFormID;
	private ParamInfo[] marrTmpParams;

	private ClosableHeader mheader;
	private PopupGrid msrcMain;

	public LookupPopup(Lookup prefOwner)
	{
		super(false, true);

		VerticalPanel lpn;

		mrefOwner = prefOwner;

		mstrFormID = null;
		mstrNameSpace = null;

		setStylePrimaryName("lookupPopup");

		setGlassEnabled(true);
		setGlassStyleName("lookupPopup-Glass");

		lpn = new VerticalPanel();
		lpn.setStylePrimaryName("lookupPopup-Wrapper");

		mheader = new ClosableHeader("");
		lpn.add(mheader);

		msrcMain = new PopupGrid();
		lpn.add(msrcMain);

		setWidget(lpn);

		mheader.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				if ( !msrcMain.TryGoBack() )
				{
					msrcMain.DoClose();
					hide();
				}
	        }
	    });

		msrcMain.addSelectHandler(new SelectEvent.Handler()
		{
			public void onSelect(SelectEvent event)
			{
				if ( event.getResult() == null )
					mrefOwner.setJValue(null, true);
				else
					mrefOwner.setJValue(event.getResult().mstrID + "!" +  event.getResult().mstrDisplayName, true);
				msrcMain.DoClose();
				hide();
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
					mheader.SetText(result.mstrFormName + " @ " + result.mstrNSpaceName);
					msrcMain.InitGrid(mstrFormID, result.mstrQueryID, mstrNameSpace, false, null, mstrTmpFormID,
							marrTmpParams, mstrTmpValue);
					mstrTmpValue = null;
					mstrTmpFormID = null;
					marrTmpParams = null;
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
		mstrTmpValue = pstrInitialValue;
		mstrNameSpace = pstrNameSpace;
		mstrTmpFormID = pstrParamFormID;
		marrTmpParams = parrExtParams;
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
					msrcMain.InitGrid(mstrFormID, result.mstrQueryID, mstrNameSpace, false, null, mstrTmpFormID,
							marrTmpParams, mstrTmpValue);
					mstrTmpValue = null;
					mstrTmpFormID = null;
					marrTmpParams = null;
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

		mstrTmpValue = pstrInitialValue;
		mstrTmpFormID = pstrParamFormID;
		marrTmpParams = parrExtParams;
        getService().OpenPopup(pstrEntity, callback);
	}
}
