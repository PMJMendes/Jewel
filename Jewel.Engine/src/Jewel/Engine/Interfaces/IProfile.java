package Jewel.Engine.Interfaces;

public interface IProfile
	extends IJewelBase
{
	IWorkspace[] getWorkspaces();
    IPermission[] getPermissions();
}
