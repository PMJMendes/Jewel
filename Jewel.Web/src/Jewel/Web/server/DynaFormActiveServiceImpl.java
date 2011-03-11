package Jewel.Web.server;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import Jewel.Engine.Engine;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

public class DynaFormActiveServiceImpl
	extends DynaFormServiceImpl
	implements DynaFormActiveService
{
	private static final long serialVersionUID = 1L;

	public FormActionObj[] GetActions(String pstrFormID) throws JewelWebException
	{
		IForm lrefForm;
		IFormAction[] larrActions;
		int llngCount;
		FormActionObj[] larrRes;
		int i;

		if ( Engine.getCurrentUser() == null )
			return null;

		if (pstrFormID == null)
			throw new JewelWebException("Unexpected: unidentified Form in GetActions.");

		try
		{
			lrefForm = Form.GetInstance(UUID.fromString(pstrFormID));
		}
		catch(Throwable e)
		{
        	throw new JewelWebException(e.getMessage(), e);
		}

		larrActions = lrefForm.getActions();
		llngCount = larrActions.length;

		larrRes = new FormActionObj[llngCount];
		for ( i = 0; i < llngCount; i++ )
		{
			larrRes[i] = new FormActionObj();
			larrRes[i].mstrCaption = larrActions[i].getName();
		}

		return larrRes;
	}

	public FormActionResponse DoAction(int plngID, String pstrFormID, String pstrNameSpace, String[] parrData)
		throws JewelWebException
	{
		UUID lidNameSpace, lidForm;
		java.lang.Object[] larrValues;
		IForm lrefForm;
    	FormActionResponse laux;

		if ( Engine.getCurrentUser() == null )
			return null;

		if (pstrFormID == null)
			throw new JewelWebException("Unexpected: unidentified Form in DoAction.");

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
        	throw new JewelWebException(e.getMessage(), e);
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

        	laux = new FormActionResponse();
        	laux.mstrResult = x.getMessage();
        	laux.marrData = null;
        	return laux;
		}
        catch(Throwable e)
        {
        	throw new JewelWebException(e.getMessage(), e);
		}

    	laux = new FormActionResponse();
    	laux.mstrResult = "";
    	laux.marrData = FormDataBridge.BuildData(lidForm, larrValues, lidNameSpace);
    	return laux;
	}
}
