package Jewel.Petri.SysObjects;

import java.util.UUID;

import Jewel.Engine.DataAccess.SQLServer;

public abstract class UndoableOperation
	extends Operation
{
	private static final long serialVersionUID = 1L;

	public static class UndoSet
	{
		public UUID midType;
		public UUID[] marrCreated;
		public UUID[] marrChanged;
		public UUID[] marrDeleted;
	}

	public UndoableOperation(UUID pidProcess)
	{
		super(pidProcess);
	}

	public abstract String UndoDesc(String pstrLineBreak);
	public abstract String UndoLongDesc(String pstrLineBreak);
	protected abstract void Undo(SQLServer pdb) throws JewelPetriException;
	public abstract UndoSet[] GetSets();

	public void ExecuteUndo(QueueContext parrQueue, SQLServer pdb)
		throws JewelPetriException
	{
		QueueContext larrOldContext;

		larrOldContext = marrTriggers;
		marrTriggers = parrQueue;

		try
		{
			Undo(pdb);
		}
		catch (JewelPetriException e)
		{
			marrTriggers = larrOldContext;
			throw e;
		}
		catch (Throwable e)
		{
			marrTriggers = larrOldContext;
			throw new JewelPetriException(e.getMessage(), e);
		}
	}

	public boolean LocalCanUndo()
	{
		return true;
	}
}
