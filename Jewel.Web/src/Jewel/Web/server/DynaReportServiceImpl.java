package Jewel.Web.server;

import java.util.*;

import Jewel.Engine.Engine;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

public class DynaReportServiceImpl
	extends EngineImplementor
	implements DynaReportService
{
	private static final long serialVersionUID = 1L;

	public String GetParamFormID(String pstrReportID)
		throws JewelWebException
	{
		if ( Engine.getCurrentUser() == null )
			return null;

		try
		{
			return Report.GetInstance(UUID.fromString(pstrReportID)).getParamForm().toString();
		}
		catch (JewelEngineException e)
		{
			throw new JewelWebException(e.getMessage(), e);
		}
	}

	public String OpenReport(ReportID pobjParams) throws JewelWebException
	{
		UUID lid;

		if ( Engine.getCurrentUser() == null )
			return null;

		lid = UUID.randomUUID();
		
		ReportServiceImpl.GetReportParamStorage().put(lid, pobjParams);

		return lid.toString();
	}
}
