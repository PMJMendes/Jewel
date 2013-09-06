package Jewel.Web.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.PropertyConfigurator;

/**
 * Class to load Log4j configurations at the servlet context
 * 
 * @author acamilo
 *
 */
public class ContextLog4jListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent sce) {
	    ServletContext context = sce.getServletContext( );

	    String realPath = context.getRealPath("/");
	    String fileSep = System.getProperty("file.separator");

	    //Make sure the real path ends with a file separator character ('/')
	    if (realPath != null && (! realPath.endsWith(fileSep))){
	          realPath = realPath + fileSep;}


	    //Initialize logger here; the log4j properties filename is specified
	    //by a context parameter named "logger-config"

	    PropertyConfigurator.configure(realPath + "WEB-INF/" + context.getInitParameter("logger-config"));
	}

	public void contextDestroyed(ServletContextEvent sce) {
	}

}
