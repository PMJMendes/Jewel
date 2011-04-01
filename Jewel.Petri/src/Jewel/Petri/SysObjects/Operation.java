package Jewel.Petri.SysObjects;

import java.io.*;
import java.util.*;

import Jewel.Engine.Engine;
import Jewel.Petri.Objects.PNProcess;

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
		PNProcess lrefProcess;

		lrefProcess = PNProcess.GetInstance(Engine.getCurrentNameSpace(), midProcess);

		while ( !lrefProcess.Lock() )
		{
			try
			{
				Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
			}
		}

		Run();

		lrefProcess.Unlock();
	}
}
