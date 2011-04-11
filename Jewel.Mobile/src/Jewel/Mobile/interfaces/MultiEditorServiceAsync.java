package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface MultiEditorServiceAsync
{
	void GetTabs(String pstrViewID, AsyncCallback<EditorResponse> callback);
}
