package Jewel.Mobile.server;

import java.lang.reflect.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

public class SingleFormServiceImpl
	extends EngineImplementor
	implements SingleFormService
{
	private static final long serialVersionUID = 1L;

	public CommandResponse DoCommand(int plngID, String pstrFormID, String pstrNameSpace, String[] parrData)
		throws JewelMobileException
	{
		UUID lidNameSpace, lidForm;
		java.lang.Object[] larrValues;
		IForm lrefForm;
    	CommandResponse laux;

		if ( Engine.getCurrentUser() == null )
			return null;

		if (pstrFormID == null)
			throw new JewelMobileException("Unexpected: unidentified Form in DoCommand.");

		if (pstrNameSpace == null)
			lidNameSpace = null;
		else
			lidNameSpace = UUID.fromString(pstrNameSpace);

		lidForm = UUID.fromString(pstrFormID);

		larrValues = FormDataBridge.ParseData(lidForm, parrData);

        try
        {
        	lrefForm = Form.GetInstance(lidForm);
        }
        catch(Throwable e)
        {
        	throw new JewelMobileException(e.getMessage(), e);
		}

        try
        {
        	lrefForm.getActions()[plngID].Run(lidNameSpace, larrValues);
		}
        catch (InvocationTargetException e)
        {
        	Throwable x;

        	x = e;
        	while( x.getCause() != null )
        		x = x.getCause();

        	laux = new CommandResponse();
        	laux.mstrResult = x.getMessage();
        	laux.marrData = null;
        	return laux;
		}
        catch(Throwable e)
        {
        	throw new JewelMobileException(e.getMessage(), e);
		}

    	laux = new CommandResponse();
    	laux.mstrResult = "";
    	laux.marrData = FormDataBridge.BuildData(lidForm, larrValues, lidNameSpace);
    	return laux;
	}

}
