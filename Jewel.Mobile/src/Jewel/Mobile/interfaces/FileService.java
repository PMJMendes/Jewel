package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("file")
public interface FileService
	extends RemoteService
{
	String Discard(String pstrID) throws JewelMobileException;
}
