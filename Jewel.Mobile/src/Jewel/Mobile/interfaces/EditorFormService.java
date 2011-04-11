package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("editorform")
public interface EditorFormService
	extends RemoteService
{
	CommandResponse DoCommand(int plngID, String pstrFormID, String pstrNameSpace, String[] parrData) throws JewelMobileException;
}
