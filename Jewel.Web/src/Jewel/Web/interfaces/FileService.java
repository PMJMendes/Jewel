package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("file")
public interface FileService
	extends RemoteService
{
	String Discard(String pstrID) throws JewelWebException;
}
