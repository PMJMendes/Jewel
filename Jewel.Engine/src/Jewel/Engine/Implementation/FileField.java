package Jewel.Engine.Implementation;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.apache.poi.ss.usermodel.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class FileField
	extends ObjectBase
	implements IFileField
{
    private IFileSection mrefSection;
    private IFileSection mrefSubSection;

    public static FileField GetInstance(UUID pidNameSpace, UUID pidKey)
    	throws JewelEngineException, SQLException
	{
        return (FileField)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_FileField), pidKey);
	}

    public static FileField GetInstance(UUID pidNameSpace, ResultSet prsObject)
    	throws SQLException, JewelEngineException
	{
        return (FileField)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_FileField), prsObject);
	}

    public void Initialize()
    	throws JewelEngineException
    {
        try
        {
			mrefSection = FileSection.GetInstance(getDefinition().getMemberOf().getKey(), (UUID)getAt(4));
		}
        catch(JewelEngineException e)
        {
        	throw e;
        }
        catch(Exception e)
        {
        	throw new JewelEngineException(e.getMessage(), e);
        }
        if (((UUID)getAt(3)).equals(FileFieldTypeGUIDs.FFdT_SubSection))
            mrefSubSection = mrefSection.getSubSections()[(Integer)getAt(1)];
    }

    public String getName()
    {
        return (String)getAt(0);
    }

    public Integer getLength()
    {
    	return (Integer)getAt(5);
    }

    public FileFieldData ParseStream(java.lang.Object pstream, java.lang.Object pobjCtrl)
    	throws JewelEngineException
    {
        if (mrefSection.getSpec().getFormat().equals(FileFormatTypeGUIDs.FFT_Flat))
			try
        	{
				return ParseFlat((BufferedReader) pstream);
			}
        	catch (JewelEngineException e)
        	{
        		throw e;
			}
        	catch (Exception e)
        	{
        		throw new JewelEngineException(e.getMessage(), e);
			}

        if (mrefSection.getSpec().getFormat().equals(FileFormatTypeGUIDs.FFT_MSXL))
            return ParseXL((Sheet)pstream, (XLRC)pobjCtrl);

        return null;
    }

    public void BuildStream(java.lang.Object pstream, FileFieldData pobjData, java.lang.Object pobjCtrl)
    	throws JewelEngineException
    {
        if (mrefSection.getSpec().getFormat().equals(FileFormatTypeGUIDs.FFT_Flat))
			try
        	{
				BuildFlat((BufferedWriter)pstream, pobjData, (Boolean)pobjCtrl);
			}
        	catch (JewelEngineException e)
        	{
        		throw e;
        	}
        	catch (Exception e)
        	{
        		throw new JewelEngineException(e.getMessage(), e);
			}

        if (mrefSection.getSpec().getFormat().equals(FileFormatTypeGUIDs.FFT_MSXL))
            BuildXL((Sheet)pstream, pobjData, (XLRC)pobjCtrl);
    }

    private FileFieldData ParseFlat(BufferedReader pstream)
    	throws JewelEngineException, IOException
    {
        String lstrBuffer;
        ArrayList<FileSectionData> larrSectionData;
        FileSectionData lobjData;
        StringReader lrdr;
        int i;

        if ( getLength() != null )
            lstrBuffer = FileSpec.ReadLength(pstream, getLength());
        else
        	lstrBuffer = FileSpec.ReadToSeparator(pstream, mrefSection.getSeparator());
        if (lstrBuffer == null)
            lstrBuffer = "";

        if (mrefSubSection != null)
        {
            larrSectionData = new ArrayList<FileSectionData>();
            lrdr = new StringReader(lstrBuffer);

            for (i = 0; (mrefSubSection.getMaxCount() == -1) || (i < mrefSubSection.getMaxCount()); i++)
            {
                lobjData = mrefSubSection.ParseStream(lrdr, null);
                if (lobjData == null)
                    break;
                larrSectionData.add(lobjData);
            }

            return new FileFieldData(this, larrSectionData.toArray(new FileSectionData[larrSectionData.size()]));
        }

        return new FileFieldData(this, lstrBuffer);
    }

    private void BuildFlat(BufferedWriter pstream, FileFieldData pobjData, boolean pbWriteSeparator)
    	throws IOException, JewelEngineException
    {
        int i;

        if (mrefSubSection != null)
            for (i = 0; i < pobjData.getSubSection().length; i++)
                mrefSubSection.BuildStream(pstream, pobjData.getSubSection()[i], (i < pobjData.getSubSection().length - 1));
        else
            pstream.write(pobjData.getData());

        if (pbWriteSeparator)
            FileSpec.WriteSeparator(pstream, mrefSection.getSeparator());
    }

    private FileFieldData ParseXL(Sheet pobjSheet, XLRC pXLRC)
    {
    	Row lobjRow;
        Cell lobjRange;
        String lstrBuffer;

        lobjRow = pobjSheet.getRow(pXLRC.R + 2);
        if ( lobjRow == null )
            lobjRow = pobjSheet.createRow(pXLRC.R + 2);
        lobjRange = lobjRow.getCell(pXLRC.C + 1);
        if ( lobjRange == null )
            lobjRange = lobjRow.createCell(pXLRC.C + 1);
        lstrBuffer = lobjRange.getStringCellValue();

        return new FileFieldData(this, lstrBuffer);
    }

    private void BuildXL(Sheet pobjSheet, FileFieldData pobjData, XLRC pXLRC)
    {
    	Row lobjRow;
        Cell lobjRange;

        lobjRow = pobjSheet.getRow(pXLRC.R + 1);
        if ( lobjRow == null )
            lobjRow = pobjSheet.createRow(pXLRC.R + 1);
        lobjRange = lobjRow.getCell(pXLRC.C);
        if ( lobjRange == null )
            lobjRange = lobjRow.createCell(pXLRC.C);
        lobjRange.setCellValue(pobjData.getData());
    }
}
