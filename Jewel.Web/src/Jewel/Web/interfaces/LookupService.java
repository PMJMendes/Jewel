package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("lookup")
public interface LookupService
	extends RemoteService
{
	LookupResponse OpenPopup(String pstrEntity) throws JewelWebException;
	LookupResponse OpenPopup(String pstrFormID, String pstrNameSpace) throws JewelWebException;
}
