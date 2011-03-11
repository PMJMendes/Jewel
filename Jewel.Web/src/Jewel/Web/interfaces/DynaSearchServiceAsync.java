package Jewel.Web.interfaces;

import com.google.gwt.user.client.rpc.*;

public interface DynaSearchServiceAsync
{
	void GetQueryID(String pstrFormID, AsyncCallback<String> callback);
}
