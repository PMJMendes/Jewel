package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("login")
public interface LoginService
	extends RemoteService
{
	LoginDomain[] GetDomains() throws JewelWebException;
	String CheckLogin(LoginResponse pobjLogin) throws JewelWebException;
}
