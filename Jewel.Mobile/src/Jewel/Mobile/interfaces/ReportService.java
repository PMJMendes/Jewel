package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.JewelMobileException;
import Jewel.Mobile.shared.ReportID;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("report")
public interface ReportService
	extends RemoteService
{
	String GetParamFormID(String pstrReportID) throws JewelMobileException;
	String OpenReport(ReportID pobjParams) throws JewelMobileException;
}
