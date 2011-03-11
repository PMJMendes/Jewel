package Jewel.Engine.Interfaces;

import java.sql.*;
import java.util.*;

import Jewel.Engine.SysObjects.JewelEngineException;

public interface ITypeDef
	extends IJewelBase
{
	String TypeForCreate();
	String TranslateValue(Object pobjValue, boolean pbForFilter, ArrayList<Blob> parrParams) throws JewelEngineException;

//	IMADDSCtl BuildForForm();
}
