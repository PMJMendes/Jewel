package Jewel.Engine.SysObjects;

public class JewelEngineException
	extends Exception
{
	private static final long serialVersionUID = 1L;

	public JewelEngineException()
	{
	}
	
	public JewelEngineException(String pstrMessage)
	{
		super(pstrMessage);
	}

	public JewelEngineException(Throwable e)
	{
		super(e);
	}

	public JewelEngineException(String pstrMessage, Throwable e)
	{
		super(pstrMessage, e);
	}
}
