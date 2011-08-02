package Jewel.Petri.Interfaces;

import java.util.UUID;

import Jewel.Engine.Interfaces.IJewelBase;

public interface IPermission
	extends IJewelBase
{
	UUID getOperation();
	UUID getProfile();
}
