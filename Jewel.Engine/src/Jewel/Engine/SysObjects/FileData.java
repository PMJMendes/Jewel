package Jewel.Engine.SysObjects;

import Jewel.Engine.Interfaces.*;

public class FileData
{
    private IFileSpec mrefSpec;
    private FileSectionData[][] marrData;

    public FileData(IFileSpec prefSpec, FileSectionData[][] parrData)
    {
        mrefSpec = prefSpec;
        marrData = parrData;
    }

    public IFileSpec getRefSpec()
    {
        return mrefSpec;
    }

    public FileSectionData[][] getData()
    {
        return marrData;
    }
}
