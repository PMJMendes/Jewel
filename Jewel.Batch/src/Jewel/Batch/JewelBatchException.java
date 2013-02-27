package Jewel.Batch;

public class JewelBatchException
	extends Exception
{
	private static final long serialVersionUID = 1L;

	public JewelBatchException()
	{
	}
	
	public JewelBatchException(String pstrMessage)
	{
		super(pstrMessage);
	}

	public JewelBatchException(Throwable e)
	{
		super(e);
	}

	public JewelBatchException(String pstrMessage, Throwable e)
	{
		super(pstrMessage, e);
	}
}
