package Jewel.Engine.Constants;

import Jewel.Engine.Security.SecureFunctions;

public class DBConstants
{
	public static final String Env_DBServer = "JEWEL_DBSERVER";
	public static final String Env_MasterDB = "JEWEL_MASTERDB";
	public static final String Env_LargeCache = "JEWEL_LARGECACHE";

	public static final String DBServer = "localhost";
	public static final String MasterDB = "JewelMasterDB";
	public static final String User = "madds";
    public static final String Password = SecureFunctions.EncryptClosed("sdtGH63s.5t7");
}
