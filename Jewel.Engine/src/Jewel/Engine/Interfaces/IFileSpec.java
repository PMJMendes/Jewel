package Jewel.Engine.Interfaces;

import java.util.*;

import Jewel.Engine.SysObjects.*;

public interface IFileSpec
	extends IJewelBase
{
    String getName();
    UUID getFormat();
    IFileSection[] getSections();
    FileData ParseFile(FileXfer prefFile) throws JewelEngineException;
    FileXfer BuildFile(FileData pobjFile, String pstrFName) throws JewelEngineException;
}
