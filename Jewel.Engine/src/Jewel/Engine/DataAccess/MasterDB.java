package Jewel.Engine.DataAccess;

import java.sql.*;

import Jewel.Engine.Constants.*;

public class MasterDB extends SQLServer {

	public MasterDB()
		throws SQLException
	{
		super(DBConstants.DBServer, DBConstants.MasterDB, DBConstants.User, DBConstants.Password);
	}
}
