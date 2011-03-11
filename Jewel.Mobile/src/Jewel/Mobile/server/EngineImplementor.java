package Jewel.Mobile.server;

import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

import com.google.gwt.user.server.rpc.*;

public class EngineImplementor
	extends RemoteServiceServlet
	implements IEngineImpl
{
	private static final long serialVersionUID = 1L;

	protected static class SessionHolder
	{
		public ThreadLocal<HttpSession> theSession;

		public SessionHolder()
		{
			theSession = new ThreadLocal<HttpSession>()
			{
				protected HttpSession initialValue()
				{
					return null;
				}
			};
		}
	}

	private static ServletContext mrefContext;
	private static Cache garrGlobal;
	protected static SessionHolder grefSession;

    protected static HttpSession getSession()
    {
    	return grefSession.theSession.get();
    }

    public EngineImplementor()
    {
    	if ( mrefContext == null )
    		mrefContext = getServletContext();

    	if ( grefSession == null )
    		grefSession = new SessionHolder();
    }

    public EngineImplementor(ServletContext prefContext)
    {
    	mrefContext = prefContext;

    	if ( grefSession == null )
    		grefSession = new SessionHolder();
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp)
    	throws ServletException, java.io.IOException
    {
    	grefSession.theSession.set(req.getSession());
    	try
    	{
    		super.service(req, resp);
    	}
    	finally
    	{
    		grefSession.theSession.set(null);
    	}
    }

    public Cache GetCache(boolean pbGlobal)
    	throws JewelEngineException
    {
        Cache larrLocal;

        if (pbGlobal)
        {
            if (garrGlobal == null)
            {
                garrGlobal = (Cache)mrefContext.getAttribute("MADDS_Global_Cache");
                if (garrGlobal == null)
                {
                    garrGlobal = new Cache(true);
                    mrefContext.setAttribute("MADDS_Global_Cache", garrGlobal);
                    garrGlobal.InitCache();
                }
            }
            return garrGlobal;
        }
        else
        {
            if (getSession() == null)
                return null;

            larrLocal = (Cache)getSession().getAttribute("MADDS_Local_Cache");
            if (larrLocal == null)
            {
                larrLocal = new Cache(false);
                getSession().setAttribute("MADDS_Local_Cache", larrLocal);
                larrLocal.InitCache();
            }

            return larrLocal;
        }
    }

    public void ResetGlobalCache()
    	throws JewelEngineException
    {
        garrGlobal = null;
        mrefContext.removeAttribute("MADDS_Global_Cache");
        GetCache(true);
    }

    public UUID getCurrentUser()
    {
        return (UUID)getSession().getAttribute("UserID");
    }

    public UUID getCurrentNameSpace()
    {
        return (UUID)getSession().getAttribute("UserNSpace");
    }

    @SuppressWarnings("unchecked")
	public Hashtable<String, Object> getUserData()
    {
        Hashtable<String, Object> larrUserData;

        if (getSession() == null)
            return new Hashtable<String, Object>();

        larrUserData = (Hashtable<String, Object>)getSession().getAttribute("MADDS_User_Data");
        if (larrUserData == null)
        {
            larrUserData = new Hashtable<String, Object>();
            getSession().setAttribute("MADDS_User_Data", larrUserData);
        }

        return larrUserData;
    }

    public String getCurrentPath()
    {
    	return System.getenv("user.dir");
    }

    public void UnloadEngine()
    {
    	throw new UnsupportedOperationException();
    }

    public void OutputFile(FileXfer pobjFile)
    {
    	throw new UnsupportedOperationException();
//        Page lpgHandler;
//        string lstrAux;
//
//        lpgHandler = (Page)HttpContext.Current.Handler;
//
//        lstrAux = Guid.NewGuid().ToString();
//
//        HttpContext.Current.Session[lstrAux] = pobjFile;
//
//        lpgHandler.ClientScript.RegisterStartupScript(lpgHandler.GetType(), "outputfile",
//            "window.open('FileDLoad.aspx?donotcache=" + Environment.TickCount.ToString() + "&clear=yes&fileref=" + lstrAux + "');");
    }
}
