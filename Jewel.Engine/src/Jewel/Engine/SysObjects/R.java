package Jewel.Engine.SysObjects;

public class R<T>
{
	private T value; 
	 
    public R(T value)
    { 
    	this.value = value; 
    } 
 
    public T get()
    { 
        return value; 
    } 
 
    public void set(T anotherValue)
    { 
        value = anotherValue; 
    } 
 
    public String toString()
    { 
        return value.toString(); 
    } 
 
    public boolean equals(Object obj)
    { 
        return value.equals(obj); 
    } 
 
    public int hashCode()
    { 
        return value.hashCode(); 
    }
}
