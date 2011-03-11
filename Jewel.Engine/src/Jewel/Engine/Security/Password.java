package Jewel.Engine.Security;

import Jewel.Engine.SysObjects.JewelEngineException;

public class Password
{
	private String mstrPwd;

	public Password()
	{
		mstrPwd = "";
	}

	public Password(String pstrPwd, boolean pbEncrypted)
		throws JewelEngineException
	{
        if (pbEncrypted)
            mstrPwd = pstrPwd;
        else
            mstrPwd = SecureFunctions.Encrypt(pstrPwd);
	}

    public String GetEncrypted()
    {
        return mstrPwd;
    }

    public String GetClear()
    	throws JewelEngineException
    {
        return SecureFunctions.Decrypt(mstrPwd);
    }
}
