package Jewel.Mobile.server;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.Constants.FieldTypeGUIDs;
import Jewel.Engine.Implementation.Form;
import Jewel.Engine.Interfaces.IForm;
import Jewel.Engine.Interfaces.IFormField;
import Jewel.Engine.Interfaces.IObject;
import Jewel.Engine.Security.Password;
import Jewel.Engine.SysObjects.FileXfer;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Mobile.shared.JewelMobileException;
import Jewel.Mobile.shared.ParamInfo;

public class FormDataBridge
{
	public static int[] GetSearchIndexes(UUID pidForm, String[] parrData)
		throws JewelMobileException
	{
	    IForm lrefForm;
		IFormField[] larrFields;
	    IObject lrefObject;
	    ArrayList<Integer> larrMembers;
		int llngCount;
		int i, j;
		int[] larrAux;

		if ( pidForm == null )
			return new int[0];

	    try
	    {
			lrefForm = Form.GetInstance(pidForm);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelMobileException(e.getMessage(), e);
		}

	    lrefObject = lrefForm.getEditedObject();
	    if (lrefObject == null)
	        return new int[0];

		larrFields = lrefForm.getFields();
		llngCount = larrFields.length;

		larrMembers = new ArrayList<Integer>();

	    for (i = 0; i < llngCount; i++)
	    {
	        j = lrefObject.MemberByNOrd(larrFields[i].getMemberNumber());
	        if ((j > -1) && (parrData[i] != null) && !parrData[i].equals(""))
	        	larrMembers.add(j);
	    }

	    larrAux = new int[larrMembers.size()];
	    for (i = 0; i < larrMembers.size(); i++)
	    	larrAux[i] = larrMembers.get(i);
	    return larrAux;
	}

	public static java.lang.Object[] GetSearchData(UUID pidForm, String[] parrData)
		throws JewelMobileException
	{
		IForm lrefForm;
		IFormField[] larrFields;
	    IObject lrefObject;
		int llngCount;
		ArrayList<java.lang.Object> larrRes;
		int i, j;

		try
		{
			lrefForm = Form.GetInstance(pidForm);
		}
		catch(Throwable e)
		{
	    	throw new JewelMobileException(e.getMessage(), e);
		}

	    lrefObject = lrefForm.getEditedObject();
	    if (lrefObject == null)
	        return new java.lang.Object[0];

		larrFields = lrefForm.getFields();
		llngCount = larrFields.length;

		larrRes = new ArrayList<java.lang.Object>();

		for ( i = 0; i < llngCount; i++ )
		{
	        j = lrefObject.MemberByNOrd(larrFields[i].getMemberNumber());
	        if ((j > -1) && (parrData[i] != null) && !parrData[i].equals(""))
				try
	        	{
					larrRes.add(ParseValue(larrFields[i], parrData[i]));
				}
				catch(Throwable e)
				{
		        	throw new JewelMobileException(e.getMessage(), e);
				}
		}

		return larrRes.toArray();
	}

	public static int[] GetParamIndexes(UUID pidForm, String[] parrData)
		throws JewelMobileException
	{
	    IForm lrefForm;
		IFormField[] larrFields;
	    ArrayList<Integer> larrMembers;
		int llngCount;
		int i;
		int[] larrAux;

		if ( pidForm == null )
			return new int[0];

	    try
	    {
			lrefForm = Form.GetInstance(pidForm);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelMobileException(e.getMessage(), e);
		}

		larrFields = lrefForm.getFields();
		llngCount = larrFields.length;

		larrMembers = new ArrayList<Integer>();

	    for (i = 0; i < llngCount; i++)
	    {
	        if ((parrData[i] != null) && !parrData[i].equals(""))
	        	larrMembers.add(larrFields[i].getMemberNumber());
	    }

	    larrAux = new int[larrMembers.size()];
	    for (i = 0; i < larrMembers.size(); i++)
	    	larrAux[i] = larrMembers.get(i);
	    return larrAux;
	}

	public static java.lang.Object[] GetParamData(UUID pidForm, String[] parrData)
		throws JewelMobileException
	{
		IForm lrefForm;
		IFormField[] larrFields;
		int llngCount;
		ArrayList<java.lang.Object> larrRes;
		int i;

		try
		{
			lrefForm = Form.GetInstance(pidForm);
		}
		catch(Throwable e)
		{
	    	throw new JewelMobileException(e.getMessage(), e);
		}

		larrFields = lrefForm.getFields();
		llngCount = larrFields.length;

		larrRes = new ArrayList<java.lang.Object>();

		for ( i = 0; i < llngCount; i++ )
		{
	        if ((parrData[i] != null) && !parrData[i].equals(""))
				try
	        	{
					larrRes.add(ParseValue(larrFields[i], parrData[i]));
				}
				catch(Throwable e)
				{
		        	throw new JewelMobileException(e.getMessage(), e);
				}
		}

		return larrRes.toArray();
	}

