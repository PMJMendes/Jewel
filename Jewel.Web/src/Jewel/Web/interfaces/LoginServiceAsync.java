package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface LoginServiceAsync
{
	void GetDomains(AsyncCallback<LoginDomain[]> callback);
	void CheckLogin(LoginResponse pobjLogin, AsyncCallback<String> callback);
}
