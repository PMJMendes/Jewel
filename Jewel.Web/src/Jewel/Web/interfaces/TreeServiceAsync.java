package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface TreeServiceAsync
{
	void GetNodes(AsyncCallback<TreeNodeObj[]> callback);

	void ClickNode(String pstrID, String pstrNSpace, AsyncCallback<TreeResponse> callback);

	void ClickSysNode(String pstrNode, AsyncCallback<TreeResponse> callback);
}
