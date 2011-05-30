package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.JewelMobileException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("value")
public interface ValueService
	extends RemoteService
{
	String GetDisplayText(String pstrEntity, String pstrKey) throws JewelMobileException;
}
