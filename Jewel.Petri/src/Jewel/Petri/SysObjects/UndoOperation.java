package Jewel.Petri.SysObjects;

import java.util.UUID;

import Jewel.Engine.DataAccess.SQLServer;
import Jewel.Petri.Objects.PNLog;

public abstract class UndoOperation
	extends Operation
{
	private static final long serialVersionUID = 1L;

	public UndoOperation(UUID pidProcess)
	{
		super(pidProcess);
	}

	public UUID midSourceLog;

	public transient PNLog mrefLog;
	public transient UndoableOperation mobjSourceOp;

	public String LongDesc(String pstrLineBreak)
	{
		if ( mobjSourceOp instanceof UndoableOperation )
			return ((UndoableOperation)mobjSourceOp).UndoLongDesc(pstrLineBreak);

		return null;
	}

	protected void Run(SQLServer pdb)
		throws JewelPetriException
	{
		mobjSourceOp.Undo(pdb);

		try
		{
			mrefLog.setAt(5, true);
			mrefLog.SaveToDb(pdb);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}
	}
}
