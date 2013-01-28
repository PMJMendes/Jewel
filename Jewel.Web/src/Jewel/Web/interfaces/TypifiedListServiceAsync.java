package Jewel.Web.interfaces;

import Jewel.Web.shared.TypifiedListItem;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TypifiedListServiceAsync
{
	void getListItems(String pstrNSpace, String pstrListId, AsyncCallback<TypifiedListItem[]> callback);
}
