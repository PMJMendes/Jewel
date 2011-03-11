package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class FormField
	extends ObjectBase
	implements IFormField
{
	IForm mrefOwner;
	IObjMember mrefDefault;

	public static FormField GetInstance(UUID pidKey)
		throws JewelEngineException
	{
		return (FormField)Engine.GetCache(true).getAt(EntityGUIDs.E_FormField, pidKey);
	}

	public static FormField GetInstance(ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
		return (FormField)Engine.GetCache(true).getAt(EntityGUIDs.E_FormField, prsObject);
	}

	public void Initialize()
		throws JewelEngineException
	{
        IObject lrefAux;
        int i;

		mrefOwner = (IForm)Form.GetInstance((UUID)getAt(0));

        lrefAux = mrefOwner.getEditedObject();
        if (lrefAux != null)
        {
            i = lrefAux.MemberByNOrd((Integer)getAt(5));
            if (i >= 0)
                mrefDefault = lrefAux.getMembers()[i];
        }
	}

	public String getLabel()
	{
		return (String)getAt(2);
	}

	public int getRow()
	{
		return (Integer)getAt(3);
	}

	public int getColumn()
	{
		return (Integer)getAt(4);
	}

	public int getWidth()
	{
		return (Integer)getAt(6);
	}

	public int getHeight()
	{
		return (Integer)getAt(7);
	}

	public int getMemberNumber()
	{
		return (Integer)getAt(5);
	}
	
	public UUID getType()
	{
		return (UUID)getAt(1);
	}

    public IObjMember getObjMemberRef()
    {
        return mrefDefault;
    }

    public String getParamTag()
    {
        if (getAt(9) instanceof String)
            return (String)getAt(9);

        return null;
    }

    public UUID getSearchForm()
    {
        return (UUID)getAt(8);
    }

    public UUID AuxEntity(UUID pidNSpace)
    	throws SQLException, JewelEngineException
    {
        if (getAt(6) == null)
            return null;

        return Engine.FindEntity(pidNSpace, (UUID)getAt(6));
    }
}
