package Jewel.Engine.Implementation;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.commons.net.ftp.*;

import Jewel.Engine.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Security.*;

public class TransportChannel
	extends ObjectBase
	implements ITransportChannel
{
	public static TransportChannel GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelEngineException, SQLException
	{
        return (TransportChannel)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_TransportChannel), pidKey);
	}

	public void Initialize()
	{
	}

    public String getName()
    {
        return (String)getAt(0);
    }

    public UUID getType()
    {
        return (UUID)getAt(1);
    }

    public String getAddress()
    {
        return (String)getAt(2);
    }

    public String getLogin()
    {
        if (getAt(3) == null)
            return "";

        return (String)getAt(3);
    }

    public String getPassword()
    	throws JewelEngineException
    {
        if (getAt(4) == null)
            return "";

        return SecureFunctions.Decrypt((String)getAt(4));
    }

    public String[] ListFiles(String pstrLocation, String pstrFilter)
    	throws JewelEngineException
    {
        if (getType().equals(TransportTypeGUIDs.TT_FTP))
            return ListFilesFTP(pstrLocation, pstrFilter);

        //if (mrefChannel.Type == TransportTypeGUIDs.TT_Email)
        //    return ListFilesEmail(pstrLocation, pstrFilter);

        return null;
    }

    public FileXfer GetFile(String pstrLocation, String pstrFileName)
    	throws JewelEngineException
    {
        FileXfer lxferAux;
        UUID lidEntity;
        ObjectMaster lobjAux;
        MasterDB ldb;

        lxferAux = null;

        if (getType().equals(TransportTypeGUIDs.TT_FTP))
            lxferAux = GetFileFTP(pstrLocation, pstrFileName);

        if (lxferAux != null)
        {
            try
            {
				lidEntity = Engine.FindEntity(getDefinition().getMemberOf().getKey(), ObjectGUIDs.O_FileInstance);
	            lobjAux = new ObjectMaster();
	            lobjAux.LoadAt(lidEntity);

	            lobjAux.setAt(0, pstrFileName);
	            lobjAux.setAt(1, pstrLocation);
	            lobjAux.setAt(2, "Download");
	            lobjAux.setAt(3, new Timestamp(new java.util.Date().getTime()));
	            lobjAux.setAt(4, false);
	            lobjAux.setAt(5, getKey());
	            lobjAux.setAt(6, lxferAux);

				ldb = new MasterDB();
				lobjAux.SaveToDb(ldb);
				ldb.Disconnect();
			}
            catch (JewelEngineException e)
            {
            	throw e;
            }
            catch (Exception e)
            {
            	throw new JewelEngineException(e.getMessage(), e);
			}

            DeleteFile(pstrLocation, pstrFileName);
        }

        return lxferAux;
    }

    public UUID PutFile(String pstrLocation, FileXfer pobjFile)
    	throws JewelEngineException
    {
        UUID lidEntity;
        ObjectMaster lobjAux;
        MasterDB ldb;

        if (getType().equals(TransportTypeGUIDs.TT_FTP))
            PutFileFTP(pstrLocation, pobjFile);

        if (getType().equals(TransportTypeGUIDs.TT_FileSys))
            PutFileFileSys(pstrLocation, pobjFile);

        try
        {
	        lidEntity = Engine.FindEntity(getDefinition().getMemberOf().getKey(), ObjectGUIDs.O_FileInstance);
	        lobjAux = new ObjectMaster();
	        lobjAux.LoadAt(lidEntity);

	        lobjAux.setAt(0, pobjFile.getFileName());
	        lobjAux.setAt(1, pstrLocation);
	        lobjAux.setAt(2, "Upload");
	        lobjAux.setAt(3, new Timestamp(new java.util.Date().getTime()));
	        lobjAux.setAt(4, false);
	        lobjAux.setAt(5, getKey());
	        lobjAux.setAt(6, pobjFile);

	        ldb = new MasterDB();
	        lobjAux.SaveToDb(ldb);
	        ldb.Disconnect();
		}
        catch (JewelEngineException e)
        {
        	throw e;
        }
        catch (Exception e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
		}

        return lobjAux.getKey();
    }

    public void DeleteFile(String pstrLocation, String pstrFileName)
    	throws JewelEngineException
    {
        if (getType().equals(TransportTypeGUIDs.TT_FTP))
            DeleteFileFTP(pstrLocation, pstrFileName);
    }

    private String[] ListFilesFTP(String pstrLocation, String pstrFilter)
    	throws JewelEngineException
    {
    	FTPClient lcli;
        String[] larrResult;
        int llngCode;

        pstrLocation = ((String)("/" + pstrLocation.replace("//", "/") + "/")).replace("//", "/").replace("//", "/");

        lcli = new FTPClient();
        try
        {
	        lcli.connect("ftp://" + getAddress());
	        if ( !FTPReply.isPositiveCompletion(lcli.getReplyCode()) )
	        	throw new JewelEngineException("FTP Server " + getAddress() + " refused connection.");
	        if ( !lcli.login(getLogin(), getPassword()) )
	        {
	        	lcli.logout();
	        	throw new JewelEngineException("Login failed for FTP Server " + getAddress() + ".");
	        }

	        larrResult = lcli.listNames(pstrLocation + pstrFilter);
	        if ( larrResult != null )
	        	lcli.logout();
	        else
	        {
	        	lcli.logout();
	        	llngCode = lcli.getReplyCode();
	        	if ( llngCode != FTPReply.CODE_550 )
	        		throw new JewelEngineException("Error reading file list. FTP Server returned " + Integer.toString(llngCode) + ".");
	        	larrResult = new String[0];
	        }
        }
        catch (JewelEngineException e)
        {
        	throw e;
        }
        catch (Exception e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
        }

        return larrResult;
    }

    private FileXfer GetFileFTP(String pstrLocation, String pstrFileName)
    	throws JewelEngineException
    {
    	FTPClient lcli;
    	FTPFile[] larrFiles;
        int llngCode;
        int llngLen;
        FileXfer lxferAux;
        InputStream lstream;

        pstrLocation = ((String)("/" + pstrLocation.replace("//", "/") + "/")).replace("//", "/").replace("//", "/");

        lcli = new FTPClient();
        try
        {
	        lcli.connect("ftp://" + getAddress());
	        if ( !FTPReply.isPositiveCompletion(lcli.getReplyCode()) )
	        	throw new JewelEngineException("FTP Server " + getAddress() + " refused connection.");
	        if ( !lcli.login(getLogin(), getPassword()) )
	        {
	        	lcli.logout();
	        	throw new JewelEngineException("Login failed for FTP Server " + getAddress() + ".");
	        }

	        larrFiles = lcli.listFiles(pstrLocation + pstrFileName);
	        if ( larrFiles == null )
	        {
	        	lcli.logout();
	        	llngCode = lcli.getReplyCode();
	        	if ( llngCode == FTPReply.CODE_550 )
	        		throw new JewelEngineException("File not found.");
        		throw new JewelEngineException("Error reading file size. FTP Server returned " + Integer.toString(llngCode) + ".");
	        }
	        switch(larrFiles.length)
	        {
	        case 0:
	        	lcli.logout();
	        	throw new JewelEngineException("File not found.");
        	
	        case 1:
	        	if ( larrFiles[0] == null )
	        	{
		        	lcli.logout();
	            	throw new JewelEngineException("Unexpected: Internal FTP error parsing file entry in FTP Get.");
	        	}
	        	llngLen = (int)larrFiles[0].getSize();
	        	break;
        	
	        default:
	        	lcli.logout();
	        	throw new JewelEngineException("Error: too many files returned in FTP Get.");
	        }

	        lstream = lcli.retrieveFileStream(pstrLocation + pstrFileName);
	        if ( lstream == null )
	        {
	        	lcli.logout();
	        	throw new JewelEngineException("Error reading file in FTP Get.");
	        }

	        try
	        {
				lxferAux = new FileXfer(llngLen, "text/plain", pstrFileName, lstream);
			}
	        catch (IOException e)
	        {
	        	lcli.logout();
				throw e;
			}

	        lcli.logout();
        }
        catch (JewelEngineException e)
        {
        	throw e;
        }
        catch (Exception e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
        }

        return lxferAux;
    }

    private void DeleteFileFTP(String pstrLocation, String pstrFileName)
    	throws JewelEngineException
    {
    	FTPClient lcli;
        int llngCode;
    	boolean b;

        pstrLocation = ((String)("/" + pstrLocation.replace("//", "/") + "/")).replace("//", "/").replace("//", "/");

        lcli = new FTPClient();
        try
        {
	        lcli.connect("ftp://" + getAddress());
	        if ( !FTPReply.isPositiveCompletion(lcli.getReplyCode()) )
	        	throw new JewelEngineException("FTP Server " + getAddress() + " refused connection.");
	        if ( !lcli.login(getLogin(), getPassword()) )
	        {
	        	lcli.logout();
	        	throw new JewelEngineException("Login failed for FTP Server " + getAddress() + ".");
	        }

	        b = lcli.deleteFile(pstrLocation + pstrFileName);
	        lcli.logout();
        }
        catch (JewelEngineException e)
        {
        	throw e;
        }
        catch (Exception e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
        }

        if (!b)
        {
        	llngCode = lcli.getReplyCode();
        	if ( llngCode != FTPReply.CODE_550 )
        		throw new JewelEngineException("Error deleting file list. FTP Server returned " + Integer.toString(llngCode) + ".");
        }
    }

    private void PutFileFTP(String pstrLocation, FileXfer pobjFile)
    	throws JewelEngineException
    {
    	FTPClient lcli;
    	OutputStream lstream;

        pstrLocation = ((String)("/" + pstrLocation.replace("//", "/") + "/")).replace("//", "/").replace("//", "/");

        lcli = new FTPClient();
        try
        {
	        lcli.connect("ftp://" + getAddress());
	        if ( !FTPReply.isPositiveCompletion(lcli.getReplyCode()) )
	        	throw new JewelEngineException("FTP Server " + getAddress() + " refused connection.");
	        if ( !lcli.login(getLogin(), getPassword()) )
	        {
	        	lcli.logout();
	        	throw new JewelEngineException("Login failed for FTP Server " + getAddress() + ".");
	        }

	        lstream = lcli.storeFileStream(pstrLocation + pobjFile.getFileName());
	        if ( lstream == null )
	        {
	        	lcli.logout();
	        	throw new JewelEngineException("Error reading file in FTP Get.");
	        }
	        lstream.write(pobjFile.getData());
	        lstream.close();

	    	lcli.logout();
        }
        catch (JewelEngineException e)
        {
        	throw e;
        }
        catch (Exception e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
        }
    }

