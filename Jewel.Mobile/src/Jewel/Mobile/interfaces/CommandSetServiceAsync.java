package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface CommandSetServiceAsync
{
	void GetCommands(String pstrFormID, AsyncCallback<CommandObj[]> callback);
}
