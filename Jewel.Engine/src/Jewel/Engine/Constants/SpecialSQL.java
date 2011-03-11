package Jewel.Engine.Constants;

public final class SpecialSQL
{
	public static final String ObjectSelect = "SELECT [t1].[PK] [PK], [t1].[ObjName] [Name], [t1].[ObjComments] [Comments], " +
		"[t1].[MainTable] [Table], [t1].[ClassName] [Class Name], [t1].[FKApplication] [Application] " +
		"FROM [madds].[tblObjects] [t1]";

	public static final String NameSpaceSelect = "SELECT [t1].[PK] [PK], [t1].[SpaceName] [Name], [t1].[SpaceComments] [Comments], " +
	    "[t1].[DBUserName] [DB User], [t1].[FKParentSpace] [Parent Space], [t1].[FKApplication] [Application] " +
	    "FROM [madds].[tblNameSpaces] [t1]";

	public static final String EntitySelect = "SELECT [t1].[PK] [PK], [t1].[FKNameSpace] [Name Space], [t1].[FKObject] [Object] " +
	    "FROM [madds].[tblEntities] [t1]";

	public static final String TypeDefSelect = "SELECT [t1].[PK] [PK], [t1].[TypeName] [Name], [t1].[TypeComments] [Comments], " +
	    "[t1].[SQLGen] [SQL Generation] FROM [madds].[tblTypeDefs] [t1]";

	public static final String ObjMemberSelect = "SELECT [t1].[PK] [PK], [t1].[FKObject] [Owner], [t1].[NOrder], [t1].[MemberName] [Name], " +
	    "[t1].[MemberComments] [Comments], [t1].[FKTypeDef] [Type], [t1].[Size] [Size], [t1].[FKRefersTo] [Refers To], " +
	    "[t1].[Nullable] [Can Be Null], [t1].[Unique] [Unique], [t1].[TableColumn] [Column], [t1].[Precision] [Precision] " +
	    "FROM [madds].[tblObjectMembers] [t1]";

	public static final String ApplicationSelect = "SELECT [t1].[PK] [PK], [t1].[AppName] [Name], [t1].[AppAssembly] [Assembly], " +
	    "[t1].[AppComments] [Comments], [t1].[ClassName] [Class Name], [t1].[LoginMethod] [Login Method] " +
	    "FROM [madds].[tblApplications] [t1]";

	public static final String ObjMemberBuild = " WHERE [t1].[FKObject] = ";
	public static final String ObjMemberSort = " ORDER BY [t1].[NOrder] ASC";
}
