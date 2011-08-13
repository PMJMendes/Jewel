package Jewel.Petri.SysObjects;

import java.util.UUID;

import Jewel.Engine.DataAccess.SQLServer;

public abstract class UndoableOperation
	extends Operation
{
	private static final long serialVersionUID = 1L;

	public UndoableOperation(UUID pidProcess)
	{
		super(pidProcess);
	}

	public abstract String UndoDesc(String pstrLineBreak);
	public abstract String UndoLongDesc(String pstrLineBreak);
	protected abstract void Undo(SQLServer pdb) throws JewelPetriException;
}
