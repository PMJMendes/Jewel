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
	public UUID midNameSpace;

	public transient PNLog mrefLog;
	public transient Operation mobjSourceOp;

	public String LongDesc(String pstrLineBreak)
	{
		try
		{
			if ( mobjSourceOp == null )
			{
				if ( mrefLog == null )
					mrefLog = PNLog.GetInstance(midNameSpace, midSourceLog);
			}
			mobjSourceOp = mrefLog.GetOperationData();
			if ( !(mobjSourceOp instanceof UndoableOperation) )
				throw new JewelPetriException("Unexpected: Operation does not support Undo methods.");
		}
		catch (Throwable e)
		{
			return "Error obtaining log information from original operation.";
		}

		return ((UndoableOperation)mobjSourceOp).UndoLongDesc(pstrLineBreak);
	}

	protected void Run(SQLServer pdb)
		throws JewelPetriException
	{
		if ( !(mobjSourceOp instanceof UndoableOperation) )
			throw new JewelPetriException("Unexpected: Operation does not support Undo methods.");

		((UndoableOperation)mobjSourceOp).Undo(pdb);

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
