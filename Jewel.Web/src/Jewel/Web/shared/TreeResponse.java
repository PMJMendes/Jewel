package Jewel.Web.shared;

import java.io.*;

public class TreeResponse
	implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final int NONE=0;
	public static final int SEARCH=1;
	public static final int FORM=2;
	public static final int REPORT=3;

	public String mstrTitle;
	public String mstrID;
	public String mstrNSpace;
	public int mlngType;
}
