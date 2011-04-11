package Jewel.Mobile.interfaces;

import Jewel.Mobile.shared.*;

import com.google.gwt.user.client.rpc.*;

public interface GridServiceAsync
{
	void OpenQuery(String pstrQueryID, String pstrNameSpace, boolean pbForceParam, String pstrParam,
			String pstrFormID, ParamInfo[] parrExtParams, String pstrInitValue, AsyncCallback<GridResponse> callback);
	void SetPageSize(String pstrWorkspace, int plngPageSize, AsyncCallback<GridResponse> callback);
	void PageForward(String pstrWorkspace, AsyncCallback<GridResponse> callback);
	void PageBack(String pstrWorkspace, AsyncCallback<GridResponse> callback);
	void PageFirst(String pstrWorkspace, AsyncCallback<GridResponse> callback);
	void PageLast(String pstrWorkspace, AsyncCallback<GridResponse> callback);
	void ForceRefresh(String pstrWorkspace, boolean pbForceParam, AsyncCallback<GridResponse> callback);
	void ReloadAt(String pstrWorkspace, boolean pbForceParam, String pstrParam, AsyncCallback<GridResponse> callback);
	void ApplySearch(String pstrWorkspace, String pstrFormID, String[] parrData, AsyncCallback<GridResponse> callback);
	void ApplySort(String pstrWorkspace, int[] parrOrder, AsyncCallback<GridResponse> callback);
	void GetRow(String pstrWorkspace, int plngRow, AsyncCallback<DataObject> callback);
	void NewRow(String pstrWorkspace, AsyncCallback<GridResponse> callback);
	void DoAction(String pstrWorkspace, int plngRow, int plngOrder, int plngAction, DataObject pobjData,
			AsyncCallback<GridActionResponse> callback);
	void SaveRow(String pstrWorkspace, int plngRow, DataObject pobjData, AsyncCallback<GridSaveResponse> callback);
	void DeleteRow(String pstrWorkspace, int plngRow, AsyncCallback<GridResponse> callback);
	void CloseQuery(String pstrWorkspace, AsyncCallback<String> callback);
}
