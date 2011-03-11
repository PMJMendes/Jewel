package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface DynaViewServiceAsync
{
	void GetTabs(String pstrViewID, AsyncCallback<DynaViewResponse> callback);
}
