package Jewel.Mobile.server;

import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

public class CommandSetServiceImpl
	extends EngineImplementor
	implements CommandSetService
{
	private static final long serialVersionUID = 1L;

	public CommandObj[] GetCommands(String pstrFormID)
		throws JewelMobileException
	{
		IForm lrefForm;
		IFormAction[] larrActions;
		int llngCount;
		CommandObj[] larrRes;
		int i;

		if ( Engine.getCurrentUser() == null )
			return null;

		if (pstrFormID == null)
			throw new JewelMobileException("Unexpected: unidentified Form in GetCommands.");

		try
		{
			lrefForm = Form.GetInstance(UUID.fromString(pstrFormID));
		}
		catch(Throwable e)
		{
        	throw new JewelMobileException(e.getMessage(), e);
		}

		larrActions = lrefForm.getActions();
		llngCount = larrActions.length;

		larrRes = new CommandObj[llngCount];
		for ( i = 0; i < llngCount; i++ )
		{
			larrRes[i] = new CommandObj();
			larrRes[i].mstrCaption = larrActions[i].getName();
		}

		return larrRes;
	}

}
