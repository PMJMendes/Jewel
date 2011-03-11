package Jewel.Engine.Interfaces;

public interface IApplication
	extends IJewelBase
{
    String getAssemblyName();
    Package getAssembly();
    String getStaticClass();
    String getLoginMethod();
}
