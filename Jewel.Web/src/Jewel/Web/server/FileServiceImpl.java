package Jewel.Web.server;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import Jewel.Engine.Engine;
import Jewel.Engine.SysObjects.FileXfer;
import Jewel.Web.interfaces.FileService;
import Jewel.Web.shared.JewelWebException;

public class FileServiceImpl
	extends EngineImplementor
	implements FileService
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public static ConcurrentHashMap<UUID, FileXfer> GetFileXferStorage()
	{
		ConcurrentHashMap<UUID, FileXfer> larrAux;

        if (getSession() == null)
            return null;

        larrAux = (ConcurrentHashMap<UUID, FileXfer>)getSession().getAttribute("MADDS_FileXfer_Storage");
        if (larrAux == null)
        {
        	larrAux = new ConcurrentHashMap<UUID, FileXfer>();
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
			setSession(req.getSession());
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
				clearSession();
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