//    private String[] ListFilesFileSys(String pstrLocation, String pstrFilter)
//    {
//        //FtpWebRequest lreq;
//        //FtpWebResponse lresp;
//        //StreamReader lrdr;
//        //string lstrAux;
//        //ArrayList larrResult;
//
//        //larrResult = new ArrayList();
//
//        //pstrLocation = ((string)("/" + pstrLocation.Replace("//", "/") + "/")).Replace("//", "/").Replace("//", "/");
//
//        //lreq = (FtpWebRequest)WebRequest.Create("ftp://" + Address + pstrLocation + pstrFilter);
//        //lreq.Method = WebRequestMethods.Ftp.ListDirectory;
//        //lreq.Credentials = new NetworkCredential(Login, Password);
//        //lreq.KeepAlive = false;
//        //lrdr = null;
//        //lresp = null;
//        //try
//        //{
//        //    lresp = (FtpWebResponse)lreq.GetResponse();
//        //    lrdr = new StreamReader(lresp.GetResponseStream());
//        //    while ((lstrAux = lrdr.ReadLine()) != null)
//        //        larrResult.Add(lstrAux);
//        //}
//        //catch (WebException e)
//        //{
//        //    if (e.Message != "The remote server returned an error: (550) File unavailable (e.g., file not found, no access).")
//        //        throw e;
//        //}
//        //finally
//        //{
//        //    if (lrdr != null)
//        //        lrdr.Close();
//        //    if (lresp != null)
//        //        lresp.Close();
//        //}
//
//        //return (string[])larrResult.ToArray(typeof(string));
//
//        return new String[0];
//    }

