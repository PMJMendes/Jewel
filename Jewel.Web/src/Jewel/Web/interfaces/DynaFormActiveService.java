package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("dynaformactive")
public interface DynaFormActiveService
	extends RemoteService
{
	FormActionObj[] GetActions(String pstrFormID) throws JewelWebException;
	FormActionResponse DoAction(int plngID, String pstrFormID, String pstrNameSpace, String[] parrData) throws JewelWebException;
}
