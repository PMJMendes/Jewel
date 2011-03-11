package Jewel.Engine.Constants;

import java.util.UUID;

public final class GUIDArrays
{
    public static final int N_Entities = 6;
    public static final UUID[] A_Entities = {EntityGUIDs.E_Entity, EntityGUIDs.E_Application, EntityGUIDs.E_NameSpace, EntityGUIDs.E_Object,
                                       EntityGUIDs.E_TypeDef, EntityGUIDs.E_ObjMember};

    public static final int N_Applications = 1;
    public static final UUID[] A_Applications = { ApplicationGUIDs.A_MADDS };

    public static final int N_NameSpaces = 1;
    public static final UUID[] A_NameSpaces = { NameSpaceGUIDs.N_MADDS };

    public static final int N_Objects = 6;
    public static final UUID[] A_Objects = {ObjectGUIDs.O_Entity, ObjectGUIDs.O_Application, ObjectGUIDs.O_NameSpace, ObjectGUIDs.O_Object,
									  ObjectGUIDs.O_TypeDef, ObjectGUIDs.O_ObjMember};

    public static final int N_TypeDefs = 0; //4;
	public static final UUID[] A_TypeDefs = {/*TypeDefGUIDs.T_ObjRef, TypeDefGUIDs.T_String, TypeDefGUIDs.T_Integer, TypeDefGUIDs.T_Boolean*/};

    public static final int N_ObjectMembers = 0; //11;
	public static final UUID[] A_ObjMembers = {/*ObjMemberGUIDs.M_01, ObjMemberGUIDs.M_02, ObjMemberGUIDs.M_03, ObjMemberGUIDs.M_04,
										 ObjMemberGUIDs.M_05, ObjMemberGUIDs.M_06, ObjMemberGUIDs.M_07, ObjMemberGUIDs.M_08,
										 ObjMemberGUIDs.M_09, ObjMemberGUIDs.M_10, ObjMemberGUIDs.M_11*/};
}
