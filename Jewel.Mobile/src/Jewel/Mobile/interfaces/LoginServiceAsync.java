package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface LoginServiceAsync
{
	void GetDomains(AsyncCallback<LoginDomain[]> callback);
	void CheckLogin(AsyncCallback<String> callback);
	void CheckLogin(LoginResponse pobjLogin, AsyncCallback<String> callback);
}
