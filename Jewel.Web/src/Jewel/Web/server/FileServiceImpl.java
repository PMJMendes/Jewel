package Jewel.Web.server;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;

import Jewel.Engine.Engine;
import Jewel.Engine.SysObjects.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

public class FileServiceImpl
	extends EngineImplementor
	implements FileService
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public static Hashtable<UUID, FileXfer> GetFileXferStorage()
	{
		Hashtable<UUID, FileXfer> larrAux;

        if (getSession() == null)
            return null;

        larrAux = (Hashtable<UUID, FileXfer>)getSession().getAttribute("MADDS_FileXfer_Storage");
        if (larrAux == null)
        {
        	larrAux = new Hashtable<UUID, FileXfer>();
            getSession().setAttribute("MADDS_FileXfer_Storage", larrAux);
        }

        return larrAux;
	}

    protected void service(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException
	{
    	String r;

    	if ( req.getMethod().equals("POST") && ServletFileUpload.isMultipartContent(req) )
    	{
			grefSession.theSession.set(req.getSession());
			try
			{
				overridePost(req, resp);
			}
			catch (JewelWebException e)
			{
				r = "!Exception thrown: " + e.getMessage();
				resp.setContentLength(r.length());
				resp.setContentType("text/html");
				resp.setStatus(HttpServletResponse.SC_OK);
				resp.getWriter().print(r);
			}
			finally
			{
				grefSession.theSession.set(null);
			}
    	}
    	else
    		super.service(req, resp);
	}

    private void overridePost(HttpServletRequest request, HttpServletResponse response)
    	throws IOException, JewelWebException
    {
    	DiskFileItemFactory factory;
    	ServletFileUpload upload;
    	List<?> items;
    	FileItem item;
        FileXfer lbuffer;
        String r;
        UUID lidKey;

        factory = new DiskFileItemFactory();
        factory.setSizeThreshold(102400);
        factory.setFileCleaningTracker(FileCleanerCleanup.getFileCleaningTracker(request.getSession().getServletContext()));
    	upload = new ServletFileUpload(factory);

    	try
    	{
        	items = upload.parseRequest(request);
		}
    	catch (FileUploadException e)
    	{
    		throw new JewelWebException(e.getMessage(), e);
		}
    	if ( items.size() != 1 )
        	throw new JewelWebException("Unexpected number of fields in form.");
    	item = (FileItem)items.get(0);
        if (item.isFormField())
        	throw new JewelWebException("Unexpected non-file field in form.");

        r = item.getFieldName();
        if ( r.equals("none") )
        	lidKey = UUID.randomUUID();
        else
        	lidKey = UUID.fromString(r);
        lbuffer = new FileXfer((int)item.getSize(), item.getContentType(), item.getName(), item.getInputStream());
        GetFileXferStorage().put(lidKey, lbuffer);
        r = lidKey.toString() + "!" + item.getName();

        response.setContentLength(r.length());
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(r);
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    	throws IOException, ServletException
    {
    	String lstrRef;
    	FileXfer lbuffer;

		lstrRef = req.getParameter("fileref");
		if ( lstrRef == null )
		{
			super.doGet(req, resp);
			return;
		}

		if ( Engine.getCurrentUser() == null )
			return;

		lbuffer = GetFileXferStorage().get(UUID.fromString(lstrRef));
		if ( lbuffer == null )
			return;

		resp.setContentType(lbuffer.getContentType());
		resp.addHeader("Content-Disposition", "attachment; filename=\"" + lbuffer.getFileName() + "\"");
		resp.flushBuffer();
		resp.getOutputStream().write(lbuffer.getData());
    }

	public String Discard(String pstrID)
		throws JewelWebException 
	{
		if ( Engine.getCurrentUser() == null )
			return null;

		try
		{
			GetFileXferStorage().remove(UUID.fromString(pstrID));
		}
		catch(Throwable e)
		{
			throw new JewelWebException(e.getMessage(), e);
		}

		return "";
	}
}
