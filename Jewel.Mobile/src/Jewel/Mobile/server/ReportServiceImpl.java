package Jewel.Mobile.server;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ecs.Document;
import org.apache.tools.ant.filters.StringInputStream;
import org.apache.xerces.parsers.DOMParser;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xml.sax.InputSource;

import Jewel.Engine.Engine;
import Jewel.Engine.Implementation.Report;
import Jewel.Engine.Interfaces.IReport;
import Jewel.Engine.SysObjects.CodeExecuter;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Mobile.interfaces.ReportService;
import Jewel.Mobile.shared.JewelMobileException;
import Jewel.Mobile.shared.ReportID;

public class ReportServiceImpl
	extends EngineImplementor
	implements ReportService
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

			resp.setContentType("image/png");
			resp.addHeader("Content-Disposition", "inline; filename=\"report.png\"");
			ImageIO.write(RenderReport(ldoc), "png", resp.getOutputStream());
		}
		catch (JewelMobileException e)
		{
			r = "Error building report: " + e.getMessage();
			resp.setContentLength(r.length());
			resp.setContentType("text/html");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().print(r);
			return;
		}

//        resp.setContentType("text/html; charset=UTF-8");
//        resp.setStatus(HttpServletResponse.SC_OK);
//        ldoc.output(resp.getWriter());
	}

	public String GetParamFormID(String pstrReportID)
		throws JewelMobileException
	{
		if ( Engine.getCurrentUser() == null )
			return null;

		try
		{
			return Report.GetInstance(UUID.fromString(pstrReportID)).getParamForm().toString();
		}
		catch (JewelEngineException e)
		{
			throw new JewelMobileException(e.getMessage(), e);
		}
	}

	public String OpenReport(ReportID pobjParams)
		throws JewelMobileException
	{
		UUID lid;

		if ( Engine.getCurrentUser() == null )
			return null;

		lid = UUID.randomUUID();
		
		GetReportParamStorage().put(lid, pobjParams);

		return lid.toString();
	}
    
    private Document BuildReport(ReportID pobjReport)
    	throws JewelMobileException
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
    		throw new JewelMobileException(e.getMessage(), e);
    	}
    		
		if ( !lstrRes.equals("") )
			throw new JewelMobileException("Error building report: " + lstrRes);

		return ldoc;
    }

    private BufferedImage RenderReport(Document pobjReport)
    	throws JewelMobileException
    {
    	String lstr;
    	DOMParser lobjParser;
    	Graphics2DRenderer lobjRenderer;
    	BufferedImage limgResult;
    	Graphics2D lobjGraphics;

    	lstr = pobjReport.toString().replace("&nbsp;", " ");

    	lobjParser = new DOMParser();
    	try
    	{
			lobjParser.parse(new InputSource(new StringInputStream(lstr)));
		}
    	catch (Throwable e)
    	{
    		throw new JewelMobileException(e.getMessage(), e);
		}

    	lobjRenderer = new Graphics2DRenderer();
    	lobjRenderer.setDocument(lobjParser.getDocument(), lobjParser.getDocument().getDocumentURI());

    	limgResult = new BufferedImage(1280, 800, BufferedImage.TYPE_BYTE_GRAY);

    	lobjGraphics = (Graphics2D)limgResult.getGraphics();

    	lobjRenderer.layout(lobjGraphics, new Dimension(1280, 800));
    	lobjRenderer.render(lobjGraphics);
    	lobjGraphics.dispose();

    	return limgResult;
    }
}
