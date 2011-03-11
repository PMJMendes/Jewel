package Jewel.Engine.Interfaces;

public interface IForm
	extends IJewelBase
{
	String getName();
	IObject getEditedObject();
	IQueryDef getResultsQuery();
	IFormField[] getFields();
	IFormAction[] getActions();
    String getAssembly();
    String getClassName();
    Class<?> getClassType();
}
