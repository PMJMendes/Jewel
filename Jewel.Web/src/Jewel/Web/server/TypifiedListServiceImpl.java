package Jewel.Web.server;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.Implementation.Entity;
import Jewel.Web.interfaces.TypifiedListService;
import Jewel.Web.shared.JewelWebException;
import Jewel.Web.shared.TypifiedListItem;

public class TypifiedListServiceImpl
	extends EngineImplementor
	implements TypifiedListService
{
	private static final long serialVersionUID = 1L;

	public TypifiedListItem[] getListItems(String pstrNSpace, String pstrListId)
		throws JewelWebException
	{
		UUID lidListRef;
        MasterDB ldb;
        int[] larrSorts;
        ResultSet lrsItems;
		ArrayList<TypifiedListItem> larrAux;
		TypifiedListItem lobjAux;

		if ( Engine.getCurrentUser() == null )
			return null;

		larrAux = new ArrayList<TypifiedListItem>();
		larrSorts = new int[1];
		larrSorts[0] = 0;

		try
		{
			lidListRef = Engine.FindEntity(UUID.fromString(pstrNSpace), UUID.fromString(pstrListId));
			ldb = new MasterDB();
		}
		catch (Throwable e)
		{
			throw new JewelWebException(e.getMessage(), e);
		}

        try
        {
	        lrsItems = Entity.GetInstance(lidListRef).SelectAllSort(ldb, larrSorts);
		}
		catch (Throwable e)
		{
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			throw new JewelWebException(e.getMessage(), e);
		}

		try
		{
	        while (lrsItems.next())
	        {
	        	lobjAux = new TypifiedListItem();
	        	lobjAux.id = UUID.fromString(lrsItems.getString(1)).toString();
	        	lobjAux.value = lrsItems.getString(2);
	        	larrAux.add(lobjAux);
	        }
        }
        catch (Throwable e)
        {
			try { lrsItems.close(); } catch (Throwable e1) {}
			try { ldb.Disconnect(); } catch (Throwable e1) {}
        	throw new JewelWebException(e.getMessage(), e);
        }

        try
        {
        	lrsItems.close();
        }
		catch (Throwable e)
		{
			try { ldb.Disconnect(); } catch (Throwable e1) {}
			throw new JewelWebException(e.getMessage(), e);
		}

		try
		{
			ldb.Disconnect();
		}
		catch (Throwable e)
		{
			throw new JewelWebException(e.getMessage(), e);
		}

		return larrAux.toArray(new TypifiedListItem[larrAux.size()]);
	}
}
