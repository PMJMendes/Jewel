package Jewel.Engine.DataAccess;

import java.sql.*;

import Jewel.Engine.Constants.*;

public class MasterDB extends SQLServer
{
	private static String gstrServer =
			( System.getenv(DBConstants.Env_DBServer) == null ? DBConstants.DBServer : System.getenv(DBConstants.Env_DBServer) );
	private static String gstrMasterDB =
			( System.getenv(DBConstants.Env_MasterDB) == null ? DBConstants.MasterDB : System.getenv(DBConstants.Env_MasterDB) );

	public MasterDB()
		throws SQLException
	{
		super(gstrServer, gstrMasterDB, DBConstants.User, DBConstants.Password);
	}
}
