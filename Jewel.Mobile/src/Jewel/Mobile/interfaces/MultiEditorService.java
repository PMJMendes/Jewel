package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("multieditor")
public interface MultiEditorService
	extends RemoteService
{
	EditorResponse GetTabs(String pstrViewID) throws JewelMobileException;
}