	public static java.lang.Object[] ParseData(UUID pidForm, String[] parrData)
		throws JewelMobileException
	{
		IForm lrefForm;
		IFormField[] larrFields;
		int llngCount;
		java.lang.Object[] larrRes;
		int i;

		try
		{
			lrefForm = Form.GetInstance(pidForm);
		}
		catch(Throwable e)
		{
	    	throw new JewelMobileException(e.getMessage(), e);
		}

		larrFields = lrefForm.getFields();
		llngCount = larrFields.length;

		larrRes = new java.lang.Object[llngCount];
		for ( i = 0; i < llngCount; i++ )
		{
			try
			{
				larrRes[i] = ParseValue(larrFields[i], parrData[i]);
			}
			catch(Throwable e)
			{
	        	throw new JewelMobileException(e.getMessage(), e);
			}
		}

		return larrRes;
	}

	public static String[] BuildData(UUID pidForm, java.lang.Object[] parrData, UUID pidNSpace)
		throws JewelMobileException
	{
		IForm lrefForm;
		IFormField[] larrFields;
		int llngCount;
		String[] larrRes;
		int i;

		try
		{
			lrefForm = Form.GetInstance(pidForm);
		}
		catch(Throwable e)
		{
	    	throw new JewelMobileException(e.getMessage(), e);
		}

		larrFields = lrefForm.getFields();
		llngCount = larrFields.length;

		larrRes = new String[llngCount];
		for ( i = 0; i < llngCount; i++ )
		{
			try
			{
				larrRes[i] = BuildValue(larrFields[i], parrData[i], pidNSpace);
			}
			catch(Throwable e)
			{
	        	throw new JewelMobileException(e.getMessage(), e);
			}
		}

		return larrRes;
	}

	public static void WriteToObject(UUID pidForm, ObjectBase prefDest, String[] parrData)
		throws JewelMobileException
	{
		IForm lrefForm;
		IObject lrefObject;
		IFormField[] larrFields;
		int llngCount;
		int i, j, k;

		try
		{
			lrefForm = Form.GetInstance(pidForm);
		}
		catch(Throwable e)
		{
	    	throw new JewelMobileException(e.getMessage(), e);
		}
	    lrefObject = lrefForm.getEditedObject();
		larrFields = lrefForm.getFields();
		llngCount = larrFields.length;

		for ( i = 0; i < llngCount; i++ )
		{
	        j = larrFields[i].getMemberNumber();
	        k = lrefObject.MemberByNOrd(j);
			if ( k >= 0 )
			{
				try
				{
					prefDest.setAt(k, ParseValue(larrFields[i], parrData[i]));
				}
				catch(Throwable e)
				{
		        	throw new JewelMobileException(e.getMessage(), e);
				}
			}
		}
	}

	public static String[] ReadFromObject(UUID pidForm, ObjectBase prefSource, UUID pidNSpace)
		throws JewelMobileException
	{
		IForm lrefForm;
		IObject lrefObject;
		IFormField[] larrFields;
		String[] larrRes;
		int llngCount;
		int i, j, k;

		try
		{
			lrefForm = Form.GetInstance(pidForm);
		}
		catch(Throwable e)
		{
	    	throw new JewelMobileException(e.getMessage(), e);
		}
	    lrefObject = lrefForm.getEditedObject();
		larrFields = lrefForm.getFields();
		llngCount = larrFields.length;

		larrRes = new String[llngCount];
		for ( i = 0; i < llngCount; i++ )
		{
	        j = larrFields[i].getMemberNumber();
	        k = lrefObject.MemberByNOrd(j);
	        if (k == -1)
	        	larrRes[i] = null;
	        else
	        {
				try
				{
					larrRes[i] = BuildValue(larrFields[i], prefSource.getAt(k), pidNSpace);
				}
				catch(Throwable e)
				{
		        	throw new JewelMobileException(e.getMessage(), e);
				}
	        }
		}

		return larrRes;
	}

