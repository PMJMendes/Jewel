package Jewel.Petri.SysObjects;

public class NotRunnableException
	extends Exception
{
	private static final long serialVersionUID = 1L;

	public NotRunnableException()
	{
	}
	
	public NotRunnableException(String pstrMessage)
	{
		super(pstrMessage);
	}

	public NotRunnableException(Throwable e)
	{
		super(e);
	}

	public NotRunnableException(String pstrMessage, Throwable e)
	{
		super(pstrMessage, e);
	}

}
