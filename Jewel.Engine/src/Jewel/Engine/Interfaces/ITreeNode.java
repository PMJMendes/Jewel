package Jewel.Engine.Interfaces;

import java.util.*;

import Jewel.Engine.SysObjects.JewelEngineException;

public interface ITreeNode
	extends IJewelBase
{
	String getName();
	UUID getType();
	UUID getFormID();
    UUID getReportID();
    String getAssembly() throws JewelEngineException;
	String getClassName() throws JewelEngineException;
	String getMethod();
	INameSpace getNodeNameSpace() throws JewelEngineException;
    ITreeNode[] getChildren();
}
