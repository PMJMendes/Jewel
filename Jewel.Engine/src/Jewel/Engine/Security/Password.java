package Jewel.Engine.Security;

import java.io.Serializable;

import Jewel.Engine.SysObjects.JewelEngineException;

public final class Password
	implements Serializable
{
	private static final long serialVersionUID = 1L;

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
    
    public boolean equals(java.lang.Object obj)
    {
    	return ( (obj != null) && (obj instanceof Password) && (mstrPwd.equals(((Password)obj).mstrPwd)) );
    }

    public boolean equalsWrong(Password obj)
    	throws JewelEngineException
    {
    	return ( (obj != null) && (SecureFunctions.EncryptWrong(SecureFunctions.Decrypt(obj.mstrPwd)).equals(mstrPwd)) );
    }

    public void setWrong(String pstrPwd)
    	throws JewelEngineException
    {
        mstrPwd = SecureFunctions.EncryptWrong(pstrPwd);
    }

    public String getWrong()
    	throws JewelEngineException
    {
    	return SecureFunctions.EncryptWrong(SecureFunctions.Decrypt(mstrPwd));
    }
}
