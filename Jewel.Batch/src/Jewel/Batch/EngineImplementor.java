package Jewel.Batch;

import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.Constants.Miscellaneous;
import Jewel.Engine.Constants.ObjectGUIDs;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Implementation.NameSpace;
import Jewel.Engine.Implementation.User;
import Jewel.Engine.Interfaces.IEngineImpl;
import Jewel.Engine.Interfaces.IEntity;
import Jewel.Engine.Interfaces.INameSpace;
import Jewel.Engine.Interfaces.IUser;
import Jewel.Engine.SysObjects.Cache;
import Jewel.Engine.SysObjects.FileXfer;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.JewelWorkerThread;

public class EngineImplementor
	implements IEngineImpl
{
    private Cache marrGlobal;
    private Cache marrLocal;
    private Hashtable<String, Object> marrUserData;
    private IUser mrefCurrentUser;
    private INameSpace mrefCurrentNSpace;
    private IUser mrefPushedUser;
    private INameSpace mrefPushedNSpace;
    private boolean mbPushed;

    public EngineImplementor()
    {
    	mbPushed = false;
    }

    public void Login(IUser prefUser, INameSpace prefNSpace)
    {
    	mrefCurrentUser = prefUser;
    	mrefCurrentNSpace = prefNSpace;

        mrefCurrentNSpace.DoLogin(mrefCurrentUser.getKey(), true);
    }

	public Cache GetCache(boolean pbGlobal)
		throws JewelEngineException
	{
        if (pbGlobal)
        {
            if (marrGlobal == null)
            {
                marrGlobal = new Cache(true);
                marrGlobal.InitCache();
            }
            return marrGlobal;
        }
        else
        {
            if (marrLocal == null)
            {
                marrLocal = new Cache(false);
                marrLocal.InitCache();
            }
            return marrLocal;
        }
	}

	public void ResetGlobalCache()
		throws JewelEngineException
	{
        marrGlobal = null;
        GetCache(true);
	}

	public UUID getCurrentUser()
	{
		return mrefCurrentUser.getKey();
	}

	public UUID getCurrentNameSpace()
	{
		return mrefCurrentNSpace.getKey();
	}

	public Hashtable<String, Object> getUserData()
	{
        if (marrUserData == null)
            marrUserData = new Hashtable<String, Object>();

        return marrUserData;
	}

	public void pushNameSpace(UUID pidNameSpace)
		throws JewelEngineException
	{
        IUser lobjAux;
        IEntity lrefUsers;
        MasterDB ldb;
        ResultSet lrs;

		if ( mbPushed )
			throw new JewelEngineException("Error: Nested Name Space push not allowed.");

		if ( mrefCurrentUser == null )
		{
			mrefPushedNSpace = mrefCurrentNSpace;
			mrefCurrentNSpace = NameSpace.GetInstance(pidNameSpace);
			mbPushed = true;
			return;
		}

        try
        {
	        lrefUsers = Entity.GetInstance(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_User));
	
	        ldb = new MasterDB();
	        lrs = lrefUsers.SelectByMembers(ldb, new int[] {Miscellaneous.Username_In_User},
	        		new java.lang.Object[] {"!" + mrefCurrentUser.getUserName()}, new int[0]);

	        if (lrs.next())
	        {
	        	lobjAux = User.GetInstance(pidNameSpace, lrs);
	        	if ( !lobjAux.CheckPassword(mrefCurrentUser.getPassword()) )
	        	{
	    	        lrs.close();
	    	        ldb.Disconnect();
		            throw new JewelEngineException("Username or Password mismatch.");
	        	}
	            if (lrs.next())
	            {
	    	        lrs.close();
	    	        ldb.Disconnect();
	                throw new JewelEngineException("Unexpected: Username is not unique!");
	            }
	        }
	        else
	        {
		        lrs.close();
		        ldb.Disconnect();
	            throw new JewelEngineException("Username or Password mismatch.");
	        }

	        lrs.close();
	        ldb.Disconnect();

	        mrefPushedUser = mrefCurrentUser;
	        mrefPushedNSpace = mrefCurrentNSpace;
	        mbPushed = true;

	        mrefCurrentUser = lobjAux;
	        mrefCurrentNSpace = NameSpace.GetInstance(pidNameSpace);

	        mrefCurrentNSpace.DoLogin(mrefCurrentUser.getKey(), true);
        }
        catch (JewelEngineException e)
        {
        	throw e;
        }
        catch (Throwable e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
        }
	}

	public void popNameSpace()
		throws JewelEngineException
	{
		if ( !mbPushed )
			throw new JewelEngineException("Error: No previous Name Space push.");
        mrefCurrentUser = mrefPushedUser;
        mrefCurrentNSpace = mrefPushedNSpace;
        mrefPushedUser = null;
        mrefPushedNSpace = null;
        mbPushed = false;

		if ( mrefCurrentUser != null )
			mrefCurrentNSpace.DoLogin(mrefCurrentUser.getKey(), true);
	}

	public String getCurrentPath()
	{
		return System.getProperty("user.dir");
	}

	public void UnloadEngine()
	{
    	throw new UnsupportedOperationException();
	}

	public void OutputFile(FileXfer pobjFile)
	{
    	throw new UnsupportedOperationException();
	}

	public JewelWorkerThread getThread(Runnable prefThread)
	{
		return new BatchThread(prefThread);
	}
}
