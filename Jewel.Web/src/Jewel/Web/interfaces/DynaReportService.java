package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("dynareport")
public interface DynaReportService
	extends RemoteService
{
	String GetParamFormID(String pstrReportID) throws JewelWebException;
	String OpenReport(ReportID pobjParams) throws JewelWebException;
}
