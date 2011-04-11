package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface LookupServiceAsync
{
	void OpenPopup(String pstrEntity, AsyncCallback<LookupResponse> callback);
	void OpenPopup(String pstrFormID, String pstrNameSpace, AsyncCallback<LookupResponse> callback);
}
