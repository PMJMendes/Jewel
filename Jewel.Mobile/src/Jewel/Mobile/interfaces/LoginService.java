package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("login")
public interface LoginService
	extends RemoteService
{
	LoginDomain[] GetDomains() throws JewelMobileException;
	String CheckLogin() throws JewelMobileException;
	String CheckLogin(LoginResponse pobjLogin) throws JewelMobileException;
}
