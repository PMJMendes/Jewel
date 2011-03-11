package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

@RemoteServiceRelativePath("dynagrid")
public interface DynaGridService
	extends RemoteService
{
	DynaGridResponse OpenQuery(String pstrQueryID, String pstrNameSpace, boolean pbForceParam, String pstrParam,
			String pstrFormID, ParamInfo[] parrExtParams, String pstrInitValue) throws JewelWebException;
	DynaGridResponse PageForward(String pstrWorkspace) throws JewelWebException;
	DynaGridResponse PageBack(String pstrWorkspace) throws JewelWebException;
	DynaGridResponse PageFirst(String pstrWorkspace) throws JewelWebException;
	DynaGridResponse PageLast(String pstrWorkspace) throws JewelWebException;
	DynaGridResponse ForceRefresh(String pstrWorkspace, boolean pbForceParam) throws JewelWebException;
	DynaGridResponse ReloadAt(String pstrWorkspace, boolean pbForceParam, String pstrParam) throws JewelWebException;
	DynaGridResponse ApplySearch(String pstrWorkspace, String pstrFormID, String[] parrData) throws JewelWebException;
	DynaGridResponse ApplySort(String pstrWorkspace, int[] parrOrder) throws JewelWebException;
	DataObject GetRow(String pstrWorkspace, int plngRow) throws JewelWebException;
	DynaGridResponse NewRow(String pstrWorkspace) throws JewelWebException;
	GridActionResponse DoAction(String pstrWorkspace, int plngRow, int plngOrder, int plngAction,
			DataObject pobjData) throws JewelWebException;
	DynaGridSaveResponse SaveRow(String pstrWorkspace, int plngRow, DataObject pobjData) throws JewelWebException;
	DynaGridResponse DeleteRow(String pstrWorkspace, int plngRow) throws JewelWebException;
	String CloseQuery(String pstrWorkspace) throws JewelWebException;
}