	public static java.lang.Object[] GetNonObjectParams(UUID pidForm, java.lang.Object[] parrObjParams, String[] parrData)
		throws JewelMobileException
	{
		IForm lrefForm;
		IObject lrefObject;
		IFormField[] larrFields;
	    java.lang.Object[] larrAux;
		int llngCount;
	    int llngMaxLen;
		int i, j, k, l;

		try
		{
			lrefForm = Form.GetInstance(pidForm);
		}
		catch(Throwable e)
		{
	    	throw new JewelMobileException(e.getMessage(), e);
		}
	    lrefObject = lrefForm.getEditedObject();
		larrFields = lrefForm.getFields();
		llngCount = larrFields.length;

	    llngMaxLen = parrObjParams.length;
	    for (i = 0; i < llngCount; i++)
	    {
	        j = larrFields[i].getMemberNumber();
	        k = lrefObject.MemberByNOrd(j);
	        if (k == -1)
	            llngMaxLen++;
	    }

	    if (llngMaxLen == parrObjParams.length)
	        return parrObjParams;

	    larrAux = new java.lang.Object[llngMaxLen];
	    for (i = 0; i < parrObjParams.length; i++)
	        larrAux[i] = parrObjParams[i];
	    l = parrObjParams.length;
	    for (i = 0; i < llngCount; i++)
	    {
	        j = larrFields[i].getMemberNumber();
	        k = lrefObject.MemberByNOrd(j);
	        if (k == -1)
	        {
	            try
	            {
					larrAux[l] = ParseValue(larrFields[i], parrData[i]);
				}
	            catch (JewelEngineException e)
	            {
		        	throw new JewelMobileException(e.getMessage(), e);
				}
	            l++;
	        }
	    }
	    for (i = l; i < llngMaxLen; i++)
	        larrAux[i] = null;

	    return larrAux;
	}

	public static void SetNonObjectParams(UUID pidForm, java.lang.Object[] parrObjParams, String[] parrData, 
			java.lang.Object[] parrParams, UUID pidNSpace)
		throws JewelMobileException
	{
		IForm lrefForm;
		IObject lrefObject;
		IFormField[] larrFields;
		int llngCount;
        int llngMaxLen;
		int i, j, k, l;

		try
		{
			lrefForm = Form.GetInstance(pidForm);
		}
		catch(Throwable e)
		{
        	throw new JewelMobileException(e.getMessage(), e);
		}
        lrefObject = lrefForm.getEditedObject();
		larrFields = lrefForm.getFields();
		llngCount = larrFields.length;

        llngMaxLen = parrObjParams.length;
        for (i = 0; i < llngCount; i++)
        {
            j = larrFields[i].getMemberNumber();
            k = lrefObject.MemberByNOrd(j);
            if (k == -1)
                llngMaxLen++;
        }

        if (llngMaxLen == parrObjParams.length)
            return;

        l = parrObjParams.length;
        for (i = 0; i < llngCount; i++)
        {
            j = larrFields[i].getMemberNumber();
            k = lrefObject.MemberByNOrd(j);
            if (k == -1)
            {
                try
                {
                	parrData[i] = BuildValue(larrFields[i], parrParams[l], pidNSpace);
				}
                catch (JewelEngineException e)
                {
		        	throw new JewelMobileException(e.getMessage(), e);
				}
                l++;
            }
        }
	}

	private static java.lang.Object ParseValue(IFormField prefField, String pstrValue)
		throws JewelEngineException
	{
		if (FieldTypeGUIDs.FT_Default.equals(prefField.getType()))
	    {
	        if (prefField.getObjMemberRef() == null)
	            return null;

			return TypeDataBridge.ParseValue(prefField.getObjMemberRef().getTypeDefRef(), pstrValue);
	    }

		if ( (pstrValue == null) || (pstrValue.equals("")) )
			return null;

	    if (FieldTypeGUIDs.FT_TextBox.equals(prefField.getType()))
	    	return pstrValue;
        
        if (FieldTypeGUIDs.FT_LabelBox.equals(prefField.getType()))
        	return pstrValue;

	    if (FieldTypeGUIDs.FT_DateBox.equals(prefField.getType()))
	    	return Timestamp.valueOf(pstrValue);
	    
	    if ( FieldTypeGUIDs.FT_Integer.equals(prefField.getType()))
	    	return new Integer(pstrValue);

	    if (FieldTypeGUIDs.FT_Decimal.equals(prefField.getType()))
	    	return new BigDecimal(pstrValue);

	    if (FieldTypeGUIDs.FT_Bool.equals(prefField.getType()))
	    {
	    	if ( (pstrValue.equals("TRUE")) || (pstrValue.equals("True")) || (pstrValue.equals("1")) )
	    		return Boolean.TRUE;
	    	else
	    		return Boolean.FALSE;
	    }

	    if (FieldTypeGUIDs.FT_Lookup.equals(prefField.getType()))
	    	return UUID.fromString(pstrValue.split("!", 2)[0]);

	    if (FieldTypeGUIDs.FT_File.equals(prefField.getType()))
	    	return FileServiceImpl.GetFileXferStorage().get(UUID.fromString(pstrValue.split("!", 2)[0]));

	    if (FieldTypeGUIDs.FT_Passwd.equals(prefField.getType()))
	    	return new Password(pstrValue, false);

		return null;
	}

