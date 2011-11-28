package Jewel.Web.Filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class JewelWebFilterChain
	implements FilterChain
{
	boolean mbCalled;

	public JewelWebFilterChain()
	{
		mbCalled = false;
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse)
		throws IOException, ServletException
	{
		mbCalled = true;
	}

	public boolean isChainCalled()
	{
		return mbCalled;
	}
}
