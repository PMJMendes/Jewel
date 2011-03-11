package Jewel.Engine.DataAccess;

import java.sql.*;
import java.util.*;

public class SQLServer
{
	@SuppressWarnings("unused")
	private static int gintInitDone = DoInit();

	private static int DoInit()
	{
		try
		{
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		}
		catch (ClassNotFoundException e)
		{
			return -1;
		}
		return 0;
	}

//	private String mstrServer;
//	private String mstrDB;
//	private String mstrUser;
//	private String mstrPassword;
	private Connection mcon;
	private boolean mbInTrans;

	public SQLServer(String pstrServer, String pstrDB, String pstrUser, String pstrPassword)
		throws SQLException
	{
//		mstrServer = pstrServer;
//		mstrDB = pstrDB;
//		mstrUser = pstrUser;
//		mstrPassword = pstrPassword;
		
		mcon = DriverManager.getConnection("jdbc:sqlserver://" + pstrServer + ";databaseName=" + pstrDB,
				pstrUser, pstrPassword);
		mcon.setAutoCommit(true);
		mbInTrans = false;
	}

	public void Disconnect()
		throws SQLException
	{
		if (mbInTrans)
			Rollback();
		mcon.close();
	}

	public void ExecuteSQL(String pstrSQL)
		throws SQLException
	{
		Statement lstmt;
		
		lstmt = mcon.createStatement();
		lstmt.execute(pstrSQL);
	}

	public void ExecuteSQL(String pstrSQL, Blob[] parrParams)
		throws SQLException
	{
		PreparedStatement lstmt;
		int i;

		lstmt = mcon.prepareStatement(pstrSQL);

		if ( parrParams != null )
		{
			for ( i = 0; i < parrParams.length; i++ )
				lstmt.setBlob(i + 1, parrParams[i]);
		}

		lstmt.execute();
	}

	public Object ExecuteFunction(String pstrSQL)
		throws SQLException
	{
		Statement lcmd;
		ResultSet lrs;
		Object lobjAux;

		lcmd = mcon.createStatement();
		lrs = lcmd.executeQuery(pstrSQL);
		if ( !lrs.next() )
		{
			lrs.close();
			return null;
		}
		
		lobjAux = lrs.getObject(1);
		lrs.close();
		return lobjAux;
	}

	public UUID ExecuteGUIDFunction(String pstrSQL)
		throws SQLException
	{
		Statement lcmd;
		ResultSet lrs;
		String lstrAux;

		lcmd = mcon.createStatement();
		lrs = lcmd.executeQuery(pstrSQL);
		if ( !lrs.next() )
		{
			lrs.close();
			return null;
		}

		lstrAux = lrs.getString(1);
		lrs.close();
		return UUID.fromString(lstrAux);
	}

	public ResultSet OpenRecordset(String pstrSQL)
		throws SQLException
	{
		Statement lcmd;

		lcmd = mcon.createStatement();
		return lcmd.executeQuery(pstrSQL);
	}

	public void BeginTrans()
		throws SQLException
	{
		BeginTrans(Connection.TRANSACTION_READ_UNCOMMITTED);
	}

	public void BeginTrans(int plevel)
		throws SQLException
	{
		mcon.setAutoCommit(false);
		mcon.setTransactionIsolation(plevel);
		mbInTrans = true;
	}

	public void Commit()
		throws SQLException
	{
		mcon.commit();
		mcon.setAutoCommit(true);
		mbInTrans = false;
	}

	public void Rollback()
		throws SQLException
	{
		mcon.rollback();
		mcon.setAutoCommit(true);
		mbInTrans = false;
	}

	public void ExecuteDDL(UUID pidNSpace, String pstrSQL)
		throws SQLException
	{
		ExecuteSQL("INSERT INTO [madds].[tblDDLLogs] ([PK], [FKNSpace], [DDLText]) VALUES(NEWID(), '" + pidNSpace.toString() + "', '" +
				pstrSQL.replace("'", "''") + "')");

		ExecuteSQL(pstrSQL);
	}
}
