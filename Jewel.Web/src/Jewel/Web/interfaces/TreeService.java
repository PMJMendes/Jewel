package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("tree")
public interface TreeService
	extends RemoteService
{
	TreeNodeObj[] GetNodes() throws JewelWebException;
	TreeResponse ClickNode(String pstrID, String pstrNSpace) throws JewelWebException;
	TreeResponse ClickSysNode(String pstrNode);
}
