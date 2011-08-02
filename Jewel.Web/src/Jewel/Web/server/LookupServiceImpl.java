package Jewel.Web.server;

import java.util.*;

import Jewel.Engine.Engine;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.IForm;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

public class LookupServiceImpl
	extends EngineImplementor
	implements LookupService
{
	private static final long serialVersionUID = 1L;

	public LookupResponse OpenPopup(String pstrEntity)
		throws JewelWebException
	{
		LookupResponse lobjRes;
		Entity lrefEntity;
		IForm lrefForm;

		if ( Engine.getCurrentUser() == null )
			return null;

		lobjRes = new LookupResponse();
		try
		{
			lrefEntity = Entity.GetInstance(UUID.fromString(pstrEntity));
			lrefForm = lrefEntity.getDefaultSearchForm();

			if ( lrefForm == null )
				throw new JewelWebException("Object does not have default search form.");

			lobjRes.mstrFormID = lrefForm.getKey().toString();
			lobjRes.mstrNSpaceID = lrefEntity.getMemberOf().getKey().toString();
			lobjRes.mstrFormName = lrefForm.getName();
			lobjRes.mstrNSpaceName = lrefEntity.getMemberOf().getName();
		}
		catch (Throwable e)
		{
			throw new JewelWebException(e.getMessage(), e);
		}

		return lobjRes;
	}

	public LookupResponse OpenPopup(String pstrFormID, String pstrNameSpace)
		throws JewelWebException
	{
		LookupResponse lobjRes;

		if ( Engine.getCurrentUser() == null )
			return null;

		lobjRes = new LookupResponse();
		try
		{
			lobjRes.mstrFormName = Form.GetInstance(UUID.fromString(pstrFormID)).getName();
			lobjRes.mstrNSpaceName = NameSpace.GetInstance(UUID.fromString(pstrNameSpace)).getName();
		}
		catch (Throwable e)
		{
			throw new JewelWebException(e.getMessage(), e);
		}

		return lobjRes;
	}
}
