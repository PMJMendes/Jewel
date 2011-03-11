package Jewel.Mobile.server;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

//import org.apache.commons.fileupload.*;
//import org.apache.commons.fileupload.disk.*;
//import org.apache.commons.fileupload.servlet.*;

import Jewel.Engine.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

public class FileServiceImpl
	extends EngineImplementor
{
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public static Hashtable<UUID, FileXfer> GetFileXferStorage()
	{
		Hashtable<UUID, FileXfer> larrAux;

        if (getSession() == null)
            return null;

        larrAux = (Hashtable<UUID, FileXfer>)getSession().getAttribute("MADDS_FileXfer_Storage");
        if (larrAux == null)
        {
        	larrAux = new Hashtable<UUID, FileXfer>();
            getSession().setAttribute("MADDS_FileXfer_Storage", larrAux);
        }

        return larrAux;
	}
}
