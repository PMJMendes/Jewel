package Jewel.Engine.Implementation;

import java.io.*;
import java.nio.charset.*;
import java.sql.*;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;

public class FileSpec
	extends ObjectBase
	implements IFileSpec
{
    private IFileSection[] marrSections;

	public static FileSpec GetInstance(UUID pidNameSpace, UUID pidKey)
		throws JewelEngineException, SQLException
	{
        return (FileSpec)Engine.GetCache(true).getAt(Engine.FindEntity(pidNameSpace, ObjectGUIDs.O_FileSpec), pidKey);
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
		ArrayList<IFileSection> larrList;

		try
		{
			ldb = new MasterDB();

			larrList = new ArrayList<IFileSection>();

			larrMembers = new int[1];
			larrMembers[0] = Miscellaneous.FKFileSpec_In_FileSection;
			larrParams = new java.lang.Object[1];
			larrParams[0] = getKey();
			larrSorts = new int[1];
			larrSorts[0] = Miscellaneous.NOrd_In_FileSection;

	        lrefFileSection = Entity.GetInstance(Engine.FindEntity(getDefinition().getMemberOf().getKey(), ObjectGUIDs.O_FileSection));

			lrsSections = lrefFileSection.SelectByMembers(ldb, larrMembers, larrParams, larrSorts);
			while ( lrsSections.next() )
	            larrList.add((IFileSection)FileSection.GetInstance(getDefinition().getMemberOf().getKey(), lrsSections));
			lrsSections.close();

	        marrSections = larrList.toArray(new IFileSection[larrList.size()]);

			ldb.Disconnect();
		}
		catch (JewelEngineException e)
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

    public UUID getFormat()
    {
        return (UUID)getAt(1);
    }

    public IFileSection[] getSections()
    {
        return marrSections;
    }

    public FileData ParseFile(FileXfer prefFile)
    	throws JewelEngineException
    {
        ByteArrayInputStream lstream;
        InputStreamReader lreader;
        Workbook lobjWBook;
        FileData lfdtAux;

        if (getFormat().equals(FileFormatTypeGUIDs.FFT_Flat))
        {
            lstream = new ByteArrayInputStream(prefFile.getData());
            lreader = new InputStreamReader(lstream, Charset.forName("Windows-1252"));
            return ParseFlat(new BufferedReader(lreader));
        }

        if (getFormat() == FileFormatTypeGUIDs.FFT_MSXL)
        {
            lstream = new ByteArrayInputStream(prefFile.getData());
            try
            {
				lobjWBook = WorkbookFactory.create(lstream);
			}
            catch (Exception e)
            {
            	throw new JewelEngineException(e.getMessage(), e);
			}
            lfdtAux = ParseXL(lobjWBook);
            return lfdtAux;
        }

        return null;
    }

    public FileXfer BuildFile(FileData pobjFile, String pstrFName)
    	throws JewelEngineException
    {
    	ByteArrayOutputStream lstream;
        OutputStreamWriter lwriter;
        Workbook lobjWBook;

        if (getFormat().equals(FileFormatTypeGUIDs.FFT_Flat))
        {
            lstream = new ByteArrayOutputStream();
            lwriter = new OutputStreamWriter(lstream, Charset.forName("Windows-1252"));
            BuildFlat(new BufferedWriter(lwriter), pobjFile);
            try
            {
				lwriter.flush();
	            return new FileXfer((int)lstream.size(), "text/plain", pstrFName, new ByteArrayInputStream(lstream.toByteArray()));
			}
            catch (Exception e)
            {
            	throw new JewelEngineException(e.getMessage(), e);
			}
        }

        if (getFormat().equals(FileFormatTypeGUIDs.FFT_MSXL))
        {
            lobjWBook = new XSSFWorkbook();
            BuildXL(lobjWBook, pobjFile);
            lstream = new ByteArrayOutputStream();
            try
            {
				lobjWBook.write(lstream);
	            return new FileXfer((int)lstream.size(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
	            		pstrFName, new ByteArrayInputStream(lstream.toByteArray()));
			}
            catch (Exception e)
            {
            	throw new JewelEngineException(e.getMessage(), e);
			}
        }

        return null;
    }

    private FileData ParseFlat(BufferedReader pstream) 
    	throws JewelEngineException
    {
        ArrayList<FileSectionData> larrInnerData;
        ArrayList<FileSectionData[]> larrOuterData;
        FileSectionData lobjData;
        int i, j;

        larrOuterData = new ArrayList<FileSectionData[]>();

        for (i = 0; i < marrSections.length; i++)
        {
            larrInnerData = new ArrayList<FileSectionData>();
            for (j = 0; (marrSections[i].getMaxCount() == -1) || (j < marrSections[i].getMaxCount()); j++)
            {
                lobjData = marrSections[i].ParseStream(pstream, null);
                if (lobjData == null)
                    break;
                larrInnerData.add(lobjData);
            }
            larrOuterData.add(larrInnerData.toArray(new FileSectionData[larrInnerData.size()]));
        }

        return new FileData(this, larrOuterData.toArray(new FileSectionData[larrOuterData.size()][]));
    }

    private void BuildFlat(BufferedWriter pstream, FileData pobjFile)
    	throws JewelEngineException
    {
        int i, j;

        for (i = 0; i < marrSections.length; i++)
            for (j = 0; j < pobjFile.getData()[i].length; j++)
                marrSections[i].BuildStream(pstream, pobjFile.getData()[i][j], (j < pobjFile.getData()[i].length - 1));
    }

    protected static String ReadToSeparator(BufferedReader pstream, String pstrSeparator)
    	throws IOException
    {
        String lstrBuffer, lstrSep;
        int lchData;

        if ( (pstrSeparator == null) || (pstrSeparator.length() == 0) )
    		return pstream.readLine();

        lchData = pstream.read();
        if (lchData == -1)
            return null;

        lstrBuffer = "";
        lstrSep = "";

        while (lchData != -1)
        {
            while ((char)lchData == pstrSeparator.charAt(0))
            {
                while ((char)lchData == pstrSeparator.charAt(lstrSep.length()))
                {
                    lstrSep += (char)lchData;
                    if (lstrSep == pstrSeparator)
                        return lstrBuffer;
                    lchData = pstream.read();
                }
                lstrBuffer += lstrSep;
                lstrSep = "";
            }
            if (lchData != -1)
            {
                lstrBuffer += (char)lchData;
                lchData = pstream.read();
            }
        }

        return lstrBuffer;
    }

    protected static String ReadLength(BufferedReader pstream, Integer plngLen)
    	throws IOException
    {
    	char[] larrBuffer;

        if ( (plngLen == null) || (plngLen < 1) )
    		return pstream.readLine();

        larrBuffer = new char[plngLen];
        pstream.read(larrBuffer, 0, plngLen);

        return new String(larrBuffer);
    }

    protected static void WriteSeparator(BufferedWriter pstream, String pstrSeparator)
    	throws IOException
    {
        if ( (pstrSeparator == null) || (pstrSeparator.length() == 0) )
            pstream.newLine();
        else
            pstream.write(pstrSeparator);
    }

    private FileData ParseXL(Workbook pobjWBook)
    	throws JewelEngineException
    {
        ArrayList<FileSectionData> larrInnerData;
        ArrayList<FileSectionData[]> larrOuterData;
        FileSectionData lobjData;
        int i, j;

        larrOuterData = new ArrayList<FileSectionData[]>();

        for (i = 0; i < marrSections.length; i++)
        {
            larrInnerData = new ArrayList<FileSectionData>();
            for (j = 0; true/*(marrSections[i].MaxCount == -1) || (j < marrSections[i].MaxCount)*/; j++)
            {
                lobjData = marrSections[i].ParseStream(pobjWBook.getSheetAt(i), j);
                if (lobjData == null)
                    break;
                larrInnerData.add(lobjData);
            }
            larrOuterData.add(larrInnerData.toArray(new FileSectionData[larrInnerData.size()]));
        }

        return new FileData(this, larrOuterData.toArray(new FileSectionData[larrOuterData.size()][]));
    }

    private void BuildXL(Workbook pobjWBook, FileData pobjFile)
    	throws JewelEngineException
    {
        int i, j;
        Sheet lobjSheet;
        Row lobjRow;
        Cell lobjRange;
        CellStyle style;
        Font font;
        
        for (i = 0; i < marrSections.length; i++)
        {
            lobjSheet = pobjWBook.createSheet(marrSections[i].getName());
            style = pobjWBook.createCellStyle();
            font = pobjWBook.createFont();
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            style.setFont(font);
            
            for (j = 0; j < marrSections[i].getFields().length; j++)
            {
                lobjRow = lobjSheet.getRow(0);
                if ( lobjRow == null )
                    lobjRow = lobjSheet.createRow(0);
                lobjRange = lobjRow.getCell(j);
                if ( lobjRange == null )
                    lobjRange = lobjRow.createCell(j);
                lobjRange.setCellValue(marrSections[i].getFields()[j].getName());
                lobjRange.setCellStyle(style);
            }

            for (j = 0; j < pobjFile.getData()[i].length; j++)
                marrSections[i].BuildStream(lobjSheet, pobjFile.getData()[i][j], j);
        }
    }
}
