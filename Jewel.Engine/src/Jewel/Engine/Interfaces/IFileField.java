package Jewel.Engine.Interfaces;

import Jewel.Engine.SysObjects.*;

public interface IFileField
	extends IJewelBase
{
    String getName();
    FileFieldData ParseStream(java.lang.Object pstream, java.lang.Object pobjCtrl) throws JewelEngineException;
    void BuildStream(java.lang.Object pstream, FileFieldData pobjData, java.lang.Object pobjCtrl) throws JewelEngineException;
}
