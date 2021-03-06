package Jewel.Web.server;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.Constants.TypeDefGUIDs;
import Jewel.Engine.Interfaces.IObjMember;
import Jewel.Engine.Interfaces.ITypeDef;
import Jewel.Engine.Security.Password;
import Jewel.Engine.SysObjects.FileXfer;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Web.shared.JewelWebException;

public class TypeDataBridge
{
	public static java.lang.Object ParseValue(ITypeDef prefType, String pstrValue)
		throws JewelEngineException
	{
		if ( (pstrValue == null) || (pstrValue.equals("")) )
			return null;

        if (TypeDefGUIDs.T_String.equals(prefType.getKey()))
        	return pstrValue;

        if (TypeDefGUIDs.T_Integer.equals(prefType.getKey()))
        	return new Integer(pstrValue);

        if (TypeDefGUIDs.T_Decimal.equals(prefType.getKey()))
        	return new BigDecimal(pstrValue);

        if (TypeDefGUIDs.T_Boolean.equals(prefType.getKey()))
        {
        	if ( (pstrValue.equalsIgnoreCase("true")) || (pstrValue.equals("1")) )
        		return Boolean.TRUE;
        	else
        		return Boolean.FALSE;
        }

        if (TypeDefGUIDs.T_ObjRef.equals(prefType.getKey()))
        	return UUID.fromString(pstrValue.split("!", 2)[0]);

        if (TypeDefGUIDs.T_ValueRef.equals(prefType.getKey()))
        	return UUID.fromString(pstrValue);

        if (TypeDefGUIDs.T_Date.equals(prefType.getKey()))
        	return Timestamp.valueOf(pstrValue);

        if (TypeDefGUIDs.T_Binary.equals(prefType.getKey()))
        	return FileServiceImpl.GetFileXferStorage().get(UUID.fromString(pstrValue.split("!", 2)[0]));

        if (TypeDefGUIDs.T_Passwd.equals(prefType.getKey()))
        {
        	return new Password(pstrValue, false);
        }

        return null;
	}

	public static String BuildValue(IObjMember prefMember, java.lang.Object pobjValue, UUID pidNSpace)
		throws JewelWebException
	{
		ITypeDef lrefType;
		FileXfer laux;
		UUID lidAux;

		if ( pobjValue == null )
			return null;

		lrefType = prefMember.getTypeDefRef();

        if (TypeDefGUIDs.T_String.equals(lrefType.getKey()))
        	return (String)pobjValue;

        if (TypeDefGUIDs.T_Integer.equals(lrefType.getKey()))
        	return ((Integer)pobjValue).toString();

        if (TypeDefGUIDs.T_Decimal.equals(lrefType.getKey()))
        	return ((BigDecimal)pobjValue).toPlainString();

        if (TypeDefGUIDs.T_Boolean.equals(lrefType.getKey()))
        {
        	if ( (Boolean)pobjValue )
        		return "1";
        	else
        		return "0";
        }

        if (TypeDefGUIDs.T_ObjRef.equals(lrefType.getKey()))
        {
			try
        	{
				return ((UUID)pobjValue).toString() + "!" +
						Engine.GetWorkInstance(Engine.FindEntity(pidNSpace, prefMember.getRefersToObj()),
						(UUID)pobjValue).getLabel();
			}
        	catch (Throwable e)
        	{
        		throw new JewelWebException(e.getMessage(), e);
			}
        }

        if (TypeDefGUIDs.T_ValueRef.equals(lrefType.getKey()))
        	return ((UUID)pobjValue).toString();

        if (TypeDefGUIDs.T_Date.equals(lrefType.getKey()))
        	return ((Timestamp)pobjValue).toString();

        if (TypeDefGUIDs.T_Binary.equals(lrefType.getKey()))
        {
        	if ( pobjValue instanceof FileXfer )
        		laux = (FileXfer)pobjValue;
        	else
            	laux = new FileXfer((byte[])pobjValue);
        	lidAux = UUID.randomUUID();
        	FileServiceImpl.GetFileXferStorage().put(lidAux, laux);
    		return lidAux + "!" + laux.getFileName();
        }

        if (TypeDefGUIDs.T_Passwd.equals(lrefType.getKey()))
        	return "";

		throw new JewelWebException("Unimplemented object field type in TypeDataBridge.BuildValue.");
	}
}
