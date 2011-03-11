package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface MenuServiceAsync
{
	void GetNodes(AsyncCallback<MenuNodeObj[]> callback);
	void ClickNode(String pstrID, String pstrNSpace, AsyncCallback<MenuResponse> callback);
	void ClickSysNode(String pstrNode, AsyncCallback<MenuResponse> callback);
}
