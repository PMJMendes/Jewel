package Jewel.Mobile.server;

import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.Implementation.Form;
import Jewel.Mobile.interfaces.ComplexScreenService;
import Jewel.Mobile.shared.JewelMobileException;

public class ComplexScreenServiceImpl
	extends EngineImplementor
	implements ComplexScreenService
{
	private static final long serialVersionUID = 1L;

	public String GetQueryID(String pstrFormID)
		throws JewelMobileException
	{
		if ( Engine.getCurrentUser() == null )
			return null;

		try
		{
			return Form.GetInstance(UUID.fromString(pstrFormID)).getResultsQuery().getKey().toString();
		}
		catch (Throwable e)
		{
			throw new JewelMobileException(e.getMessage(), e);
		}
	}
}
