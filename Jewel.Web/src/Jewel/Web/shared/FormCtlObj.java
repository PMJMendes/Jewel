package Jewel.Web.shared;

import java.io.Serializable;

public class FormCtlObj
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final int TEXTBOX = 0;
	public static final int INTBOX = 1;
	public static final int DECBOX = 2;
	public static final int PWDBOX = 3;
	public static final int LABELBOX = 4;
	public static final int LOOKUP = 5;
	public static final int VALUELOOKUP = 6;
	public static final int CALENDAR = 7;
	public static final int FILEXFER = 8;
	public static final int TRICHECKBOX = 9;
	public static final int DROPDOWN = 10;

	public int mlngType;
	public boolean mbCanBeNull;
	public int mlngRow;
	public int mlngColumn;
	public String mstrCaption;
	public int mlngRowSpan;
	public int mlngColSpan;
	public String mstrParamTag;
	public String mstrFormID;
	public String mstrObjID;
	public String mstrDefault;
}
