package Jewel.Engine.Interfaces;

import java.util.*;

import Jewel.Engine.SysObjects.*;

public interface IEngineImpl
{
    Cache GetCache(boolean pbGlobal) throws JewelEngineException;
    void ResetGlobalCache() throws JewelEngineException;

    UUID getCurrentUser();
    UUID getCurrentNameSpace();
    Hashtable<String, Object> getUserData();

    String getCurrentPath();
    void UnloadEngine();

    void OutputFile(FileXfer pobjFile);
}
