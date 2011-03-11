package Jewel.Web.shared;

public class JewelWebException
	extends Exception
{
	private static final long serialVersionUID = 1L;

	public JewelWebException()
	{
	}
	
	public JewelWebException(String pstrMessage)
	{
		super(pstrMessage);
	}

	public JewelWebException(Throwable e)
	{
		super(e);
	}

	public JewelWebException(String pstrMessage, Throwable e)
	{
		super(pstrMessage, e);
	}
}
