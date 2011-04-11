package Jewel.Mobile.client;

import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class ComplexScreen
	extends Composite
	implements ClosableContent
{
	private ComplexScreenServiceAsync cplxSvc;

	private String mstrFormID;
	private String mstrNameSpace;
	private String mstrTmpInitValue;
	private String mstrTmpFormID;
	private ParamInfo[] marrTmpParams;

	private SingleGrid mgrdResults;

	public ComplexScreen(String pstrText)
	{
		VerticalPanel lpnOuter;
		ClosableHeader lheader;

		lpnOuter = new VerticalPanel();
		lpnOuter.setStylePrimaryName("complexScreen");

		lheader = new ClosableHeader(pstrText);
		lpnOuter.add(lheader);
		lheader.getElement().getParentElement().setClassName("complexScreen-Header-Wrapper");

		mgrdResults = new SingleGrid(false);
		lpnOuter.add(mgrdResults);
		mgrdResults.getElement().getParentElement().setClassName("complexScreen-Grid-Wrapper");

		initWidget(lpnOuter);

		lheader.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				if ( !mgrdResults.TryGoBack() )
					Jewel_Mobile.getReference().setMenuScreen(Menu.GetRoot());
	        }
	    });
	}

	private ComplexScreenServiceAsync getService()
	{
		if ( cplxSvc == null )
			cplxSvc = GWT.create(ComplexScreenService.class);

		return cplxSvc;
	}

	public void InitScreen(String pstrFormID, String pstrNameSpace, String pstrInitialValue,
			String pstrParamFormID, ParamInfo[] parrExtParams)
	{
		AsyncCallback<String> callback = new AsyncCallback<String>()
        {
			public void onSuccess(String result)
			{
				if ( result != null )
				{
					mgrdResults.InitGrid(mstrFormID, result, mstrNameSpace, false, null, mstrTmpFormID, marrTmpParams,
							mstrTmpInitValue);
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
		mstrTmpInitValue = pstrInitialValue;
		mstrTmpFormID = pstrParamFormID;
		marrTmpParams = parrExtParams;
    	getService().GetQueryID(mstrFormID, callback);
	}

	public void DoClose()
	{
		mgrdResults.DoClose();
	}
}
