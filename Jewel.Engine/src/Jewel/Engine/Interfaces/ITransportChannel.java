package Jewel.Engine.Interfaces;

import java.util.*;

import Jewel.Engine.SysObjects.*;

public interface ITransportChannel
	extends IJewelBase
{
    String getName();
    UUID getType();
    String getAddress();
    String getLogin();
    String getPassword() throws JewelEngineException;
    String[] ListFiles(String pstrLocation, String pstrFilter) throws JewelEngineException;
    FileXfer GetFile(String pstrLocation, String pstrFileName) throws JewelEngineException;
    void PutFile(String pstrLocation, FileXfer pobjFile) throws JewelEngineException;
}
