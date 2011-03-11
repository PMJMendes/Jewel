package Jewel.Engine.Interfaces;

import Jewel.Engine.SysObjects.*;

public interface IFileSection
	extends IJewelBase
{
    String getName();
    int getIndex();
    int getMaxCount();
    String getSeparator();
    String getTerminator();
    IFileSpec getSpec();
    IFileSection getParent();
    IFileSection[] getSubSections();
    IFileField[] getFields();
    FileSectionData ParseStream(java.lang.Object pstream, java.lang.Object pobjCtrl) throws JewelEngineException;
    void BuildStream(java.lang.Object pstream, FileSectionData pobjData, java.lang.Object pobjCtrl) throws JewelEngineException;
}
