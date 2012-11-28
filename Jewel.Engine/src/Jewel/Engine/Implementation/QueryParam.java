package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class QueryParam
	extends ObjectBase
	implements IQueryParam
{
    public static QueryParam GetInstance(UUID pidKey)
    	throws JewelEngineException
	{
        return (QueryParam)Engine.GetCache(true).getAt(EntityGUIDs.E_QueryParam, pidKey);
	}

    public static QueryParam GetInstance(ResultSet prsObject)
    	throws SQLException, JewelEngineException
	{
        return (QueryParam)Engine.GetCache(true).getAt(EntityGUIDs.E_QueryParam, prsObject);
	}

	public void Initialize()
	{
	}

    public String ColumnForFiltering(String pstrSeparator, HashMap<String, java.lang.Object> parrValues)
    	throws JewelEngineException
    {
    	StringTokenizer lobjTokens;
    	String lstrToken, lstrPrev;
        int i;
        ITypeDef lrefType;
        java.lang.Object lobjParam;
        StringBuilder lbldr;

        lobjTokens = new StringTokenizer((String)getAt(2), "=+-*()^)", true);

        lbldr = new StringBuilder(" " + pstrSeparator + " ");

        lstrPrev = "";
        i = 0;
        while ( lobjTokens.hasMoreTokens() )
        {
        	lstrToken = lobjTokens.nextToken();
            if (lstrToken.equals("") || lstrToken.equals("@") || lstrToken.equals("@p"))
                continue;

            if (lstrToken.charAt(0) == '@')
            {
                lrefType = (ITypeDef)TypeDef.GetInstance((UUID)getAt(3));

                switch (lstrToken.charAt(1))
                {
                    case 'v':
                    	lstrToken = lrefType.TranslateValue(getAt(5), true, null);
                        break;

                    case 't':
                    	lstrToken = lrefType.TranslateValue(getAt(7), true, null);
                        break;

                    case 'u':
                    	lstrToken = lrefType.TranslateValue(Engine.getCurrentUser(), true, null);
                        break;

                    case 'p':
                        if (parrValues == null)
                            return " " + pstrSeparator + "1=1";
                        lobjParam = parrValues.get(lstrToken.substring(2));
                        if (lobjParam == null)
                            return " " + pstrSeparator + "1=1";
                        lstrToken = lrefType.TranslateValue(lobjParam, true, null);
                        break;
                }

                if (i > 0 && lstrPrev.equals("="))
                {
                    if ( (lstrToken.equals("NULL")) || (lstrToken.equals("NOT NULL")) )
                        lstrPrev = " IS ";
                    else
                        if (TypeDefGUIDs.T_String.equals(lrefType.getKey()))
                            lstrPrev = " LIKE ";
                        else
                            lstrPrev = " = ";
                }
            }

            lbldr.append(lstrPrev);
            lstrPrev = lstrToken;
            i++;
        }
        lbldr.append(lstrPrev);

        return lbldr.toString();
    }

    public int getParamAppliesTo()
    {
        return (Integer)getAt(4);
    }

    public java.lang.Object getParamValue()
    	throws JewelEngineException
    {
        ITypeDef lrefType;

        lrefType = (ITypeDef)TypeDef.GetInstance((UUID)getAt(3));

        if ((TypeDefGUIDs.T_ObjRef.equals(lrefType.getKey())) || (TypeDefGUIDs.T_ValueRef.equals(lrefType.getKey())))
            return getAt(5);

        if ((TypeDefGUIDs.T_String.equals(lrefType.getKey())) && (getAt(7) instanceof String) && (((String)getAt(7)).charAt(0) == '!'))
            return ((String)getAt(7)).substring(1);

        return getAt(7);
    }
}
