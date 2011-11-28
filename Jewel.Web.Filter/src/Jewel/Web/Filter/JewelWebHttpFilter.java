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

	public void init(FilterConfig filterConfig)
		throws ServletException
	{
		mrefSpnego.init(filterConfig);
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
		throws IOException, ServletException
	{
		mrefSpnego.doFilter(servletRequest, new JewelWebResponseWrapper(), new JewelWebFilterChain());
		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void destroy()
	{
		mrefSpnego.destroy();
	}
}
