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

	private transient PNLog mrefLog;
	private transient UndoableOperation mobjSourceOp;

	public UndoableOperation GetSource()
		throws JewelPetriException
	{
		Setup();
		return mobjSourceOp;
	}

	public String LongDesc(String pstrLineBreak)
	{
		try
		{
			Setup();
		}
		catch (Throwable e)
		{
			return "Error obtaining log information from original operation.";
		}

		return mobjSourceOp.UndoLongDesc(pstrLineBreak);
	}

	public UUID GetExternalProcess()
	{
		return mobjSourceOp.GetExternalProcess();
	}

	protected void Run(SQLServer pdb)
		throws JewelPetriException
	{
		Setup();

		mobjSourceOp.ExecuteUndo(marrTriggers, pdb);

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

	private void Setup()
		throws JewelPetriException
	{
		Operation lobjOp;

		if ( mrefLog == null )
			mrefLog = PNLog.GetInstance(midNameSpace, midSourceLog);

		if ( mobjSourceOp == null )
		{
			lobjOp = mrefLog.GetOperationData();
			if ( !(lobjOp instanceof UndoableOperation) )
				throw new JewelPetriException("Unexpected: Operation does not support Undo methods.");
			mobjSourceOp = (UndoableOperation)lobjOp;
		}
	}
}
