package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("dynaform")
public interface DynaFormService
	extends RemoteService
{
	FormCtlObj[] GetControls(String pstrFormID, String pstrNameSpace) throws JewelWebException;
}
