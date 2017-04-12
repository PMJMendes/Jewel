package Jewel.Web.server;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.Document;
import org.apache.ecs.html.Link;

import Jewel.Engine.Engine;
import Jewel.Engine.Implementation.Report;
import Jewel.Engine.Interfaces.IReport;
import Jewel.Engine.SysObjects.CodeExecuter;
import Jewel.Web.shared.JewelWebException;
import Jewel.Web.shared.ReportID;

public class ReportServiceImpl
	extends EngineImplementor
{
	private static final long serialVersionUID = 1L;
	final static String STYLESHEET_PATH = "../stylesheets/report-styles.css";

	@SuppressWarnings("unchecked")
	public static ConcurrentHashMap<UUID, ReportID> GetReportParamStorage()
	{
		ConcurrentHashMap<UUID, ReportID> larrAux;

        if (getSession() == null)
            return null;

        larrAux = (ConcurrentHashMap<UUID, ReportID>)getSession().getAttribute("MADDS_ReportParam_Storage");
        if (larrAux == null)
        {
        	larrAux = new ConcurrentHashMap<UUID, ReportID>();
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
    	UUID lidReport, lidForm, lidRefObj;
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

    		lidRefObj = pobjReport.mstrRefObj == null ? null : UUID.fromString(pobjReport.mstrRefObj);

    		ldoc = new Document();

    		linkCSSByMethod(ldoc, lrefReport.getAssembly(), lrefReport.getClassName(), lrefReport.getMethod());
    		
    		lstrRes = CodeExecuter.ExecuteReport(lrefReport.getAssembly(), lrefReport.getClassName(), lrefReport.getMethod(),
    				UUID.fromString(pobjReport.mstrNameSpace), larrIndexes, larrValues, lidRefObj, ldoc.getBody());
    	}
    	catch (Throwable e)
    	{
    		throw new JewelWebException(e.getMessage(), e);
    	}
    		
		if ( !lstrRes.equals("") )
			throw new JewelWebException("Error building report: " + lstrRes);

		return ldoc;
    }
    
    private void linkCSSByMethod(Document doc, String assembly, String className, String methodName) {
    	if(!className.replace(assembly + ".", "").equals(new String("Objects.Incident")) || !methodName.equals(new String("PrintReport")))
    		return;
    	
		Link css = new Link();
		css.setType("text/css");
		css.setRel("stylesheet");
		css.setHref(STYLESHEET_PATH);
		
		doc.getHead().addElement(css);

    }
}
