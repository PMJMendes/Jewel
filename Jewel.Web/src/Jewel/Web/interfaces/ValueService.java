package Jewel.Web.interfaces;

import com.google.gwt.user.client.rpc.*;

import Jewel.Web.shared.*;

@RemoteServiceRelativePath("value")
public interface ValueService
	extends RemoteService
{
	String GetDisplayText(String pstrEntity, String pstrKey) throws JewelWebException;
}
