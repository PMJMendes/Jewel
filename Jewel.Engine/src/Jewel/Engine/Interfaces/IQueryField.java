package Jewel.Engine.Interfaces;

public interface IQueryField
	extends IJewelBase
{
	String ColumnForSelect();

	int getWidth();
	String getHeader();
}
