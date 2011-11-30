package Jewel.Web.Filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class JewelWebFilterChain
	implements FilterChain
{
	private ServletRequest mrefRequest;

	public JewelWebFilterChain()
	{
		mrefRequest = null;
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException
	{
		mrefRequest = servletRequest;
	}

	public ServletRequest getRequest()
	{
		return mrefRequest;
	}
}
