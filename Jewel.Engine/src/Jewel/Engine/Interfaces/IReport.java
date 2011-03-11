package Jewel.Engine.Interfaces;

import java.util.*;

public interface IReport
	extends IJewelBase
{
    UUID getParamForm();
	String getAssembly();
	String getClassName();
	String getMethod();
}
