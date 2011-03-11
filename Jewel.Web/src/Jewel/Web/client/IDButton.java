package Jewel.Web.client;

import com.google.gwt.user.client.ui.*;

public class IDButton
	extends Button 
{
	private int mlngID;

	public IDButton(int plngID)
	{
		mlngID = plngID;
	}

	public int getID()
	{
		return mlngID;
	}
}
