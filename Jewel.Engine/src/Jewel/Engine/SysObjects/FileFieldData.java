package Jewel.Engine.SysObjects;

import Jewel.Engine.Interfaces.*;

public class FileFieldData
{
    private IFileField mrefField;
    private String mstrData;
    private FileSectionData[] marrSubSection;

    public FileFieldData(IFileField prefField, String pstrData)
    {
        mrefField = prefField;
        mstrData = pstrData;
        marrSubSection = null;
    }

    public FileFieldData(IFileField prefField, FileSectionData[] parrSubSection)
    {
        mrefField = prefField;
        mstrData = null;
        marrSubSection = parrSubSection;
    }

    public IFileField getRefField()
    {
        return mrefField;
    }

    public String getData()
    {
        return mstrData;
    }

    public FileSectionData[] getSubSection()
    {
        return marrSubSection;
    }
}
