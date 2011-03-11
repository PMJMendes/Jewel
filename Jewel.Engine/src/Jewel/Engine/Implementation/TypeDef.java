package Jewel.Engine.Implementation;

import java.math.*;
import java.sql.*;
import java.util.*;

import javax.sql.rowset.serial.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.Security.*;
import Jewel.Engine.SysObjects.*;

public class TypeDef
	extends ObjectBase
	implements ITypeDef
{
    public static TypeDef GetInstance(UUID pidKey)
    	throws JewelEngineException
    {
        return (TypeDef)Engine.GetCache(true).getAt(EntityGUIDs.E_TypeDef, pidKey);
    }

    public void Initialize()
    {
    }

    public String TypeForCreate()
    {
        return (String)getAt(2);
    }

    public String TranslateValue(java.lang.Object pobjValue, boolean pbForFilter, ArrayList<Blob> parrParams)
    	throws JewelEngineException
    {
        UUID lidAux;
        Timestamp ldtAux;

        if (pobjValue == null)
            return "NULL";

        if ((TypeDefGUIDs.T_ObjRef.equals(getKey())) || (TypeDefGUIDs.T_ValueRef.equals(getKey())))
        {
            if (pobjValue instanceof UUID)
                lidAux = (UUID)pobjValue;
            else
                lidAux = UUID.fromString((String)pobjValue);

            return "'" + lidAux.toString() + "'";
        }

        if (TypeDefGUIDs.T_String.equals(getKey()))
        {
            if (pbForFilter)
                if ((((String)pobjValue).length() > 0) && (((String)pobjValue).charAt(0) == '!'))
                    return "N'" + ((String)pobjValue).substring(1).replace("'", "''") + "'";
                else
                    return "N'%" + ((String)pobjValue).replace("'", "''") + "%'";
            else
                return "N'" + ((String)pobjValue).replace("'", "''") + "'";
        }

        if (TypeDefGUIDs.T_Boolean.equals(getKey()))
        {
            if (pobjValue instanceof Boolean)
            {
                if ((Boolean)pobjValue)
                    return "1";
                else
                    return "0";
            }

            if ((((String)pobjValue).equals("True")) || (((String)pobjValue).equals("1")))
                return "1";
            else
                return "0";
        }

        if (TypeDefGUIDs.T_Integer.equals(getKey()))
        {
            if (pobjValue instanceof Integer)
                return ((Integer)pobjValue).toString();

            return (Integer.valueOf((String)pobjValue)).toString();
        }

        if (TypeDefGUIDs.T_Decimal.equals(getKey()))
        {
            if (pobjValue instanceof BigDecimal)
                return ((BigDecimal)pobjValue).toString();

            return (new BigDecimal((String)pobjValue)).toString();
        }

        if (TypeDefGUIDs.T_Date.equals(getKey()))
        {
            if (pobjValue instanceof Timestamp)
                ldtAux = (Timestamp)pobjValue;
            else
                ldtAux = Timestamp.valueOf((String)pobjValue);

            if (pbForFilter && (ldtAux.equals(Timestamp.valueOf(ldtAux.toString().substring(0, 10)))))
                return "'" + ldtAux.toString().substring(0, 10) + "%'";

            return "'" + ldtAux.toString() + "'";
        }

        if (TypeDefGUIDs.T_Passwd.equals(getKey()))
            return "N'" + ((Password)pobjValue).GetEncrypted().replace("'", "''") + "'";

        if (TypeDefGUIDs.T_Binary.equals(getKey()))
        {
        	if (pbForFilter)
        		return "NOT NULL";

            if (parrParams == null)
                return "NULL";

            try
            {
				parrParams.add(new SerialBlob(((FileXfer)pobjValue).GetVarData()));
			}
            catch (Throwable e)
            {
            	throw new JewelEngineException("Unexpected exception building Blob in TypeDef TranslateValue", e);
			}
            return "?";
        }

        return null;
    }
}