//    private FileXfer GetFileFileSys(String pstrLocation, String pstrFileName)
//    {
//        //FtpWebRequest lreq;
//        //FtpWebResponse lresp;
//        //int llngLen;
//        //Stream lstream;
//        //FileXfer lxferAux;
//
//        //pstrLocation = ((string)("/" + pstrLocation.Replace("//", "/") + "/")).Replace("//", "/").Replace("//", "/");
//
//        //lreq = (FtpWebRequest)WebRequest.Create("ftp://" + Address + pstrLocation + pstrFileName);
//        //lreq.Method = WebRequestMethods.Ftp.GetFileSize;
//        //lreq.Credentials = new NetworkCredential(Login, Password);
//        //lreq.KeepAlive = false;
//        //lresp = (FtpWebResponse)lreq.GetResponse();
//        //llngLen = (int)lresp.ContentLength;
//        //lresp.Close();
//
//        //lreq = (FtpWebRequest)WebRequest.Create("ftp://" + Address + pstrLocation + pstrFileName);
//        //lreq.Method = WebRequestMethods.Ftp.DownloadFile;
//        //lreq.Credentials = new NetworkCredential(Login, Password);
//        //lreq.KeepAlive = false;
//        //lresp = (FtpWebResponse)lreq.GetResponse();
//        //lstream = lresp.GetResponseStream();
//
//        //lxferAux = new FileXfer(llngLen, "text/plain", pstrFileName, lstream);
//
//        //lresp.Close();
//
//        //return lxferAux;
//
//        return null;
//    }

