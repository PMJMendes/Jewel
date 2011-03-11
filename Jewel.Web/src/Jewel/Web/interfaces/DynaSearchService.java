package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("dynasearch")
public interface DynaSearchService
	extends RemoteService
{
	String GetQueryID(String pstrFormID) throws JewelWebException;
}
