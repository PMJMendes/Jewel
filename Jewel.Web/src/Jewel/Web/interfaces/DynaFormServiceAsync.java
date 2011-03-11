package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface DynaFormServiceAsync
{
	void GetControls(String pstrFormID, String pstrNameSpace, AsyncCallback<FormCtlObj[]> callback);
}
