package Jewel.Engine.Interfaces;

import java.util.*;

public interface IViewTab
	extends IJewelBase
{
	String getName();
	UUID getType();
	UUID getFormID();
	UUID getQueryID();
}
