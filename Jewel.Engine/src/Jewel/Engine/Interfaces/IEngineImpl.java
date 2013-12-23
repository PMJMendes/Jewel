package Jewel.Engine.Interfaces;

import java.util.Map;
import java.util.UUID;

import Jewel.Engine.SysObjects.Cache;
import Jewel.Engine.SysObjects.FileXfer;
import Jewel.Engine.SysObjects.JewelEngineException;
import Jewel.Engine.SysObjects.JewelWorkerThread;

public interface IEngineImpl
{
    Cache GetCache(boolean pbGlobal) throws JewelEngineException;
    void ResetGlobalCache() throws JewelEngineException;

    UUID getCurrentUser();
    UUID getCurrentNameSpace();
    Map<String, Object> getUserData();

    void pushNameSpace(UUID pidNameSpace, UUID pidUser) throws JewelEngineException;
    void popNameSpace() throws JewelEngineException;

    String getCurrentPath();
    void UnloadEngine();

    void OutputFile(FileXfer pobjFile);

    JewelWorkerThread getThread(Runnable prefThread);
}
