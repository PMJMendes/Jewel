package Jewel.Petri;

import java.io.*;
import java.util.*;

public abstract class Operation
	implements Serializable
{
	private static final long serialVersionUID = 1L;

	protected UUID midProcess;

	public Operation(UUID pidProcess)
	{
		midProcess = pidProcess;
	}

	protected abstract UUID OpID();
	protected abstract void Run() throws JewelPetriException;

	public final void Execute()
		throws JewelPetriException
	{
		Run();
	}
}
