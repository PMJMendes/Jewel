package Jewel.Web.server;

import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

public class ValueServiceImpl
	extends EngineImplementor
	implements ValueService
{
	private static final long serialVersionUID = 1L;

	public String GetDisplayText(String pstrEntity, String pstrKey)
		throws JewelWebException
	{
		if ( Engine.getCurrentUser() == null )
			return null;

		try
		{
			return Engine.GetWorkInstance(UUID.fromString(pstrEntity), UUID.fromString(pstrKey)).getLabel();
		}
		catch (JewelEngineException e)
		{
			return "";
		}
		catch (Throwable e)
		{
			throw new JewelWebException(e.getMessage(), e);
		}
	}
}
