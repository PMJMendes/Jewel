package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("dynaview")
public interface DynaViewService
	extends RemoteService
{
	DynaViewResponse GetTabs(String pstrViewID) throws JewelWebException;
}
