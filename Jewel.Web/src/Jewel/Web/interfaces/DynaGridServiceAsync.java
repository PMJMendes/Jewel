package Jewel.Web.interfaces;

import Jewel.Web.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface DynaGridServiceAsync
{
	void OpenQuery(String pstrQueryID, String pstrNameSpace, boolean pbForceParam, String pstrParam,
			String pstrFormID, ParamInfo[] parrExtParams, String pstrInitValue, AsyncCallback<DynaGridResponse> callback);
	void PageForward(String pstrWorkspace, AsyncCallback<DynaGridResponse> callback);
	void PageBack(String pstrWorkspace, AsyncCallback<DynaGridResponse> callback);
	void PageFirst(String pstrWorkspace, AsyncCallback<DynaGridResponse> callback);
	void PageLast(String pstrWorkspace, AsyncCallback<DynaGridResponse> callback);
	void ForceRefresh(String pstrWorkspace, boolean pbForceParam, AsyncCallback<DynaGridResponse> callback);
	void ReloadAt(String pstrWorkspace, boolean pbForceParam, String pstrParam, AsyncCallback<DynaGridResponse> callback);
	void ApplySearch(String pstrWorkspace, String pstrFormID, String[] parrData, AsyncCallback<DynaGridResponse> callback);
	void ApplySort(String pstrWorkspace, int[] parrOrder, AsyncCallback<DynaGridResponse> callback);
	void GetRow(String pstrWorkspace, int plngRow, AsyncCallback<DataObject> callback);
	void NewRow(String pstrWorkspace, AsyncCallback<DynaGridResponse> callback);
	void DoAction(String pstrWorkspace, int plngRow, int plngOrder, int plngAction, DataObject pobjData,
			AsyncCallback<GridActionResponse> callback);
	void SaveRow(String pstrWorkspace, int plngRow, DataObject pobjData, AsyncCallback<DynaGridSaveResponse> callback);
	void DeleteRow(String pstrWorkspace, int plngRow, AsyncCallback<DynaGridResponse> callback);
	void CloseQuery(String pstrWorkspace, AsyncCallback<String> callback);
}
