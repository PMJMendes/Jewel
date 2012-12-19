package Jewel.Mobile.shared;

import java.io.*;

public class GridResponse
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	public String mstrWorkspaceID;
	public QueryColumnObj[] marrColumns;
	public int mlngCurrRow;
	public int mlngCurrPage;
	public int mlngPageSize;
	public int mlngPageCount;
	public int mlngRecCount;
	public String[][] marrData;
	public int[] marrRows;
	public String mstrEditorID;
	public boolean mbReadOnly;
	public boolean mbCanCreate;
	public boolean mbCanEdit;
	public boolean mbCanDelete;
}
