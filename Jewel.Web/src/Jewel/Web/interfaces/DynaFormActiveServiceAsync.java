package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface DynaFormActiveServiceAsync
{
	void GetActions(String pstrFormID, AsyncCallback<FormActionObj[]> callback);
	void DoAction(int plngID, String pstrFormID, String pstrNameSpace, String[] parrData, AsyncCallback<FormActionResponse> callback);
}
