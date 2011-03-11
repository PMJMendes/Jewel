package Jewel.Engine.Implementation;

import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class BatchTask
	extends ObjectBase
	implements IBatchTask
{
	private IApplication mrefApp;

	public static IBatchTask GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelEngineException, SQLException
	{
		return (IBatchTask)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_BatchTask), pidKey);
	}

	public void Initialize()
		throws JewelEngineException
	{
		mrefApp = Application.GetInstance((UUID)getAt(5));
	}

	public void DoRun(UUID pidNSpace)
	{
		if ((mrefApp.getAssemblyName() == null) || (mrefApp.getStaticClass() == null))
			return;

		CodeExecuter.ExecuteStatic(mrefApp.getAssemblyName(), mrefApp.getStaticClass(), (String)getAt(1), pidNSpace);
	}
}
