package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("menu")
public interface MenuService
	extends RemoteService
{
	MenuNodeObj[] GetNodes() throws JewelMobileException;
	MenuResponse ClickNode(String pstrID, String pstrNSpace) throws JewelMobileException;
	MenuResponse ClickSysNode(String pstrNode);
}
