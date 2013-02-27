package Jewel.Batch;
import Jewel.Engine.SysObjects.JewelWorkerThread;

public class BatchThread
	extends JewelWorkerThread
{
	private Runnable mrefTarget;

	protected BatchThread(Runnable prefTarget)
	{
		mrefTarget = prefTarget;
	}

	public void run()
	{
		try
		{
			if ( mrefTarget != null )
				mrefTarget.run();
		}
		finally
		{
			mrefTarget = null;
		}
	}
}
