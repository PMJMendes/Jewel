package Jewel.Engine.Constants;

import Jewel.Engine.Security.SecureFunctions;

public class DBConstants
{
	public static final String DBServer = "localhost";
	public static final String MasterDB = "MADDSMasterDB";
	public static final String User = "madds";
    public static final String Password = SecureFunctions.EncryptClosed("sdtGH63s.5t7");
}
