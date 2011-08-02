package Jewel.Petri.Objects;

import java.lang.reflect.Constructor;
import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Petri.*;
import Jewel.Petri.Interfaces.*;
import Jewel.Petri.Interfaces.IPermission;
import Jewel.Petri.SysObjects.JewelPetriException;
import Jewel.Petri.SysObjects.Operation;

public class PNOperation
	extends ObjectBase
	implements IOperation
{
	private IController[] marrInputs;
	private IController[] marrOutputs;
	private IPermission[] marrPermissions;
	private IOperation mrefUndoOp;
    private Class<?> mrefClass;
    private Constructor<?> mrefConst;

    public static PNOperation GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelPetriException
	{
	    try
	    {
			return (PNOperation)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNOperation), pidKey);
		}
	    catch (Throwable e)
	    {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
	}

    public static PNOperation GetInstance(UUID pidNameSpace, ResultSet prsObject)
    	throws JewelPetriException
    {
        try
        {
			return (PNOperation)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, Constants.ObjID_PNOperation), prsObject);
		}
        catch (Throwable e)
        {
	    	throw new JewelPetriException(e.getMessage(), e);
		}
    }

	public void Initialize()
		throws JewelEngineException
	{
		MasterDB ldb;
		ResultSet lrsInputs, lrsOutputs, lrsPerms, lrsOperations;
		IEntity lrefSink, lrefSource, lrefPerm, lrefOperation;
		int[] larrMembers;
		java.lang.Object[] larrParams;
		ArrayList<IController> larrAux;
		ArrayList<IPermission> larrAux2;

		mrefClass = null;
		mrefConst = null;

		larrAux = new ArrayList<IController>();

		larrMembers = new int[1];
		larrMembers[0] = Constants.FKOperation_In_Sink;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();

		try
		{
			lrefSink = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNSink));
			ldb = new MasterDB();
			lrsInputs = lrefSink.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
			while ( lrsInputs.next() )
				larrAux.add(((ISink)PNSink.GetInstance(getNameSpace(), lrsInputs)).GetController());
			lrsInputs.close();
			ldb.Disconnect();

			marrInputs = larrAux.toArray(new IController[larrAux.size()]);
		}
		catch (Throwable e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}

		larrAux = new ArrayList<IController>();

		larrMembers = new int[1];
		larrMembers[0] = Constants.FKOperation_In_Source;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();

		try
		{
			lrefSource = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNSource));
			ldb = new MasterDB();
			lrsOutputs = lrefSource.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
			while ( lrsOutputs.next() )
				larrAux.add(((ISource)PNSource.GetInstance(getNameSpace(), lrsOutputs)).GetController());
			lrsOutputs.close();
			ldb.Disconnect();

			marrOutputs = larrAux.toArray(new IController[larrAux.size()]);
		}
		catch (Throwable e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}

		larrAux2 = new ArrayList<IPermission>();

		larrMembers = new int[1];
		larrMembers[0] = Constants.FKOperation_In_Permission;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();

		try
		{
			lrefPerm = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNPermission));
			ldb = new MasterDB();
			lrsPerms = lrefPerm.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
			while ( lrsPerms.next() )
				larrAux2.add((IPermission)PNPermission.GetInstance(getNameSpace(), lrsPerms));
			lrsPerms.close();
			ldb.Disconnect();

			marrPermissions = larrAux2.toArray(new IPermission[larrAux.size()]);
		}
		catch (Throwable e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}

		larrMembers = new int[1];
		larrMembers[0] = Constants.FKSourceOp_In_Operation;
		larrParams = new java.lang.Object[1];
		larrParams[0] = getKey();

		try
		{
			lrefOperation = Entity.GetInstance(Engine.FindEntity(getNameSpace(), Constants.ObjID_PNPermission));
			ldb = new MasterDB();
			lrsOperations = lrefOperation.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
			if ( lrsOperations.next() )
				mrefUndoOp = (IOperation)PNOperation.GetInstance(getNameSpace(), lrsOperations);
			lrsOperations.close();
			ldb.Disconnect();
		}
		catch (Throwable e)
		{
			throw new JewelEngineException(e.getMessage(), e);
		}
	}

    public UUID GetScriptID()
    {
    	return (UUID)getAt(1);
    }

    public IScript GetScript()
    	throws JewelPetriException
    {
		return (IScript)PNScript.GetInstance(getNameSpace(), GetScriptID());
    }

	public IController[] getInputs()
	{
		return marrInputs;
	}

	public IController[] getOutputs()
	{
		return marrOutputs;
	}

	public UUID getDefaultLevel()
	{
		return (UUID)getAt(3);
	}

	public boolean checkPermission(UUID pidProfile)
	{
		int i;

		if ( pidProfile == null )
			return false;

		for ( i = 0; i < marrPermissions.length; i++ )
			if ( pidProfile.equals(marrPermissions[i].getProfile()) )
				return true;

		return false;
	}

	public IOperation GetUndoOp()
	{
		return mrefUndoOp;
	}

	public Operation GetNewInstance(UUID pidProcess)
		throws JewelPetriException
	{
		Class<?>[] larrTypes;
		java.lang.Object[] larrParams;

		larrTypes = new Class<?>[1];
		larrTypes[0] = UUID.class;

        larrParams = new java.lang.Object[1];
		larrParams[0] = pidProcess;

		try
		{
			if ( mrefClass == null )
				mrefClass = Class.forName(((String)getAt(5)).replaceAll("MADDS", "Jewel"));
			if ( mrefConst == null )
				mrefConst = mrefClass.getConstructor(larrTypes);
			return (Operation)mrefConst.newInstance(larrParams);
		}
		catch (Throwable e)
		{
			throw new JewelPetriException(e.getMessage(), e);
		}
	}
}
