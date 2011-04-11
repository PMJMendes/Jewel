package Jewel.Mobile.server;

import java.util.*;

import Jewel.Engine.Engine;
import Jewel.Engine.Implementation.*;
import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

public class LookupServiceImpl
	extends EngineImplementor
	implements LookupService
{
	private static final long serialVersionUID = 1L;

	public LookupResponse OpenPopup(String pstrEntity)
		throws JewelMobileException
	{
		LookupResponse lobjRes;
		Entity lrefEntity;

		if ( Engine.getCurrentUser() == null )
			return null;

		lobjRes = new LookupResponse();
		try
		{
			lrefEntity = Entity.GetInstance(UUID.fromString(pstrEntity));
			lobjRes.mstrFormID = lrefEntity.getDefaultSearchForm().getKey().toString();
			lobjRes.mstrNSpaceID = lrefEntity.getMemberOf().getKey().toString();
			lobjRes.mstrFormName = lrefEntity.getDefaultSearchForm().getName();
			lobjRes.mstrNSpaceName = lrefEntity.getMemberOf().getName();
			lobjRes.mstrQueryID = Form.GetInstance(UUID.fromString(lobjRes.mstrFormID)).getResultsQuery().getKey().toString();
		}
		catch (Throwable e)
		{
			throw new JewelMobileException(e.getMessage(), e);
		}

		return lobjRes;
	}

	public LookupResponse OpenPopup(String pstrFormID, String pstrNameSpace)
		throws JewelMobileException
	{
		LookupResponse lobjRes;

		if ( Engine.getCurrentUser() == null )
			return null;

		lobjRes = new LookupResponse();
		try
		{
			lobjRes.mstrFormName = Form.GetInstance(UUID.fromString(pstrFormID)).getName();
			lobjRes.mstrNSpaceName = NameSpace.GetInstance(UUID.fromString(pstrNameSpace)).getName();
			lobjRes.mstrQueryID = Form.GetInstance(UUID.fromString(pstrFormID)).getResultsQuery().getKey().toString();
		}
		catch (Throwable e)
		{
			throw new JewelMobileException(e.getMessage(), e);
		}

		return lobjRes;
	}
}
