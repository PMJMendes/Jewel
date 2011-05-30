package Jewel.Mobile.server;

import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Mobile.interfaces.ValueService;
import Jewel.Mobile.shared.JewelMobileException;

public class ValueServiceImpl
	extends EngineImplementor
	implements ValueService
{
	private static final long serialVersionUID = 1L;

	public String GetDisplayText(String pstrEntity, String pstrKey)
		throws JewelMobileException
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
			throw new JewelMobileException(e.getMessage(), e);
		}
	}
}
