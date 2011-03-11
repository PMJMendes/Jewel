package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface LookupServiceAsync
{
	void OpenPopup(String pstrEntity, AsyncCallback<LookupResponse> callback);
	void OpenPopup(String pstrFormID, String pstrNameSpace, AsyncCallback<LookupResponse> callback);
}
