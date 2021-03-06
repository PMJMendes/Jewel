package Jewel.Engine.SysObjects;

import java.util.*;

public class CodeExecuter
{
//    private static String gstrPath;
//	private static Hashtable<String, Package> garrAssemblies;

	private CodeExecuter()
	{
	}

	public static Package LoadAssembly(String pstrAssembly)
	{
//		Package lass;
//		URLClassLoader lobjLoader;
//
//		if ( garrAssemblies == null )
//			garrAssemblies = new Hashtable<String, Package>();
//
//		if ( garrAssemblies.get(pstrAssembly) != null )
//			return (Package)garrAssemblies.get(pstrAssembly);
//
//		lass = Package.getPackage(pstrAssembly.replaceAll("MADDS", "Jewel"));
//		if (lass != null)
//		{
//			garrAssemblies.put(pstrAssembly, lass);
//			return lass;
//		}
//
//        if (gstrPath == null)
//            gstrPath = System.getProperty("user.dir");
//
//        lobjLoader = new URLClassLoader(new URL[]{new File("jar:file://" + pstrAssembly + ".jar!/").toURI().toURL()});
//        lass = lobjLoader.
//            lass = URLClassLoader.LoadFile(gstrPath + pstrAssembly + ".dll");
//        }
//        catch
//        {
//            lass = null;
//        }
//
//		garrAssemblies[pstrAssembly] = lass;
//
//		return lass;
		return CodeExecuter.class.getPackage();
	}

    public static String ExecuteGeneral(String pstrAssembly, String pstrType, String pstrMethod, Class<?>[] parrTypes, Object[] parrParams)
    {
    	pstrType = pstrType.replaceAll("MADDS", "Jewel");

        try
        {
			Class.forName(pstrType).getMethod(pstrMethod, parrTypes).invoke(null, parrParams);
		}
        catch (Throwable e)
        {
            while (e.getCause() != null)
                e = e.getCause();

            return e.getMessage();
        }

        return "";
    }

	public static String ExecuteObject(String pstrAssembly, String pstrType, String pstrMethod, UUID pidNameSpace, UUID pidKey, Object[] parrParams)
	{
		Class<?>[] larrTypes;
		java.lang.Object[] larrParams;

		larrTypes = new Class<?>[3];
		larrTypes[0] = UUID.class;
		larrTypes[1] = UUID.class;
		larrTypes[2] = java.lang.Object[].class;

        larrParams = new java.lang.Object[3];
		larrParams[0] = pidNameSpace;
		larrParams[1] = pidKey;
		larrParams[2] = parrParams;

        return ExecuteGeneral(pstrAssembly, pstrType, pstrMethod, larrTypes, larrParams);
	}

	public static String ExecuteStatic(String pstrAssembly, String pstrType, String pstrMethod, UUID pidNameSpace)
	{
		Class<?>[] larrTypes;
		java.lang.Object[] larrParams;

		larrTypes = new Class<?>[1];
		larrTypes[0] = UUID.class;

		larrParams = new java.lang.Object[1];
		larrParams[0] = pidNameSpace;

        return ExecuteGeneral(pstrAssembly, pstrType, pstrMethod, larrTypes, larrParams);
    }

	public static String ExecuteAction(String pstrAssembly, String pstrType, String pstrMethod, UUID pidNameSpace, Object[] parrParams)
	{
		Class<?>[] larrTypes;
		java.lang.Object[] larrParams;

		larrTypes = new Class<?>[2];
		larrTypes[0] = UUID.class;
		larrTypes[1] = java.lang.Object[].class;

		larrParams = new java.lang.Object[2];
		larrParams[0] = pidNameSpace;
		larrParams[1] = parrParams;

        return ExecuteGeneral(pstrAssembly, pstrType, pstrMethod, larrTypes, larrParams);
    }

	public static String ExecuteReport(String pstrAssembly, String pstrType, String pstrMethod, UUID pidNameSpace, int[] parrMembers, Object[] parrValues, UUID pidRefObj, Object pobjResults)
	{
		Class<?>[] larrTypes;
		java.lang.Object[] larrParams;

		larrTypes = new Class<?>[5];
		larrTypes[0] = UUID.class;
		larrTypes[1] = int[].class;
		larrTypes[2] = java.lang.Object[].class;
		larrTypes[3] = UUID.class;
        larrTypes[4] = java.lang.Object.class;

		larrParams = new java.lang.Object[5];
		larrParams[0] = pidNameSpace;
		larrParams[1] = parrMembers;
		larrParams[2] = parrValues;
		larrParams[3] = pidRefObj;
        larrParams[4] = pobjResults;

        return ExecuteGeneral(pstrAssembly, pstrType, pstrMethod, larrTypes, larrParams);
    }

    public static String ExecuteLogin(String pstrAssembly, String pstrType, String pstrMethod, UUID pidNameSpace, UUID pidUser, boolean pbNested)
    {
        Class<?>[] larrTypes;
        java.lang.Object[] larrParams;

        larrTypes = new Class<?>[3];
        larrTypes[0] = UUID.class;
        larrTypes[1] = UUID.class;
        larrTypes[2] = boolean.class;

        larrParams = new java.lang.Object[3];
        larrParams[0] = pidNameSpace;
        larrParams[1] = pidUser;
        larrParams[2] = pbNested;

        return ExecuteGeneral(pstrAssembly, pstrType, pstrMethod, larrTypes, larrParams);
    }
}
