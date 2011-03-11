package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface SimpleFormServiceAsync
{
	void GetControls(String pstrFormID, String pstrNameSpace, AsyncCallback<FormCtlObj[]> callback);
}
