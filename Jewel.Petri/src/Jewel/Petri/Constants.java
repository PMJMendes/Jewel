package Jewel.Petri;

import java.util.*;

public class Constants
{
    public static final UUID ObjID_PNScript     = UUID.fromString("905B49A4-B4A9-4330-825C-9E1600DBE7EA");
    public static final UUID ObjID_PNOperation  = UUID.fromString("30FBD723-9ACD-43F9-B931-9E1600DC051A");
    public static final UUID ObjID_PNController = UUID.fromString("75640640-37D1-48FC-9B3C-9E1600DC206B");
    public static final UUID ObjID_PNSink       = UUID.fromString("26F6690D-EFD4-4863-BCFA-9E1600E2C89B");
    public static final UUID ObjID_PNSource     = UUID.fromString("3189E3B3-0DCC-4580-A966-9E1600E2EE41");
    public static final UUID ObjID_PNPermission = UUID.fromString("087BB553-9DC6-4F50-8910-9F3300B72D6F");

    public static final UUID ObjID_PNProcess    = UUID.fromString("1967E563-01AD-4683-9D66-9E1700B7DF07");
    public static final UUID ObjID_PNStep       = UUID.fromString("B4E6C433-2DA6-4E66-8F3F-9E1700B8062B");
    public static final UUID ObjID_PNNode       = UUID.fromString("FF7FA5D2-A0DB-4CB3-9918-9E1700B81F23");
    public static final UUID ObjID_PNLog        = UUID.fromString("F63454DC-B63C-44EC-9E5E-9E1A01004BDB");

    public static final UUID RoleID_Autorun     = UUID.fromString("344449BF-60CB-45A6-AED3-9F090146F24D");
    public static final UUID RoleID_Triggered   = UUID.fromString("F720401E-96C5-485B-9E8C-9F090146EA2B");

    public static final UUID LevelID_Invalid    = UUID.fromString("6FDEA9C9-55E0-4214-8BC2-9EB1007E9BA5");
    public static final UUID LevelID_Override   = UUID.fromString("B2E2F120-3A20-4678-9873-A12B00C4BAD1");

    public static final int FKScript_In_Operation  = 1;
    public static final int FKSourceOp_In_Operation  = 4;
    public static final int FKScript_In_Controller = 1;
    public static final int FKOperation_In_Sink = 1;
    public static final int FKOperation_In_Source = 0;
    public static final int FKProcess_In_Step = 0;
    public static final int FKOperation_In_Step = 1;
    public static final int FKLevel_In_Step = 2;
    public static final int FKProcess_In_Node = 0;
    public static final int FKController_In_Node = 1;
    public static final int FKOperation_In_Permission = 0;
    public static final int FKScript_In_Process = 0;
    public static final int FKData_In_Process = 1;
    public static final int FKParent_In_Process = 3;
    public static final int IsRunning_In_Process = 4;
    public static final int FKClass_In_Script = 2;
    public static final int TopLevel_In_Script = 3;
    public static final int FKProcess_In_Log = 0;
    public static final int FKOperation_In_Log = 1;
    public static final int Timestamp_In_Log = 2;
    public static final int Undone_In_Log = 5;
}
