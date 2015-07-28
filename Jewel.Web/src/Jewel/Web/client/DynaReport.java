package Jewel.Web.client;

import Jewel.Web.client.events.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class DynaReport
	extends Composite
	implements ClosableContent, JErrorEvent.HasEvent
{
	private DynaReportServiceAsync reportSvc;

	private String mstrReportID;
	private String mstrNameSpace;
	private String mstrFormID;
	private String mstrRefObj;

	private DynaForm mobjParamForm;

	private HandlerManager mrefEventMgr;

	public DynaReport()
	{
		VerticalPanel lvert;
		Button lbtn;

		lvert = new VerticalPanel();
		lvert.setStylePrimaryName("jewel-DynaReport");

		mobjParamForm = new DynaForm();
		lvert.add(mobjParamForm);

		lbtn = new Button();
		lbtn.setText("Open Report");
		lbtn.addStyleName("jewel-DynaReport-Open");
		lvert.add(lbtn);

		initWidget(lvert);

		lbtn.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				doOpen();
	        }
	     });

		mrefEventMgr = new HandlerManager(this);
	}

	private DynaReportServiceAsync getService()
	{
		if ( reportSvc == null )
			reportSvc = GWT.create(DynaReportService.class);

		return reportSvc;
	}

	public void InitReport(String pstrReportID, String pstrNameSpace, String pstrRefObj)
	{
		AsyncCallback<String> callback = new AsyncCallback<String>()
        {
			public void onSuccess(String result)
			{
				if (result != null)
				{
					mstrFormID = result;
					mobjParamForm.InitForm(mstrFormID, mstrNameSpace, null, true);
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

        mstrReportID = pstrReportID;
        mstrNameSpace = pstrNameSpace;
        mstrRefObj = pstrRefObj;
		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().GetParamFormID(mstrReportID, callback);
	}

	public void SetRefObject(String pstrRefObj)
	{
        mstrRefObj = pstrRefObj;
	}

	private void doOpen()
	{
		ReportID lobjReport;

		AsyncCallback<String> callback = new AsyncCallback<String>()
        {
			public void onSuccess(String result)
			{
				if (result != null)
				{
					Window.open(GWT.getModuleBaseURL() + "report?rptid=" + result, null,
							"menubar=yes,toolbar=yes,scrollbars=yes,resizable=yes");
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

		lobjReport = new ReportID();
		lobjReport.mstrReportID = mstrReportID;
		lobjReport.mstrNameSpace = mstrNameSpace;
		lobjReport.mstrFormID = mstrFormID;
		lobjReport.mstrRefObj = mstrRefObj;
		lobjReport.marrValues = mobjParamForm.GetData();
		mrefEventMgr.fireEvent(new JErrorEvent(null));
		getService().OpenReport(lobjReport, callback);
    }

	public void DoClose()
	{
	}

	public HandlerRegistration addJErrorHandler(JErrorEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(JErrorEvent.TYPE, handler);
	}
}
