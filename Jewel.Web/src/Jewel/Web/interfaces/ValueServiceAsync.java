package Jewel.Web.interfaces;

import com.google.gwt.user.client.rpc.*;

public interface ValueServiceAsync 
{
	void GetDisplayText(String pstrEntity, String pstrKey, AsyncCallback<String> callback);
}
