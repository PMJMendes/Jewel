package Jewel.Engine.Security;

import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import Jewel.Engine.SysObjects.JewelEngineException;

public final class SecureFunctions
{
	private static final char[] garrHex = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    private static Cipher getCipher(int opMode)
    	throws JewelEngineException
    {
		DESKeySpec lspec;
		IvParameterSpec liv;
		Key lkey;
		Cipher lres;

        try
        {
        	lspec = new DESKeySpec(new byte[] {68, 7, 71, 53, 6, 87, 87, (byte) 135});
        	liv = new IvParameterSpec(new byte[] {6, 78, 98, 34, 1, 5, 7, 96});
        	lkey = SecretKeyFactory.getInstance("DES").generateSecret(lspec);

	        lres = Cipher.getInstance("DES/CBC/PKCS5Padding");
	        lres.init(opMode, lkey, liv);
        }
        catch (Throwable e)
        {
        	throw new JewelEngineException("Unexpected errors in inner Security getCipher", e);
        }

        return lres;
    }

    private static Cipher getEncCipher()
    	throws JewelEngineException
    {
    	return getCipher(Cipher.ENCRYPT_MODE);
    }

    private static Cipher getDecCipher()
    	throws JewelEngineException
    {
    	return getCipher(Cipher.DECRYPT_MODE);
    }

	private static String BitConverter(byte[] larrIn)
	{
		char[] larrOut = new char[larrIn.length * 3];
		int i, j, k;

	    i = 0; 
	    for ( j = 0; j < larrIn.length; j++ )
	    { 
	        k = larrIn[j];
	        k = (k < 0 ? k + 256 : k) & 0xFF; 
	        larrOut[i] = garrHex[k/16]; 
	        i++;
	        larrOut[i] = garrHex[k%16]; 
	        i++;
	        larrOut[i] = '-';
	        i++;
	    }
	    i--;

	    return new String(larrOut, 0, i);
	}

	public static String EncryptClosed(String pstrInput)
	{
		byte[] larrAux, larrIn, lbuffer = null;
		int i;

		if ( (pstrInput == null) || (pstrInput.equals("")) )
			return "";

		try
		{
			larrAux = pstrInput.getBytes("UTF16");
			i = larrAux.length - 2;
			larrIn = new byte[i];
			System.arraycopy(larrAux, 3, larrIn, 0, i - 1);
			larrIn[i - 1] = 0;
			lbuffer = MessageDigest.getInstance("SHA1").digest(larrIn);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		if ( (lbuffer == null) || (lbuffer.length == 0) )
			return "";

	    return BitConverter(lbuffer);
	}

    public static String Encrypt(String pstrInput)
    	throws JewelEngineException
    {
    	ByteArrayOutputStream ms;
    	CipherOutputStream encStream;
    	OutputStreamWriter sw;
    	byte[] buffer;

        ms = new ByteArrayOutputStream();
        encStream = new CipherOutputStream(ms, getEncCipher());
        sw = new OutputStreamWriter(encStream);

        try
        {
			sw.write(pstrInput);
			sw.write("\r\n");
	        sw.close();
	        buffer = ms.toByteArray();
		}
        catch (IOException e)
        {
        	throw new JewelEngineException("Unexpected IO errors in Security Encrypt", e);
		}

        return BitConverter(buffer);
    }

    public static String Decrypt(String pstrInput)
    	throws JewelEngineException
    {
        int i, llngLen, j;
    	byte[] buffer;
    	ByteArrayInputStream ms;
    	CipherInputStream decStream;
    	InputStreamReader sr;
    	BufferedReader in;
    	String val;

        llngLen = (pstrInput.length() + 1) / 3;

        buffer = new byte[llngLen];

        for (i = 0; i < llngLen; i++)
        {
        	j = Short.parseShort(pstrInput.substring(i * 3, 2 + i * 3), 16);
            buffer[i] = (byte)(j > Byte.MAX_VALUE ? j-256 : j);
        }

        try
        {
	        ms = new ByteArrayInputStream(buffer);
	        decStream = new CipherInputStream(ms, getDecCipher());
	        sr = new InputStreamReader(decStream);
	        in = new BufferedReader(sr);
	        val = in.readLine();
	        in.close();
	        sr.close();
	        decStream.close();
	        ms.close();
		}
        catch (Throwable e)
        {
        	throw new JewelEngineException("Unexpected IO errors in Security Decrypt", e);
		}

        return val;
    }

    public static String EncryptWrong(String pstrInput)
    	throws JewelEngineException
    {
    	return Encrypt(pstrInput) + "-43-9E-0B-2D-D2-9D-FF-FC";
    }
}
