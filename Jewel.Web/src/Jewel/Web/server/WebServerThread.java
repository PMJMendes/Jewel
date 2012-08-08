package Jewel.Web.server;

import javax.servlet.http.HttpSession;

import Jewel.Engine.SysObjects.JewelWorkerThread;

public class WebServerThread
	extends JewelWorkerThread
{
	private Runnable mrefTarget;
	private HttpSession mrefSession;

	protected WebServerThread(Runnable prefTarget, HttpSession prefSession)
	{
		mrefTarget = prefTarget;
		mrefSession = prefSession;
	}

	public void run()
	{
		EngineImplementor.setSession(mrefSession);
		mrefSession = null;

		try
		{
			if ( mrefTarget != null )
				mrefTarget.run();
		}
		finally
		{
			mrefTarget = null;
			EngineImplementor.clearSession();
		}
	}
}
