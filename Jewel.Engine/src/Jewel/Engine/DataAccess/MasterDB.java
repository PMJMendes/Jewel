package Jewel.Engine.DataAccess;

import java.sql.*;

import Jewel.Engine.Constants.*;

public class MasterDB extends SQLServer
{
	private static String gstrServer =
			( System.getenv("JEWEL_DBSERVER") == null ? DBConstants.DBServer : System.getenv("JEWEL_DBSERVER") );

	public MasterDB()
		throws SQLException
	{
		super(gstrServer, DBConstants.MasterDB, DBConstants.User, DBConstants.Password);
	}
}
