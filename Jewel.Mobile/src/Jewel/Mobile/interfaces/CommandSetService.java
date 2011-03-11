package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("commandset")
public interface CommandSetService
	extends RemoteService
{
	CommandObj[] GetCommands(String pstrFormID) throws JewelMobileException;
}
