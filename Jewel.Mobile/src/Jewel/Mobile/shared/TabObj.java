package Jewel.Mobile.shared;

import java.io.*;

public class TabObj
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	public static final int FORMTAB = 0;
	public static final int GRIDTAB = 1;

	public int mlngType;
	public String mstrCaption;
	public String mstrID;
}
