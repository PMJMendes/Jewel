package Jewel.Petri.SysObjects;

import java.util.UUID;

import Jewel.Engine.DataAccess.SQLServer;

public abstract class SilentOperation
	extends Operation
{
	private static final long serialVersionUID = 1L;

	public SilentOperation(UUID pidProcess)
	{
		super(pidProcess);
	}

	public String ShortDesc()
	{
		return null;
	}

	public String LongDesc(String pstrLineBreak)
	{
		return null;
	}

	public UUID GetExternalProcess()
	{
		return null;
	}

	protected void Run(SQLServer pdb)
		throws JewelPetriException
	{
	}

	protected boolean IsSilent()
	{
		return true;
	}
}
