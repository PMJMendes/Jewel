package Jewel.Mobile.shared;

public class JewelMobileException
	extends Exception
{
	private static final long serialVersionUID = 1L;

	public JewelMobileException()
	{
	}
	
	public JewelMobileException(String pstrMessage)
	{
		super(pstrMessage);
	}

	public JewelMobileException(Throwable e)
	{
		super(e);
	}

	public JewelMobileException(String pstrMessage, Throwable e)
	{
		super(pstrMessage, e);
	}
}
