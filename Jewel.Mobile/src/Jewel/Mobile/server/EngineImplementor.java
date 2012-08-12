package Jewel.Mobile.server;

import java.sql.ResultSet;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import Jewel.Engine.Engine;
import Jewel.Engine.Constants.Miscellaneous;
import Jewel.Engine.Constants.ObjectGUIDs;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Implementation.NameSpace;
import Jewel.Engine.Implementation.User;
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

    private IUser mrefPushedUser;
    private INameSpace mrefPushedNSpace;

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

	public void pushNameSpace(UUID pidNameSpace)
		throws JewelEngineException
	{
		User lobjUser;
		INameSpace lrefNSpace;
        IEntity lrefUsers;
        MasterDB ldb;
        ResultSet lrs;
        int[] larrMembers;
        java.lang.Object[] larrParams;
        UUID lidUser;

		if ( mrefPushedUser != null )
			throw new JewelEngineException("Error: Nested Name Space push not allowed.");
		if ( mrefPushedNSpace != null )
			throw new JewelEngineException("Unexpected: Inconsistent internal state during Name Space push.");

		lrefNSpace = NameSpace.GetInstance(getCurrentNameSpace());
		lobjUser = User.GetInstance(lrefNSpace.getKey(), getCurrentUser());

        lidUser = null;

        larrMembers = new int[2];
        larrMembers[0] = Miscellaneous.Username_In_User;
        larrMembers[1] = Miscellaneous.Password_In_User;
        larrParams = new java.lang.Object[2];
        larrParams[0] = "!" + lobjUser.getAt(Miscellaneous.Username_In_User);
        larrParams[1] = lobjUser.getAt(Miscellaneous.Password_In_User);

        try
        {
	        lrefUsers = Entity.GetInstance(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_User));
		
	        ldb = new MasterDB();
	        lrs = lrefUsers.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);

	        if (lrs.next())
	        {
	            lidUser = UUID.fromString(lrs.getString(1));
	            if (lrs.next())
	            {
	    	        lrs.close();
	    	        ldb.Disconnect();
	                throw new JewelEngineException("Unexpected: Username is not unique!");
	            }
	        }
	        else
	        {
		        lrs.close();
		        ldb.Disconnect();
	            throw new JewelEngineException("Invalid Username or Password!");
	        }

	        lrs.close();
	        ldb.Disconnect();

	        mrefPushedUser = lobjUser;
	        mrefPushedNSpace = lrefNSpace;

	        getSession().setAttribute("UserID", lidUser);
	        getSession().setAttribute("UserNSpace", pidNameSpace);

	        NameSpace.GetInstance(pidNameSpace).DoLogin(lidUser, true);
        }
        catch (JewelEngineException e)
        {
        	throw e;
        }
        catch (Throwable e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
        }
	}

	public void popNameSpace()
		throws JewelEngineException
	{
		if ( mrefPushedUser == null )
			throw new JewelEngineException("Error: No previous Name Space push.");
		if ( mrefPushedNSpace == null )
			throw new JewelEngineException("Unexpected: Inconsistent internal state during Name Space pop.");
        getSession().setAttribute("UserID", mrefPushedUser.getKey());
        getSession().setAttribute("UserNSpace", mrefPushedNSpace.getKey());
        mrefPushedUser = null;
        mrefPushedNSpace = null;
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

	public JewelWorkerThread getThread(Runnable prefThread)
	{
    	throw new UnsupportedOperationException();
	}
}
