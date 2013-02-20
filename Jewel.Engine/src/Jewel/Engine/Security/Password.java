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

    public boolean equalsShort(Password obj)
    	throws JewelEngineException
    {
    	return ( (obj != null) &&
    			(SecureFunctions.EncryptShort(SecureFunctions.Decrypt(obj.mstrPwd)).equals(mstrPwd)) );
    }

    public void setShort(String pstrPwd)
    	throws JewelEngineException
    {
        mstrPwd = SecureFunctions.EncryptShort(pstrPwd);
    }

    public String getShort()
    	throws JewelEngineException
    {
    	return SecureFunctions.EncryptShort(SecureFunctions.Decrypt(mstrPwd));
    }
}
