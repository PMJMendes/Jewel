package Jewel.Engine.Interfaces;

public interface IPermission
	extends IJewelBase
{
    IProfile getMemberOf();
    ITreeNode getTreeNode();
}
