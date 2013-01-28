package Jewel.Web.interfaces;

import Jewel.Web.shared.JewelWebException;
import Jewel.Web.shared.TypifiedListItem;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("list")
public interface TypifiedListService
	extends RemoteService
{
	TypifiedListItem[] getListItems(String pstrNSpace, String pstrListId)  throws JewelWebException;
}
