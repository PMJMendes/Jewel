package Jewel.Engine.Implementation;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.poi.ss.usermodel.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class FileSection
	extends ObjectBase
	implements IFileSection
{
    private IFileSpec mrefOwnerFile;
    private IFileSection mrefParentSection;

    private IFileField[] marrFields;
    private IFileSection[] marrSubSections;

    public static FileSection GetInstance(UUID pidNameSpace, UUID pidKey)
    	throws JewelEngineException, SQLException
	{
		return (FileSection)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_FileSection), pidKey);
	}

    public static FileSection GetInstance(UUID pidNameSpace, ResultSet prsObject)
		throws SQLException, JewelEngineException
	{
        return (FileSection)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_FileSection), prsObject);
	}

    public void Initialize()
    	throws JewelEngineException
    {
        MasterDB ldb;
        ResultSet lrsSections;
        IEntity lrefFileSection;
        int[] larrMembers;
        java.lang.Object[] larrParams;
        int[] larrSorts;
        ArrayList<IFileField> larrList1;
        ArrayList<IFileSection> larrList2;
        IFileSection lrefAux;
        int i;

        try
        {
	        if (/*(base[6] == DBNull.Value) || */(getAt(6) == null))
	            mrefOwnerFile = null;
	        else
	            mrefOwnerFile = FileSpec.GetInstance(getDefinition().getMemberOf().getKey(), (UUID)getAt(6));

	        if (/*(base[5] == DBNull.Value) || */(getAt(5) == null))
	            mrefParentSection = null;
	        else
	            mrefParentSection = FileSection.GetInstance(getDefinition().getMemberOf().getKey(), (UUID)getAt(5));

	        ldb = new MasterDB();

	        larrList1 = new ArrayList<IFileField>();
	        larrList2 = new ArrayList<IFileSection>();

	        larrMembers = new int[1];
	        larrMembers[0] = Miscellaneous.FKParent_In_FileSection;
	        larrParams = new java.lang.Object[1];
	        larrParams[0] = getKey();
	        larrSorts = new int[1];
	        larrSorts[0] = Miscellaneous.NOrd_In_FileSection;

	        lrefFileSection = Entity.GetInstance(Engine.FindEntity(getDefinition().getMemberOf().getKey(), ObjectGUIDs.O_FileSection));

	        lrsSections = lrefFileSection.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
	        while (lrsSections.next())
	        {
	            lrefAux = FileSection.GetInstance(getDefinition().getMemberOf().getKey(), lrsSections);
	            for (i = larrList2.size(); i < lrefAux.getIndex(); i++)
	                larrList2.add(null);
	            larrList2.add(lrefAux);
	        }
	        lrsSections.close();

	        marrSubSections = larrList2.toArray(new IFileSection[larrList2.size()]);

	        larrMembers = new int[1];
	        larrMembers[0] = Miscellaneous.FKFileSection_In_FileField;
	        larrParams = new java.lang.Object[1];
	        larrParams[0] = getKey();
	        larrSorts = new int[1];
	        larrSorts[0] = Miscellaneous.NOrd_In_FileField;

	        lrefFileSection = Entity.GetInstance(Engine.FindEntity(getDefinition().getMemberOf().getKey(), ObjectGUIDs.O_FileField));

	        lrsSections = lrefFileSection.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
	        while (lrsSections.next())
	        {
	            larrList1.add((IFileField)FileField.GetInstance(getDefinition().getMemberOf().getKey(), lrsSections));
	        }
	        lrsSections.close();

	        marrFields = larrList1.toArray(new IFileField[larrList1.size()]);

	        ldb.Disconnect();
        }
        catch(JewelEngineException e)
        {
        	throw e;
        }
        catch (Exception e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
		}
    }

    public String getName()
    {
        return (String)getAt(0);
    }

    public int getIndex()
    {
        return (Integer)getAt(1);
    }

    public int getMaxCount()
    {
        if (/*(base[3] == DBNull.Value) || */(getAt(3) == null))
            return -1;

        return (Integer)getAt(3);
    }

    public String getSeparator()
    {
        if (/*(base[7] == DBNull.Value) || */(getAt(7) == null))
            return "";

        return (String)getAt(7);
    }

    public String getTerminator()
    {
        if (/*(base[4] == DBNull.Value) || */(getAt(4) == null))
            return "";

        return (String)getAt(4);
    }

    public IFileSpec getSpec()
    {
        if (mrefOwnerFile != null)
            return mrefOwnerFile;

        if (mrefParentSection == null)
            return null;

        return mrefParentSection.getSpec();
    }

    public IFileSection getParent()
    {
        return mrefParentSection;
    }

    public IFileSection[] getSubSections()
    {
        return marrSubSections;
    }

    public IFileField[] getFields()
    {
        return marrFields;
    }

    public FileSectionData ParseStream(java.lang.Object pstream, java.lang.Object pobjCtrl)
    	throws JewelEngineException
    {
        if (getSpec().getFormat().equals(FileFormatTypeGUIDs.FFT_Flat))
			try
        	{
				return ParseFlat((BufferedReader)pstream);
			}
	    	catch (JewelEngineException e)
	    	{
	        	throw e;
			}
	    	catch (Exception e)
	    	{
	        	throw new JewelEngineException(e.getMessage(), e);
			}

        if (getSpec().getFormat().equals(FileFormatTypeGUIDs.FFT_MSXL))
            return ParseXL((Sheet)pstream, (Integer)pobjCtrl);

        return null;
    }

    public void BuildStream(java.lang.Object pstream, FileSectionData pobjData, java.lang.Object pobjCtrl)
    	throws JewelEngineException
    {
        if (getSpec().getFormat().equals(FileFormatTypeGUIDs.FFT_Flat))
			try
        	{
				BuildFlat((BufferedWriter)pstream, pobjData, (Boolean)pobjCtrl);
			}
        	catch (Exception e)
        	{
	        	throw new JewelEngineException(e.getMessage(), e);
			}

        if (getSpec().getFormat().equals(FileFormatTypeGUIDs.FFT_MSXL))
            BuildXL((Sheet)pstream, pobjData, (Integer)pobjCtrl);
    }

    private FileSectionData ParseFlat(BufferedReader pstream)
    	throws IOException, JewelEngineException
    {
        ArrayList<FileSectionData> larrInnerData;
        ArrayList<FileSectionData[]> larrOuterData;
        ArrayList<FileFieldData> larrFieldData;
        FileFieldData lobjData;
        FileSectionData lobjSubData;
        String lstrBuffer;
        StringReader lrdr;
        int i, j;

        lstrBuffer = FileSpec.ReadToSeparator(pstream, getTerminator());
        if (lstrBuffer == null)
            return null;
        lrdr = new StringReader(lstrBuffer);

        if (marrFields.length == 0)
        {
            larrOuterData = new ArrayList<FileSectionData[]>();

            for (i = 0; i < marrSubSections.length; i++)
            {
                larrInnerData = new ArrayList<FileSectionData>();
                for (j = 0; (marrSubSections[i].getMaxCount() == -1) || (j < marrSubSections[i].getMaxCount()); j++)
                {
                    lobjSubData = marrSubSections[i].ParseStream(lrdr, null);
                    if (lobjSubData == null)
                        break;
                    larrInnerData.add(lobjSubData);
                }
                larrOuterData.add(larrInnerData.toArray(new FileSectionData[larrInnerData.size()]));
            }

            return new FileSectionData(this, (FileSectionData[][])larrOuterData.toArray(new FileSectionData[larrOuterData.size()][]));
        }

        larrFieldData = new ArrayList<FileFieldData>();
        for (i = 0; i < marrFields.length; i++)
        {
            lobjData = marrFields[i].ParseStream(lrdr, null);
            if (lobjData == null)
                break;
            larrFieldData.add(lobjData);
        }

        return new FileSectionData(this, (FileFieldData[])larrFieldData.toArray(new FileFieldData[larrFieldData.size()]));
    }

    private void BuildFlat(BufferedWriter pstream, FileSectionData pobjData, boolean pbWriteTerminator)
    	throws JewelEngineException, IOException
    {
        int i, j;

        if (marrFields.length == 0)
            for (i = 0; i < marrSubSections.length; i++)
                for (j = 0; j < pobjData.getSubData()[i].length; j++)
                    marrSubSections[i].BuildStream(pstream, pobjData.getSubData()[i][j], (j < pobjData.getSubData()[i].length - 1));
        else
            for (i = 0; i < marrFields.length; i++)
                marrFields[i].BuildStream(pstream, pobjData.getData()[i], (i < marrFields.length - 1));

        if (pbWriteTerminator)
            FileSpec.WriteSeparator(pstream, getTerminator());
    }

    private FileSectionData ParseXL(Sheet pstream, int plngLine)
    	throws JewelEngineException
    {
        ArrayList<FileFieldData> larrOuterData;
        FileFieldData lobjData;
        int i/*, j*/;
        boolean lbFound;

        larrOuterData = new ArrayList<FileFieldData>();

        lbFound = false;
        for (i = 0; i < marrFields.length; i++)
        {
            lobjData = marrFields[i].ParseStream(pstream, new XLRC(plngLine, i));
            if (lobjData.getData() != "")
                lbFound = true;
            larrOuterData.add(lobjData);
        }

        if (lbFound)
            return new FileSectionData(this, (FileFieldData[])larrOuterData.toArray(new FileFieldData[larrOuterData.size()]));
        else
            return null;
    }

    private void BuildXL(Sheet pobjSheet, FileSectionData pobjData, int plngLine)
    	throws JewelEngineException
    {
        int i;

        if (marrFields.length == 0)
            throw new JewelEngineException("MS Excel files do not accept sub-sections");

        for (i = 0; i < marrFields.length; i++)
            marrFields[i].BuildStream(pobjSheet, pobjData.getData()[i], new XLRC(plngLine, i));
    }
}
