package Jewel.Web.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.Constants.EntityGUIDs;
import Jewel.Engine.Constants.Miscellaneous;
import Jewel.Engine.Constants.NameSpaceGUIDs;
import Jewel.Engine.Constants.ObjectGUIDs;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Implementation.NameSpace;
import Jewel.Engine.Implementation.User;
import Jewel.Engine.Interfaces.IEntity;
import Jewel.Engine.Interfaces.INameSpace;
import Jewel.Engine.Security.Password;
import Jewel.Web.interfaces.LoginService;
import Jewel.Web.shared.JewelWebException;
import Jewel.Web.shared.LoginDomain;
import Jewel.Web.shared.LoginResponse;

public class LoginServiceImpl
	extends EngineImplementor
	implements LoginService
{
	private static final long serialVersionUID = 1;

	public LoginDomain[] GetDomains()
		throws JewelWebException
	{
		IEntity lrefNameSpace;
		ArrayList<LoginDomain> larrNames;
        MasterDB ldb;
        ResultSet lrs;
        INameSpace lrefNSpace;
        LoginDomain lobjAux;

        try
        {
			lrefNameSpace = Entity.GetInstance(EntityGUIDs.E_NameSpace);

			larrNames = new ArrayList<LoginDomain>();

	        ldb = new MasterDB();
        }
        catch (Throwable e)
        {
        	throw new JewelWebException(e.getMessage(), e);
        }

        try
        {
	        lrs = lrefNameSpace.SelectAll(ldb);
        }
        catch (Throwable e)
        {
        	try { ldb.Disconnect(); } catch (SQLException e1) {}
        	throw new JewelWebException(e.getMessage(), e);
        }

        try
        {
	        while (lrs.next())
	        {
	            lrefNSpace = NameSpace.GetInstance(lrs);
	            if (lrefNSpace.getKey().equals(NameSpaceGUIDs.N_MADDS))
	                continue;

	            lobjAux = new LoginDomain();
	            lobjAux.mstrName = lrefNSpace.getName();
	            lobjAux.mstrKey = lrefNSpace.getKey().toString();
	            larrNames.add(lobjAux);
	        }

            lobjAux = new LoginDomain();
            lobjAux.mstrName = "MADDS";
            lobjAux.mstrKey = NameSpaceGUIDs.N_MADDS.toString();
	        larrNames.add(lobjAux);
        }
        catch (Throwable e)
        {
        	try { lrs.close(); } catch (SQLException e1) {}
        	try { ldb.Disconnect(); } catch (SQLException e1) {}
        	throw new JewelWebException(e.getMessage(), e);
        }

        try
        {
	        lrs.close();
        }
        catch (Throwable e)
        {
        	try { ldb.Disconnect(); } catch (SQLException e1) {}
        	throw new JewelWebException(e.getMessage(), e);
        }

        try
        {
			ldb.Disconnect();
		}
        catch (Throwable e)
        {
        	throw new JewelWebException(e.getMessage(), e);
		}

        return larrNames.toArray(new LoginDomain[larrNames.size()]);
	}

	public String CheckLogin(LoginResponse pobjResp)
		throws JewelWebException
	{
        IEntity lrefUser;
        MasterDB ldb;
        ResultSet lrs;
        int[] larrMembers;
        java.lang.Object[] larrParams;
        UUID lidUser;
        UUID lidNSpace;
        User lobjUser;
        boolean lbSave;

        lidUser = null;
        lidNSpace = UUID.fromString(pobjResp.mstrDomain);

        larrMembers = new int[2];
        larrMembers[0] = Miscellaneous.Username_In_User;
        larrMembers[1] = Miscellaneous.Password_In_User;
        larrParams = new java.lang.Object[2];
        larrParams[0] = "!" + pobjResp.mstrUser;

        try
        {
	        if ((pobjResp.mstrPasswd == null) || (pobjResp.mstrPasswd.equals("")))
	            larrParams[1] = null;
	        else
	            larrParams[1] = new Password(pobjResp.mstrPasswd, false);

	        lrefUser = Entity.GetInstance(Engine.FindEntity(lidNSpace, ObjectGUIDs.O_User));
	
	        lidUser = null;
	        lobjUser = null;
	        lbSave = false;

	        ldb = new MasterDB();

	        lrs = lrefUser.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
	        if (lrs.next())
	        {
	            lidUser = UUID.fromString(lrs.getString(1));
	            if (lrs.next())
	                throw new JewelWebException("Unexpected: Username is not unique!");
	        }
	        lrs.close();

	        if ( (lidUser == null) && (larrParams[1] != null) )
	        {
				((Password)larrParams[1]).setShort(pobjResp.mstrPasswd);
				lrs = lrefUser.SelectByMembers(ldb, larrMembers, larrParams, new int[0]);
				if (lrs.next())
				{
					lobjUser = (User)Engine.GetWorkInstance(Engine.FindEntity(lidNSpace, ObjectGUIDs.O_User), lrs);
					lidUser = lobjUser.getKey();
					if (lrs.next())
						throw new JewelWebException("Unexpected: Username is not unique!");
					lbSave = true;
				}
				lrs.close();
				if ( lbSave )
				{
					lobjUser.setAt(Jewel.Engine.Constants.Miscellaneous.Password_In_User, new Password(pobjResp.mstrPasswd, false));
					lobjUser.SaveToDb(ldb);
				}
	        }

	        ldb.Disconnect();

	        if ( lidUser == null )
	            throw new JewelWebException("Invalid Username or Password!");

	        getSession().setAttribute("UserID", lidUser);
	        getSession().setAttribute("UserNSpace", lidNSpace);
	
	        NameSpace.GetInstance(lidNSpace).DoLogin(lidUser, false);
	        
	        return User.GetInstance(lidNSpace, lidUser).getDisplayName();
        }
        catch (JewelWebException e)
        {
        	throw e;
        }
        catch (Throwable e)
        {
        	throw new JewelWebException(e.getMessage(), e);
        }
	}
}
