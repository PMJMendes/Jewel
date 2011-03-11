package Jewel.Mobile.server;

import javax.servlet.*;

import Jewel.Engine.*;
import Jewel.Engine.SysObjects.*;

public class Global
	implements ServletContextListener
{
	public void contextInitialized(ServletContextEvent e)
	{
        try
        {
			Engine.InitEngine(new EngineImplementor(e.getServletContext()));
		}
        catch (JewelEngineException e1)
        {
			e1.printStackTrace();
		}
	}

	public void contextDestroyed(ServletContextEvent e)
	{
	}
}
