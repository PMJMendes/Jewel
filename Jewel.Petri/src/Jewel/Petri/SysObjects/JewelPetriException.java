package Jewel.Petri.SysObjects;

public class JewelPetriException
	extends Exception
{
	private static final long serialVersionUID = 1L;

	public JewelPetriException()
	{
	}
	
	public JewelPetriException(String pstrMessage)
	{
		super(pstrMessage);
	}

	public JewelPetriException(Throwable e)
	{
		super(e);
	}

	public JewelPetriException(String pstrMessage, Throwable e)
	{
		super(pstrMessage, e);
	}

}
