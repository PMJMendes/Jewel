package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("grid")
public interface GridService
	extends RemoteService
{
	GridResponse OpenQuery(String pstrQueryID, String pstrNameSpace, boolean pbForceParam, String pstrParam,
			String pstrFormID, ParamInfo[] parrExtParams, String pstrInitValue) throws JewelMobileException;
	GridResponse SetPageSize(String pstrWorkspace, int plngPageSize) throws JewelMobileException;
	GridResponse PageForward(String pstrWorkspace) throws JewelMobileException;
	GridResponse PageBack(String pstrWorkspace) throws JewelMobileException;
	GridResponse PageFirst(String pstrWorkspace) throws JewelMobileException;
	GridResponse PageLast(String pstrWorkspace) throws JewelMobileException;
	GridResponse ForceRefresh(String pstrWorkspace, boolean pbForceParam) throws JewelMobileException;
	GridResponse ReloadAt(String pstrWorkspace, boolean pbForceParam, String pstrParam) throws JewelMobileException;
	GridResponse ApplySearch(String pstrWorkspace, String pstrFormID, String[] parrData) throws JewelMobileException;
	GridResponse ApplySort(String pstrWorkspace, int[] parrOrder) throws JewelMobileException;
	DataObject GetRow(String pstrWorkspace, int plngRow) throws JewelMobileException;
	GridResponse NewRow(String pstrWorkspace) throws JewelMobileException;
	GridActionResponse DoAction(String pstrWorkspace, int plngRow, int plngOrder, int plngAction,
			DataObject pobjData) throws JewelMobileException;
	GridSaveResponse SaveRow(String pstrWorkspace, int plngRow, DataObject pobjData) throws JewelMobileException;
	GridResponse DeleteRow(String pstrWorkspace, int plngRow) throws JewelMobileException;
	String CloseQuery(String pstrWorkspace) throws JewelMobileException;
}
