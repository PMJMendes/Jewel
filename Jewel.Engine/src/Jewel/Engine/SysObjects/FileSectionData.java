package Jewel.Engine.SysObjects;

import Jewel.Engine.Interfaces.*;

public class FileSectionData
{
    private IFileSection mrefSection;
    private FileFieldData[] marrData;
    private FileSectionData[][] marrSubData;

    public FileSectionData(IFileSection prefSection, FileFieldData[] parrData)
    {
        mrefSection = prefSection;
        marrData = parrData;
        marrSubData = null;
    }

    public FileSectionData(IFileSection prefSection, FileSectionData[][] parrSubData)
    {
        mrefSection = prefSection;
        marrData = null;
        marrSubData = parrSubData;
    }

    public IFileSection getRefSection()
    {
        return mrefSection;
    }

    public FileFieldData[] getData()
    {
        return marrData;
    }

    public FileSectionData[][] getSubData()
    {
        return marrSubData;
    }
}
