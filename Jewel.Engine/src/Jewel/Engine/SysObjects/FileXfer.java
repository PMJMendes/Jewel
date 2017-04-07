package Jewel.Engine.SysObjects;

import java.io.*;
import java.nio.charset.*;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.poi.util.IOUtils;

public class FileXfer
{
	private static final int SIZEOFINT = 4;

	protected int mlngLen;
	protected String mstrContentType;
	protected String mstrFileName;
	protected byte[] marrData;
	private transient boolean mbWasCompacted;

	public FileXfer(int plngLen, String pstrContentType, String pstrFileName, InputStream prefData)
		throws IOException
	{
        mbWasCompacted = false;

		mlngLen = plngLen;
		mstrContentType = pstrContentType;
		mstrFileName = pstrFileName;
		marrData = IOUtils.toByteArray(prefData);
	}

	public FileXfer(byte[] parrData)
	{
		LoadFromVarData(parrData);
	}

	public byte[] GetVarData()
	{
		Deflater lobjDef;
		ByteArrayOutputStream lout;
		byte[] larrAux, larrData, larrContentType, larrFileName;
		int llngLen, llngStart;
		Charset lobjEncoder;

        lobjDef = new Deflater();
        lobjDef.setInput(marrData);
        lout = new ByteArrayOutputStream(mlngLen);
        lobjDef.finish();
        larrAux = new byte[1024];
        while (!lobjDef.finished() )
        {
        	llngLen = lobjDef.deflate(larrAux);
        	lout.write(larrAux, 0, llngLen);
        }
//        try { lsAux.close(); } catch (Throwable e) {} // JMMM: Closing a ByteArrayOutputStream has no effect anyway
        larrData = lout.toByteArray();
        llngLen = larrData.length;
		lobjEncoder = Charset.forName("UTF-8");
		larrContentType = ("!" + mstrContentType).getBytes(lobjEncoder);
		larrFileName = mstrFileName.getBytes(lobjEncoder);
		larrAux = new byte[llngLen + 3*SIZEOFINT + larrContentType.length + larrFileName.length];
        llngStart = 0;

        System.arraycopy(new byte[] {(byte)llngLen, (byte)(llngLen >>> 8), (byte)(llngLen >>> 16), (byte)(llngLen >>> 24)},
        		0, larrAux, llngStart, SIZEOFINT);
        llngStart += SIZEOFINT;

        System.arraycopy(new byte[] {(byte)larrContentType.length, (byte)(larrContentType.length >>> 8),
        		(byte)(larrContentType.length >>> 16), (byte)(larrContentType.length >>> 24)},
        		0, larrAux, llngStart, SIZEOFINT);
        llngStart += SIZEOFINT;
        System.arraycopy(larrContentType, 0, larrAux, llngStart, larrContentType.length);
        llngStart += larrContentType.length;

        System.arraycopy(new byte[] {(byte)larrFileName.length, (byte)(larrFileName.length >>> 8),
        		(byte)(larrFileName.length >>> 16), (byte)(larrFileName.length >>> 24)},
        		0, larrAux, llngStart, SIZEOFINT);
        llngStart += SIZEOFINT;
        System.arraycopy(larrFileName, 0, larrAux, llngStart, larrFileName.length);
        llngStart += larrFileName.length;

        System.arraycopy(larrData, 0, larrAux, llngStart, llngLen);
        llngStart += llngLen;

		return larrAux;
	}

	public void LoadFromVarData(byte[] parrData)
	{
		Charset lobjEncoder;
		byte[] larrAux, larrData;
		int llngLen, llngTypeLen, llngNameLen, llngStart;
		Inflater lobjInf;
		ByteArrayOutputStream lout;

		lobjEncoder = Charset.forName("UTF-8");
        llngStart = 0;

		larrAux = new byte[SIZEOFINT];
		System.arraycopy(parrData, llngStart, larrAux, 0, SIZEOFINT);
        llngStart += SIZEOFINT;
		llngLen = ((larrAux[3] & 0xFF) << 24) + ((larrAux[2] & 0xFF) << 16) + ((larrAux[1] & 0xFF) << 8) + (larrAux[0] & 0xFF);

		larrAux = new byte[SIZEOFINT];
		System.arraycopy(parrData, llngStart, larrAux, 0, SIZEOFINT);
        llngStart += SIZEOFINT;
        llngTypeLen = (larrAux[3] << 24) + ((larrAux[2] & 0xFF) << 16) + ((larrAux[1] & 0xFF) << 8) + (larrAux[0] & 0xFF);

		larrAux = new byte[llngTypeLen];
		System.arraycopy(parrData, llngStart, larrAux, 0, llngTypeLen);
        llngStart += llngTypeLen;
        mstrContentType = new String(larrAux, lobjEncoder);

		larrAux = new byte[SIZEOFINT];
		System.arraycopy(parrData, llngStart, larrAux, 0, SIZEOFINT);
        llngStart += SIZEOFINT;
        llngNameLen = (larrAux[3] << 24) + ((larrAux[2] & 0xFF) << 16) + ((larrAux[1] & 0xFF) << 8) + (larrAux[0] & 0xFF);

        larrAux = new byte[llngNameLen];
        System.arraycopy(parrData, llngStart, larrAux, 0, llngNameLen);
        llngStart += llngNameLen;
		mstrFileName = new String(larrAux, lobjEncoder);

		larrData = new byte[llngLen];
		System.arraycopy(parrData, llngStart, larrData, 0, llngLen);
        llngStart += llngLen;

        if ( mstrContentType.startsWith("!") )
        {
            mbWasCompacted = true;

        	mstrContentType = mstrContentType.substring(1);
        	lobjInf = new Inflater();
        	lobjInf.setInput(larrData);
        	lout = new ByteArrayOutputStream(llngLen);
        	larrAux = new byte[1024];
        	while ( !lobjInf.finished() )
        	{
        		try { llngLen = lobjInf.inflate(larrAux); } catch (DataFormatException e) {}
        		// TODO Figure out a way to handle this exception without failing LoadFromVarData, which is never meant to fail
        		lout.write(larrAux, 0, llngLen);
        	}
//          try { lsAux.close(); } catch (Throwable e) {} // JMMM: Closing a ByteArrayOutputStream has no effect anyway
        	marrData = lout.toByteArray();
        	mlngLen = marrData.length;
        }
        else
        {
            mbWasCompacted = false;

        	marrData = larrData;
        	mlngLen = llngLen;
        }
    }

	public String getContentType()
	{
		return mstrContentType;
	}

	public String getFileName()
	{
		return mstrFileName;
	}

	public int getLength()
	{
		return mlngLen;
	}

	public byte[] getData()
	{
		return marrData;
	}

	public boolean wasCompacted()
	{
		return mbWasCompacted;
	}
}
