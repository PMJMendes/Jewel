package Jewel.Web.server;

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
import Jewel.Web.interfaces.DynaFormService;
import Jewel.Web.shared.FormCtlObj;
import Jewel.Web.shared.JewelWebException;

public class DynaFormServiceImpl
	extends EngineImplementor
	implements DynaFormService
{
	private static final long serialVersionUID = 1L;

	public FormCtlObj[] GetControls(String pstrFormID, String pstrNameSpace)
		throws JewelWebException
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
			throw new JewelWebException("Unexpected: unidentified Form in GetControls.");

		lidNameSpace = UUID.fromString(pstrNameSpace);

		try
		{
			lrefForm = Form.GetInstance(UUID.fromString(pstrFormID));
		}
		catch(Throwable e)
		{
        	throw new JewelWebException(e.getMessage(), e);
		}

		larrFields = lrefForm.getFields();
		llngCount = larrFields.length;

		larrRes = new FormCtlObj[llngCount];
		for ( i = 0; i < llngCount; i++ )
		{
			larrRes[i] = BuildControl(larrFields[i], lidNameSpace);
			larrRes[i].mlngColumn = larrFields[i].getColumn() - 1;
			larrRes[i].mlngRow = larrFields[i].getRow() - 1;
			larrRes[i].mstrCaption = larrFields[i].getLabel();
			larrRes[i].mlngRowSpan = larrFields[i].getHeight();
			larrRes[i].mlngColSpan = larrFields[i].getWidth();
			larrRes[i].mstrParamTag = larrFields[i].getParamTag();
		}

		return larrRes;
	}

	private FormCtlObj BuildControl(IFormField prefField, UUID pidNameSpace)
		throws JewelWebException
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
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_DateBox.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.CALENDAR;
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_Integer.equals(prefField.getType()))
        {
        	lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.INTBOX;
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_Decimal.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.DECBOX;
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_Bool.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.BOOLDROPDOWN;
        	return lobjAux;
        }
        
        if (FieldTypeGUIDs.FT_TriStateCheckbox.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.TRICHECKBOX;
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_Lookup.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.LOOKUP;
        	lobjAux.mstrFormID = prefField.getSearchForm().toString();
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_File.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.FILEXFER;
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_Passwd.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.PWDBOX;
        	return lobjAux;
        }

        if (FieldTypeGUIDs.FT_Value.equals(prefField.getType()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.VALUELOOKUP;
        	return lobjAux;
        }

		return null;
	}
	
	private FormCtlObj BuildDefaultControl(IFormField prefField, UUID pidNameSpace)
		throws JewelWebException
	{
        IObjMember lrefMember;
		FormCtlObj lobjAux;
		IForm lrefSearch;

		lrefMember = prefField.getObjMemberRef();
		lobjAux = PrepDefaultControl(lrefMember.getTypeDefRef());
		if ( lobjAux == null )
			return null;
		lobjAux.mbCanBeNull = lrefMember.getCanBeNull();
		if (lobjAux.mlngType == FormCtlObj.LOOKUP)
		{
			try
			{
				lrefSearch = Entity.GetInstance(Engine.FindEntity(pidNameSpace, lrefMember.getRefersToObj())).getDefaultSearchForm();
			}
			catch (Throwable e)
			{
				throw new JewelWebException(e.getMessage(), e);
			}
			if ( lrefSearch != null )
				lobjAux.mstrFormID = lrefSearch.getKey().toString();
		}

		return lobjAux;
	}

	private FormCtlObj PrepDefaultControl(ITypeDef prefType)
	{
		FormCtlObj lobjAux;

        if (TypeDefGUIDs.T_String.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.TEXTBOX;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Integer.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.INTBOX;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Decimal.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.DECBOX;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Passwd.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.PWDBOX;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Boolean.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.BOOLDROPDOWN;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_ObjRef.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.LOOKUP;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_ValueRef.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.VALUELOOKUP;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Date.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.CALENDAR;
        	return lobjAux;
        }

        if (TypeDefGUIDs.T_Binary.equals(prefType.getKey()))
        {
    		lobjAux = new FormCtlObj();
        	lobjAux.mlngType = FormCtlObj.FILEXFER;
        	return lobjAux;
        }

        return null;
	}
}
