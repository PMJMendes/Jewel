package Jewel.Petri.SysObjects;

import java.util.UUID;

import Jewel.Petri.Objects.PNLog;

public abstract class UndoOperation
	extends Operation
{
	private static final long serialVersionUID = 1L;

	public UndoOperation(UUID pidProcess)
	{
		super(pidProcess);
	}

	public Operation mobjSourceOp;
	public transient PNLog mrefLog;
}
