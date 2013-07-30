package Jewel.Engine.DataAccess;

import java.sql.*;
import java.util.*;

public class SQLServer
{
	private static class Pool
	{
		private static class PoolItem
		{
			private String mstrServer;
			private String mstrDB;
			private String mstrUser;
			private String mstrPassword;
			private Connection mcon;
			private boolean mbFree;

			public PoolItem(String pstrServer, String pstrDB, String pstrUser, String pstrPassword)
				throws SQLException
			{
				mstrServer = pstrServer;
				mstrDB = pstrDB;
				mstrUser = pstrUser;
				mstrPassword = pstrPassword;
				mcon = DriverManager.getConnection("jdbc:sqlserver://" + pstrServer + ";databaseName=" + pstrDB +
						";responseBuffering=adaptive;selectMethod=cursor", pstrUser, pstrPassword);
				mbFree = true;
			}

			public boolean Check(String pstrServer, String pstrDB, String pstrUser, String pstrPassword)
			{
				return mbFree && mstrServer.equals(pstrServer) && mstrDB.equals(pstrDB) && mstrUser.equals(pstrUser) &&
						mstrPassword.equals(pstrPassword);
			}

			public Connection GetCon()
			{
				mbFree = false;
				return mcon;
			}

			public void Release()
			{
				mbFree = true;
			}
		}

		private static PoolItem[] garrPool;
		private static int glngSize = -1;

		public static int GetSlot(String pstrServer, String pstrDB, String pstrUser, String pstrPassword)
			throws SQLException
		{
			int i;
			PoolItem[] larrAux;
			int llngNewSize;

			if ( glngSize < 0 )
			{
				try
				{
					Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				}
				catch (ClassNotFoundException e)
				{
					throw new SQLException("Driver not found.");
				}
				garrPool = new PoolItem[1];
				glngSize = 1;
			}

			for ( i = 0; i < glngSize; i++ )
			{
				if ( garrPool[i] == null )
				{
					garrPool[i] = new PoolItem(pstrServer, pstrDB, pstrUser, pstrPassword);
					return i;
				}

				if ( garrPool[i].Check(pstrServer, pstrDB, pstrUser, pstrPassword) )
					return i;
			}

			llngNewSize = glngSize * 2;
			larrAux = new PoolItem[llngNewSize];
			for ( i = 0; i < glngSize; i++ )
			{
				larrAux[i] = garrPool[i];
				garrPool[i] = null;
			}
			i = glngSize;
			garrPool = larrAux;
			glngSize = llngNewSize;

			garrPool[i] = new PoolItem(pstrServer, pstrDB, pstrUser, pstrPassword);
			return i;
		}

		public static Connection OpenConnection(int plngSlot)
		{
			return garrPool[plngSlot].GetCon();
		}

		public static void CloseConnection(int plngSlot)
		{
			garrPool[plngSlot].Release();
		}
	}

	private int mlngSlot;
	private Connection mcon;
	private boolean mbInTrans;

	public SQLServer(String pstrServer, String pstrDB, String pstrUser, String pstrPassword)
		throws SQLException
	{
		synchronized(Pool.class)
		{
			mlngSlot = Pool.GetSlot(pstrServer, pstrDB, pstrUser, pstrPassword);
			mcon = Pool.OpenConnection(mlngSlot);
		}
		mcon.setAutoCommit(true);
		mbInTrans = false;
	}

	public final void Disconnect()
		throws SQLException
	{
		if (mbInTrans)
			Rollback();
		synchronized(Pool.class)
		{
			Pool.CloseConnection(mlngSlot);
		}
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

		lcmd = mcon.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
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
