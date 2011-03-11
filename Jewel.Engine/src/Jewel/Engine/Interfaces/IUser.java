package Jewel.Engine.Interfaces;

import Jewel.Engine.Security.*;

public interface IUser
	extends IJewelBase
{
	IProfile getProfile();
    String getFullName();
    String getDisplayName();
    boolean CheckPassword(Password pobjPwd);
}
