package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("lookup")
public interface LookupService
	extends RemoteService
{
	LookupResponse OpenPopup(String pstrEntity) throws JewelMobileException;
	LookupResponse OpenPopup(String pstrFormID, String pstrNameSpace) throws JewelMobileException;
}
