package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("complexscreen")
public interface ComplexScreenService
	extends RemoteService
{
	String GetQueryID(String pstrFormID) throws JewelMobileException;
}
