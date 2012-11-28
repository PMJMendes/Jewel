package Jewel.Engine.Interfaces;

import java.util.*;

import Jewel.Engine.SysObjects.JewelEngineException;

public interface IQueryParam
	extends IJewelBase
{
	String ColumnForFiltering(String pstrSeparator, HashMap<String, java.lang.Object> parrValues) throws JewelEngineException;

    int getParamAppliesTo();
    Object getParamValue() throws JewelEngineException;
}
