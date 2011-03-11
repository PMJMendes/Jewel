package Jewel.Engine.Interfaces;

import java.sql.SQLException;
import java.util.*;

import Jewel.Engine.SysObjects.JewelEngineException;

public interface IFormField
	extends IJewelBase
{
	String getLabel();
	int getRow();
	int getColumn();
	int getWidth();
	int getHeight();
	int getMemberNumber();
	UUID getType();
    IObjMember getObjMemberRef();
    String getParamTag();
    UUID getSearchForm();
    UUID AuxEntity(UUID pidNSpace) throws SQLException, JewelEngineException;
//    IMADDSCtl BuildControl(UUID pidNSpace);
}
