package Jewel.Mobile.server;

import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.Constants.FieldTypeGUIDs;
import Jewel.Engine.Constants.TypeDefGUIDs;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Implementation.Form;
import Jewel.Engine.Interfaces.IForm;
import Jewel.Engine.Interfaces.IFormField;
import Jewel.Engine.Interfaces.IObjMember;
import Jewel.Engine.Interfaces.ITypeDef;
import Jewel.Mobile.interfaces.SimpleFormService;
import Jewel.Mobile.shared.FormCtlObj;
import Jewel.Mobile.shared.JewelMobileException;

public class SimpleFormServiceImpl
	extends EngineImplementor
	implements SimpleFormService
{
	private static final long serialVersionUID = 1L;

	public FormCtlObj[] GetControls(String pstrFormID, String pstrNameSpace)
		throws JewelMobileException
	{
			UUID lidNameSpace;
			IForm lrefForm;
			IFormField[] larrFields;
			int llngCount;
			FormCtlObj[] larrRes;
			int i;

			if ( Engine.getCurrentUser() == null )
				return null;

			if (pstrFormID == null)
				throw new JewelMobileException("Unexpected: unidentified Form in GetControls.");

			lidNameSpace = UUID.fromString(pstrNameSpace);

			try
			{
				lrefForm = Form.GetInstance(UUID.fromString(pstrFormID));
			}
			catch(Throwable e)
			{
	        	throw new JewelMobileException(e.getMessage(), e);
			}

			larrFields = lrefForm.getFields();
			llngCount = larrFields.length;

			larrRes = new FormCtlObj[llngCount];
			for ( i = 0; i < llngCount; i++ )
			{
				larrRes[i] = BuildControl(larrFields[i], lidNameSpace);
				larrRes[i].mstrCaption = larrFields[i].getLabel();
				larrRes[i].mstrParamTag = larrFields[i].getParamTag();
			}

			return larrRes;
	}

	private FormCtlObj BuildControl(IFormField prefField, UUID pidNameSpace)
		throws JewelMobileException
	{
		FormCtlObj lobjAux;

		if (FieldTypeGUIDs.FT_Default.equals(prefField.getType()))
        {
            if (prefField.getObjMemberRef() == null)
                return null;

			return BuildDefaultControl(prefField, pidNameSpace);
        }

        if (FieldTypeGUIDs.FT_TextBox.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.TEXTBOX;
        	lobjAux.mstrDefault = prefField.getDefaultText();
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_DateBox.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.CALENDAR;
        	lobjAux.mstrDefault = prefField.getDefaultText();
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_Integer.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.INTBOX;
        	lobjAux.mstrDefault = prefField.getDefaultText();
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_Decimal.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.DECBOX;
        	lobjAux.mstrDefault = prefField.getDefaultText();
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_Bool.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.BOOLDROPDOWN;
        	lobjAux.mstrDefault = prefField.getDefaultText();
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_Lookup.equals(prefField.getType()))
        {
        	UUID lidForm;
        	IObjMember lidObj;

        	lidForm = prefField.getSearchForm();
        	lidObj = prefField.getObjMemberRef();

    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.LOOKUP;
        	lobjAux.mstrFormID = prefField.getSearchForm().toString();

        	try
        	{
				lobjAux.mstrDefault = prefField.getDefaultValue();
				if ( lobjAux.mstrDefault != null )
					lobjAux.mstrDefault = lobjAux.mstrDefault + "!" +
							(lidForm == null ?
									(((lidObj == null) || (lidObj.getRefersToObj() == null)) ? "{value}" :
											Engine.GetWorkInstance(
													Engine.FindEntity(pidNameSpace, prefField.getObjMemberRef().getRefersToObj()),
													UUID.fromString(lobjAux.mstrDefault)).getLabel()) :
									Engine.GetWorkInstance(
											Engine.FindEntity(pidNameSpace, Form.GetInstance(lidForm).getEditedObject().getKey()),
											UUID.fromString(lobjAux.mstrDefault)).getLabel());
			}
        	catch (Throwable e)
        	{
        		throw new JewelMobileException(e.getMessage(), e);
			}

        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_File.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.FILEXFER;
        	lobjAux.mstrDefault = null;
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_Passwd.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.PWDBOX;
        	lobjAux.mstrDefault = null;
        	return lobjAux;
        }

		return null;
	}
	
	private FormCtlObj BuildDefaultControl(IFormField prefField, UUID pidNameSpace)
		throws JewelMobileException
	{
        IObjMember lrefMember;
		FormCtlObj lobjAux;
		IForm lrefSearch;

		lrefMember = prefField.getObjMemberRef();
		lobjAux = PrepDefaultControl(lrefMember.getTypeDefRef(), prefField.getDefaultText(), prefField.getDefaultValue());
		if ( lobjAux == null )
			return null;
		lobjAux.mbCanBeNull = lrefMember.getCanBeNull();
		if (lobjAux.mlngType == FormCtlObj.LOOKUP)
		{
			try
			{
				lrefSearch = Entity.GetInstance(Engine.FindEntity(pidNameSpace, lrefMember.getRefersToObj())).getDefaultSearchForm();
				if ( lrefSearch != null )
				{
					if ( lobjAux.mstrDefault != null )
						lobjAux.mstrDefault = lobjAux.mstrDefault + "!" +
								Engine.GetWorkInstance(
										Engine.FindEntity(pidNameSpace,
												Form.GetInstance(lrefSearch.getKey()).getEditedObject().getKey()),
										UUID.fromString(lobjAux.mstrDefault)).getLabel();
					lobjAux.mstrFormID = lrefSearch.getKey().toString();
				}
				else
					if ( lobjAux.mstrDefault != null )
						lobjAux.mstrDefault = lobjAux.mstrDefault + "!" +
								Engine.GetWorkInstance(
										Engine.FindEntity(pidNameSpace, lrefMember.getRefersToObj()),
										UUID.fromString(lobjAux.mstrDefault)).getLabel();
			}
			catch (Throwable e)
			{
				throw new JewelMobileException(e.getMessage(), e);
			}
		}

		return lobjAux;
	}

	private FormCtlObj PrepDefaultControl(ITypeDef prefType, String pstrDefaultText, String pstrDefaultValue)
	{
		FormCtlObj lobjAux;

        if (TypeDefGUIDs.T_String.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.TEXTBOX;
        	lobjAux.mstrDefault = pstrDefaultText;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Integer.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.INTBOX;
        	lobjAux.mstrDefault = pstrDefaultText;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Decimal.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.DECBOX;
        	lobjAux.mstrDefault = pstrDefaultText;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Passwd.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.PWDBOX;
        	lobjAux.mstrDefault = null;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Boolean.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.BOOLDROPDOWN;
        	lobjAux.mstrDefault = pstrDefaultText;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_ObjRef.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.LOOKUP;
        	lobjAux.mstrDefault = pstrDefaultValue;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_ValueRef.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.VALUELOOKUP;
        	lobjAux.mstrDefault = pstrDefaultValue;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Date.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.CALENDAR;
        	lobjAux.mstrDefault = pstrDefaultText;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Binary.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.FILEXFER;
        	lobjAux.mstrDefault = null;
        	return lobjAux;
        }

        return null;
	}
}
