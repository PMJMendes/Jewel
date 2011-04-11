package Jewel.Mobile.interfaces;

import com.google.gwt.user.client.rpc.*;

public interface ComplexScreenServiceAsync
{
	void GetQueryID(String pstrFormID, AsyncCallback<String> callback);
}
