package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.ReportID;

import com.google.gwt.user.client.rpc.*;

public interface ReportServiceAsync
{
	void GetParamFormID(String pstrReportID, AsyncCallback<String> callback);
	void OpenReport(ReportID pobjParams, AsyncCallback<String> callback);
}