//    private void DeleteFileFileSys(String pstrLocation, String pstrFileName)
//    {
//        //FtpWebRequest lreq;
//        //FtpWebResponse lresp;
//
//        //pstrLocation = ((string)("/" + pstrLocation.Replace("//", "/") + "/")).Replace("//", "/").Replace("//", "/");
//
//        //lreq = (FtpWebRequest)WebRequest.Create("ftp://" + Address + pstrLocation + pstrFileName);
//        //lreq.Method = WebRequestMethods.Ftp.DeleteFile;
//        //lreq.Credentials = new NetworkCredential(Login, Password);
//        //lreq.KeepAlive = false;
//        //lresp = (FtpWebResponse)lreq.GetResponse();
//        //lresp.Close();
//    }

    private void PutFileFileSys(String pstrLocation, FileXfer pobjFile)
    	throws JewelEngineException
    {
        FileOutputStream lstream;
        BufferedOutputStream lwriter;

        pstrLocation = ((String)(getAddress() + "\\" + pstrLocation.replace("\\\\", "\\") + "\\" + pobjFile.getFileName())).replace("//", "/").replace("//", "/");

        try
        {
			lstream = new FileOutputStream(pstrLocation/*, FileMode.Create, FileAccess.Write*/);
	        lwriter = new BufferedOutputStream(lstream);
	        lwriter.write(pobjFile.getData());
	        lwriter.flush();
	        lwriter.close();
		}
        catch (Throwable e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
		}
    }
}