	private static String BuildValue(IFormField prefField, java.lang.Object pobjValue, UUID pidNSpace)
		throws JewelEngineException, JewelMobileException
	{
		FileXfer laux;
		UUID lidAux;

		if (FieldTypeGUIDs.FT_Default.equals(prefField.getType()))
	    {
	        if (prefField.getObjMemberRef() == null)
	            return null;

			return TypeDataBridge.BuildValue(prefField.getObjMemberRef(), pobjValue, pidNSpace);
	    }
	
		if ( pobjValue == null )
			return null;

	    if (FieldTypeGUIDs.FT_TextBox.equals(prefField.getType()))
	    	return (String)pobjValue;
        
        if (FieldTypeGUIDs.FT_LabelBox.equals(prefField.getType()))
        	return pobjValue.toString();

	    if (FieldTypeGUIDs.FT_DateBox.equals(prefField.getType()))
	    	return ((Timestamp)pobjValue).toString();

	    if (FieldTypeGUIDs.FT_Integer.equals(prefField.getType()))
	    	return ((Integer)pobjValue).toString();

	    if (FieldTypeGUIDs.FT_Decimal.equals(prefField.getType()))
	    	return ((BigDecimal)pobjValue).toPlainString();

	    if (FieldTypeGUIDs.FT_Bool.equals(prefField.getType()))
	    {
	    	if ( (Boolean)pobjValue )
	    		return "1";
	    	else
	    		return "0";
	    }

	    if (FieldTypeGUIDs.FT_Lookup.equals(prefField.getType()))
	    {
			try
	    	{
					return ((UUID)pobjValue).toString() + "!" +
							Engine.GetWorkInstance(Engine.FindEntity(pidNSpace,
							Form.GetInstance(prefField.getSearchForm()).getEditedObject().getKey()),
							(UUID)pobjValue).getLabel();
			}
	    	catch (Throwable e)
	    	{
	    		throw new JewelMobileException(e.getMessage(), e);
			}
	    }

	    if (FieldTypeGUIDs.FT_File.equals(prefField.getType()))
	    {
	    	if ( pobjValue instanceof FileXfer )
	    		laux = (FileXfer)pobjValue;
	    	else
	        	laux = new FileXfer((byte[])pobjValue);
	    	lidAux = UUID.randomUUID();
	    	FileServiceImpl.GetFileXferStorage().put(lidAux, laux);
			return lidAux.toString() + "!" + laux.getFileName();
	    }

	    if (FieldTypeGUIDs.FT_Passwd.equals(prefField.getType()))
	    	return "";

		throw new JewelMobileException("Unimplemented form field type in BuildValue.");
	}

	public static HashMap<String, java.lang.Object> ParseExtParams(UUID pidForm, ParamInfo[] parrData)
		throws JewelMobileException
	{
		IForm lrefForm;
		IFormField[] larrFields;
		HashMap<String, java.lang.Object> larrRes;
		int i;
		java.lang.Object lobjAux;

		try
		{
			lrefForm = Form.GetInstance(pidForm);
		}
		catch(Throwable e)
		{
	    	throw new JewelMobileException(e.getMessage(), e);
		}

		larrFields = lrefForm.getFields();

		larrRes = new HashMap<String, java.lang.Object>();
		for ( i = 0; i < parrData.length; i++ )
		{
			try
			{
				lobjAux = ParseValue(larrFields[parrData[i].mlngIndex], parrData[i].mstrValue);
				if ( lobjAux != null )
					larrRes.put(parrData[i].mstrTag, lobjAux);
			}
			catch(Throwable e)
			{
	        	throw new JewelMobileException(e.getMessage(), e);
			}
		}

		return larrRes;
	}
}
