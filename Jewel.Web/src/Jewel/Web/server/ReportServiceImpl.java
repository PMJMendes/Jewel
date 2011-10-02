package Jewel.Web.server;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.ecs.*;

import Jewel.Engine.Engine;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Web.shared.*;

public class ReportServiceImpl
	extends EngineImplementor
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public static Hashtable<UUID, ReportID> GetReportParamStorage()
	{
		Hashtable<UUID, ReportID> larrAux;

        if (getSession() == null)
            return null;

        larrAux = (Hashtable<UUID, ReportID>)getSession().getAttribute("MADDS_ReportParam_Storage");
        if (larrAux == null)
        {
        	larrAux = new Hashtable<UUID, ReportID>();
            getSession().setAttribute("MADDS_ReportParam_Storage", larrAux);
        }

        return larrAux;
	}

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws IOException, ServletException
	{
    	String lstrRef;
    	UUID lid;
    	ReportID lobjReport;
    	Document ldoc;
    	String r;

		lstrRef = req.getParameter("rptid");
		if ( lstrRef == null )
		{
			super.doGet(req, resp);
			return;
		}

		if ( Engine.getCurrentUser() == null )
			return;

		lid = UUID.fromString(lstrRef);
		lobjReport = GetReportParamStorage().get(lid);
		if ( lobjReport == null )
			return;
		GetReportParamStorage().remove(lid);

		try
		{
			ldoc = BuildReport(lobjReport);
		}
		catch (JewelWebException e)
		{
			r = "Error building report: " + e.getMessage();
			resp.setContentLength(r.length());
			resp.setContentType("text/html");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().print(r);
			return;
		}

        resp.setContentType("text/html; charset=UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        ldoc.output(resp.getWriter());
	}
    
    private Document BuildReport(ReportID pobjReport)
    	throws JewelWebException
    {
    	UUID lidReport, lidForm;
    	IReport lrefReport;
    	int[] larrIndexes;
    	java.lang.Object[] larrValues;
    	Document ldoc;
    	String lstrRes;

    	try
    	{
    		lidReport = UUID.fromString(pobjReport.mstrReportID);
    		lrefReport = Report.GetInstance(lidReport);

    		lidForm = UUID.fromString(pobjReport.mstrFormID);
    		larrIndexes = FormDataBridge.GetParamIndexes(lidForm, pobjReport.marrValues);
    		larrValues = FormDataBridge.GetParamData(lidForm, pobjReport.marrValues);

    		ldoc = new Document();

    		lstrRes = CodeExecuter.ExecuteReport(lrefReport.getAssembly(), lrefReport.getClassName(), lrefReport.getMethod(),
    				UUID.fromString(pobjReport.mstrNameSpace), larrIndexes, larrValues, ldoc.getBody());
    	}
    	catch (Throwable e)
    	{
    		throw new JewelWebException(e.getMessage(), e);
    	}
    		
		if ( !lstrRes.equals("") )
			throw new JewelWebException("Error building report: " + lstrRes);

		return ldoc;
    }
}
