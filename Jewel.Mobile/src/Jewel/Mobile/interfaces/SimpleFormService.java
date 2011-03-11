package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("simpleform")
public interface SimpleFormService
	extends RemoteService
{
	FormCtlObj[] GetControls(String pstrFormID, String pstrNameSpace) throws JewelMobileException;
}
