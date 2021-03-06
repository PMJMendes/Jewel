package Jewel.Engine.Interfaces;

import Jewel.Engine.Security.*;
import Jewel.Engine.SysObjects.JewelEngineException;

public interface IUser
	extends IJewelBase
{
	IProfile getProfile();
    String getFullName();
    String getDisplayName();
    String getUserName();
    Password getPassword() throws JewelEngineException;
    boolean CheckPassword(Password pobjPwd) throws JewelEngineException;
}
