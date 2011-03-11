package Jewel.Mobile.shared;

import java.io.*;

public class MenuNodeObj
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	public String mstrText;
	public String mstrID;
	public String mstrNSpace;
	public boolean mbExpanded;
	public boolean mbConfirm;
	public MenuNodeObj[] marrChildren;
}
