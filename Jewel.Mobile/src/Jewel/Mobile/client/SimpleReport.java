package Jewel.Mobile.client;

import Jewel.Mobile.client.events.InitEvent;
import Jewel.Mobile.interfaces.ReportService;
import Jewel.Mobile.interfaces.ReportServiceAsync;
import Jewel.Mobile.shared.ReportID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SimpleReport
	extends Composite
	implements ClosableContent
{
	private ReportServiceAsync reportSvc;

	private String mstrReportID;
	private String mstrNameSpace;
	private String mstrFormID;

	private SimpleForm mfrmParams;
	private Button mbtnOpenReport;

	public SimpleReport(String pstrText)
	{
		VerticalPanel lvert;
		ClosableHeader lheader;

		lvert = new VerticalPanel();
		lvert.setStylePrimaryName("simpleReport");

		lheader = new ClosableHeader(pstrText);
		lvert.add(lheader);
		lheader.getElement().getParentElement().setClassName("simpleReport-Header-Wrapper");

		mfrmParams = new SimpleForm();
		lvert.add(mfrmParams);
		mfrmParams.getElement().getParentElement().setClassName("simpleReport-Form-Wrapper");

		mbtnOpenReport = new Button();
		mbtnOpenReport.setText("Open Report");
		mbtnOpenReport.setStylePrimaryName("simpleReport-Open");
		mbtnOpenReport.setEnabled(false);
		lvert.add(mbtnOpenReport);
		mbtnOpenReport.getElement().getParentElement().setClassName("simpleReport-Open-Wrapper");

		initWidget(lvert);

		lheader.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				Jewel_Mobile.getReference().setMenuScreen(Menu.GetRoot());
	        }
	    });

		mfrmParams.addInitHandler(new InitEvent.Handler()
		{
			public void onInit(InitEvent event)
			{
				mbtnOpenReport.setEnabled(true);
			}
		});

		mbtnOpenReport.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				doOpen();
	        }
	     });
	}

	private ReportServiceAsync getService()
	{
		if ( reportSvc == null )
			reportSvc = GWT.create(ReportService.class);

		return reportSvc;
	}

	public void InitReport(String pstrReportID, String pstrNameSpace)
	{
		AsyncCallback<String> callback = new AsyncCallback<String>()
        {
			public void onSuccess(String result)
			{
				if (result != null)
				{
					mstrFormID = result;
					mfrmParams.InitForm(mstrFormID, mstrNameSpace, null, true);
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

        mstrReportID = pstrReportID;
        mstrNameSpace = pstrNameSpace;
        getService().GetParamFormID(mstrReportID, callback);
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

		lobjReport = new ReportID();
		lobjReport.mstrReportID = mstrReportID;
		lobjReport.mstrNameSpace = mstrNameSpace;
		lobjReport.mstrFormID = mstrFormID;
		lobjReport.marrValues = mfrmParams.GetData();
		getService().OpenReport(lobjReport, callback);
    }

	public void DoClose()
	{
		mfrmParams.DoClose();
	}
}
