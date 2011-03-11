package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface SingleFormServiceAsync
{
	void DoCommand(int plngID, String pstrFormID, String pstrNameSpace, String[] parrData, AsyncCallback<CommandResponse> callback);
}
