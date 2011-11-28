package Jewel.Web.Filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.sourceforge.spnego.SpnegoHttpFilter;

public final class JewelWebHttpFilter
	implements Filter
{
	private final transient SpnegoHttpFilter mrefSpnego = new SpnegoHttpFilter();

	public void init(FilterConfig arg0)
		throws ServletException
	{
		mrefSpnego.init(arg0);
	}

	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
		throws IOException, ServletException
	{
		try
		{
			mrefSpnego.doFilter(arg0, arg1, arg2);
		}
		catch (Throwable e)
		{
			
		}
	}

	public void destroy()
	{
		mrefSpnego.destroy();
	}
}
