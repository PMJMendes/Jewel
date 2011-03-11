package Jewel.Web.interfaces;

import com.google.gwt.user.client.rpc.*;

public interface FileServiceAsync
{
	void Discard(String pstrID, AsyncCallback<String> callback);
}
