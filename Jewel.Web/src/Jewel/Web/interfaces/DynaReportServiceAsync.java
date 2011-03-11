package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface DynaReportServiceAsync
{
	void GetParamFormID(String pstrReportID, AsyncCallback<String> callback);
	void OpenReport(ReportID pobjParams, AsyncCallback<String> callback);
}
