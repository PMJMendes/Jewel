package Jewel.Web.server;

import java.util.*;

import Jewel.Engine.Engine;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

public class DynaSearchServiceImpl
	extends EngineImplementor
	implements DynaSearchService
{
	private static final long serialVersionUID = 1L;

	public String GetQueryID(String pstrFormID)
		throws JewelWebException
	{
		if ( Engine.getCurrentUser() == null )
			return null;

		try
		{
			return Form.GetInstance(UUID.fromString(pstrFormID)).getResultsQuery().getKey().toString();
		}
		catch (JewelEngineException e)
		{
			throw new JewelWebException(e.getMessage(), e);
		}
	}
}
