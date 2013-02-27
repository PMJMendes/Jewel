package Jewel.Batch;

import java.sql.ResultSet;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.Constants.NameSpaceGUIDs;
import Jewel.Engine.Constants.ObjectGUIDs;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.Implementation.BatchTask;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Interfaces.IEntity;

public class JewelBatchRunner
{
    public static void main(String[] args)
    {
    	EngineImplementor lobjEngine;
        IEntity lrefNSpaces;
        MasterDB ldb;
        ResultSet lrs;

        try
        {
        	lobjEngine = new EngineImplementor();
			Engine.InitEngine(lobjEngine);

	        RunNamespace(NameSpaceGUIDs.N_MADDS, true);

	        lrefNSpaces = Entity.GetInstance(Engine.FindEntity(NameSpaceGUIDs.N_MADDS, ObjectGUIDs.O_NameSpace));

	        ldb = new MasterDB();
	        lrs = lrefNSpaces.SelectAll(ldb);
	        while (lrs.next())
	        {
	            if (NameSpaceGUIDs.N_MADDS.equals(UUID.fromString(lrs.getString(1))))
	                continue;

	            RunNamespace(UUID.fromString(lrs.getString(1)), false);
	        }
	        lrs.close();
	        ldb.Disconnect();
		}
        catch (Throwable e)
        {
			e.printStackTrace();
		}
    }

    static void RunNamespace(UUID pidNSpace, boolean pbAsMADDS)
    	throws JewelBatchException
    {
        IEntity lrefTasks;
        MasterDB ldb;
        ResultSet lrs;

        try
        {
        	lrefTasks = Entity.GetInstance(Engine.FindEntity(pidNSpace, ObjectGUIDs.O_BatchTask));

	        if (!pbAsMADDS && (NameSpaceGUIDs.N_MADDS.equals(lrefTasks.getMemberOf().getKey())))
	            return;

	        ldb = new MasterDB();
	        lrs = lrefTasks.SelectAll(ldb);
	        while (lrs.next())
	            BatchTask.GetInstance(pidNSpace, UUID.fromString(lrs.getString(1))).DoRun(pidNSpace);
	        lrs.close();
	        ldb.Disconnect();
        }
        catch (Throwable e)
        {
        	throw new JewelBatchException(e.getMessage(), e);
        }
    }
}
