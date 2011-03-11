package Jewel.Engine.Interfaces;

public interface IView
	extends IJewelBase
{
	IObject getEditedObject();
	IViewTab[] getTabs();
}
