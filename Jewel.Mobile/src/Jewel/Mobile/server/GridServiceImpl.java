package Jewel.Mobile.server;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.UUID;

import Jewel.Engine.Engine;
import Jewel.Engine.Constants.QueryTypeGUIDs;
import Jewel.Engine.DataAccess.MasterDB;
import Jewel.Engine.Implementation.Entity;
import Jewel.Engine.Implementation.Form;
import Jewel.Engine.Implementation.QueryDef;
import Jewel.Engine.Implementation.TypeDef;
import Jewel.Engine.Implementation.View;
import Jewel.Engine.Interfaces.IEntity;
import Jewel.Engine.Interfaces.IFormAction;
import Jewel.Engine.Interfaces.IObject;
import Jewel.Engine.Interfaces.IQueryDef;
import Jewel.Engine.Interfaces.IQueryField;
import Jewel.Engine.Interfaces.IQueryParam;
import Jewel.Engine.Interfaces.ITypeDef;
import Jewel.Engine.SysObjects.ObjectBase;
import Jewel.Engine.SysObjects.ObjectMaster;
import Jewel.Mobile.interfaces.GridService;
import Jewel.Mobile.shared.DataObject;
import Jewel.Mobile.shared.GridActionResponse;
import Jewel.Mobile.shared.GridResponse;
import Jewel.Mobile.shared.GridSaveResponse;
import Jewel.Mobile.shared.JewelMobileException;
import Jewel.Mobile.shared.ParamInfo;
import Jewel.Mobile.shared.QueryColumnObj;

public class GridServiceImpl
	extends EngineImplementor
	implements GridService
{
	private static final long serialVersionUID = 1L;

	private class QueryWSpace
	{
        private class Row
        	implements Comparable<Row>
        {
        	private QueryWSpace mrefOwner;
        	private int mlngRNum;
        	private UUID midKey;
        	private java.lang.Object[] marrValues;
        	private ObjectBase mrefCache;

        	public Row(QueryWSpace prefOwner, int plngRNum, UUID pidKey, java.lang.Object[] parrValues)
        	{
        		mrefOwner = prefOwner;
        		mlngRNum = plngRNum;
        		midKey = pidKey;
        		marrValues = parrValues;
        		mrefCache = null;
        	}

        	public int RowNum()
        	{
        		return mlngRNum;
        	}

        	public UUID getKey()
        	{
        		return midKey;
        	}

        	public String get(int i)
        	{
        		if ( marrValues[i] == null )
        			return "";
        		return marrValues[i].toString();
        	}

        	public int length()
        	{
        		return marrValues.length;
        	}

			public int compareTo(Row o)
			{
	        	int[] larrSortOrder;
				int llngResult;
				int i, j, s;

				larrSortOrder = mrefOwner.GetSortOrder();
				for ( llngResult = 0, i = 0; llngResult == 0 && i < larrSortOrder.length; i++ )
				{
					s = ( larrSortOrder[i] < 0 ? -1 : 1);
					j = larrSortOrder[i] * s;
					llngResult = get(j - 1).compareTo(o.get(j - 1)) * s;
				}
				if ( llngResult == 0 )
					return mlngRNum - o.mlngRNum;
				return llngResult;
			}

			public ObjectBase getCache()
			{
				return mrefCache;
			}

			public void setCache(ObjectBase prefCache)
			{
				mrefCache = prefCache;
			}
			
			public void setRefresh(ObjectBase prefCache, java.lang.Object[] parrValues)
			{
				midKey = prefCache.getKey();
				marrValues = parrValues;
				mrefCache = prefCache;
			}
        }

		private UUID mid;
        private IQueryDef mrefQuery;
        private UUID midNameSpace;
		private java.lang.Object mobjParam;
		private int[] marrMembers;
		private java.lang.Object[] marrValues;
        private ArrayList<Row> marrData;
		private Hashtable<String, java.lang.Object> marrExternParams;
        private int mlngPageSize = 10;
        private int mlngCurrentPage;
        private int[] marrSortOrder;
        private int mlngColCount;

		public QueryWSpace(String pstrQueryID, String pstrNameSpace)
			throws JewelMobileException
		{
			mid = UUID.randomUUID();
			try
			{
				mrefQuery = QueryDef.GetInstance(UUID.fromString(pstrQueryID));
			}
			catch (Throwable e)
			{
				throw new JewelMobileException(e.getMessage(), e);
			}
			midNameSpace = UUID.fromString(pstrNameSpace);

			marrSortOrder = new int[0];
			mlngPageSize = 10;
		}
		
		public UUID GetID()
		{
			return mid;
		}

		public UUID GetNameSpace()
		{
			return midNameSpace;
		}

		public UUID GetEditorID()
		{
			return mrefQuery.getEditorViewID();
		}
		
		public ITypeDef GetParamType()
			throws JewelMobileException
		{
			try
			{
				return TypeDef.GetInstance(mrefQuery.getParamType());
			}
			catch (Throwable e)
			{
				throw new JewelMobileException(e.getMessage(), e);
			}
		}

		public QueryColumnObj[] GetColumns()
		{
			IQueryField[] larrFields;
			int llngCount;
			QueryColumnObj[] larrRes;
			int i;

			larrFields = mrefQuery.getFields();
			llngCount = larrFields.length;

			larrRes = new QueryColumnObj[llngCount];
			for ( i = 0; i < llngCount; i++ )
			{
				larrRes[i] = new QueryColumnObj();
				larrRes[i].mstrName = larrFields[i].getHeader();
				larrRes[i].mlngWidth = larrFields[i].getWidth();
			}

			return larrRes;
		}

		public boolean CanCreate()
		{
			return mrefQuery.getCanCreate();
		}

        public void LoadQuery(boolean pbForceParam, java.lang.Object pobjParamValue, Hashtable<String, java.lang.Object> parrExtParams,
        		int[] parrMembers, java.lang.Object[] parrValues)
        	throws JewelMobileException
		{
			MasterDB ldb;
			ResultSet lrsRows;
			int i, llngRow;
            ResultSetMetaData lrsMetaData;
            java.lang.Object[] larrRow;
            String lstrAux;
            UUID lidKey;

            mobjParam = pobjParamValue;
            marrExternParams = parrExtParams;
            marrMembers = parrMembers;
            marrValues = parrValues;

			try {
				ldb = new MasterDB();

	            if ( ((mobjParam == null) && (!pbForceParam)) || (mrefQuery.getParamType() == null) )
				{
					if ( marrMembers == null )
						lrsRows = mrefQuery.SelectAll(ldb, midNameSpace, marrExternParams);
					else
	                    lrsRows = mrefQuery.SelectByMembers(ldb, midNameSpace, marrMembers, marrValues, marrExternParams);
				}
				else
	                lrsRows = mrefQuery.SelectByParam(ldb, midNameSpace, mobjParam, marrExternParams);

	            lrsMetaData = lrsRows.getMetaData();
	            mlngColCount = lrsMetaData.getColumnCount();

	            marrData = new ArrayList<Row>();
				llngRow = 0;
				while ( lrsRows.next() )
				{
					lidKey = UUID.fromString(lrsRows.getString(1));
	                larrRow = new java.lang.Object[mlngColCount - 1]; 
	                for (i = 1; i < mlngColCount; i++)
	                	if ( lrsMetaData.getColumnTypeName(i + 1).equals("uniqueidentifier") )
	                	{
	                		lstrAux = lrsRows.getString(i + 1);
	                		if ( lstrAux == null )
	                    		larrRow[i - 1] = null;
	                		else
	                			larrRow[i - 1] = UUID.fromString(lstrAux);
	                	}
	                	else
	                		larrRow[i - 1] = lrsRows.getObject(i + 1);
					marrData.add(new Row(this, llngRow, lidKey, larrRow));
					llngRow++;
				}
				lrsRows.close();

				ldb.Disconnect();
			}
			catch (Throwable e)
			{
				throw new JewelMobileException(e.getMessage(), e);
			}

            mlngCurrentPage = 0;
		}
        
        public void ReloadAt(boolean pbForceParam, java.lang.Object pobjParamValue)
        	throws JewelMobileException
		{
        	LoadQuery(pbForceParam, pobjParamValue, marrExternParams, marrMembers, marrValues);
        	Collections.sort(marrData);
		}

        public void ApplySearch(int[] parrMembers, java.lang.Object[] parrValues)
    		throws JewelMobileException
		{
        	LoadQuery(false, null, marrExternParams, parrMembers, parrValues);
		}

        public void GetCurrentPage(GridResponse prefResp)
        {
        	int llngSize, llngRow;
        	int i, j;

        	if ( mlngPageSize > 0 )
        	{
	        	if ( mlngPageSize * (mlngCurrentPage + 1) > marrData.size() )
	        		llngSize = marrData.size() - mlngPageSize * mlngCurrentPage;
	        	else
	        		llngSize = mlngPageSize;
        	}
        	else
        		llngSize = marrData.size();

        	prefResp.marrRows = new int[llngSize];
        	prefResp.marrData = new String[llngSize][];

        	for ( i = 0; i < llngSize; i++ )
        	{
        		llngRow = ( mlngPageSize > 0 ? mlngPageSize * mlngCurrentPage + i : i );
        		prefResp.marrRows[i] = marrData.get(llngRow).RowNum();
        		prefResp.marrData[i] = new String[marrData.get(llngRow).length()];
        		for ( j = 0; j < marrData.get(llngRow).length(); j++ )
        			prefResp.marrData[i][j] = marrData.get(llngRow).get(j);
        	}

        	prefResp.mlngCurrPage = mlngCurrentPage;
        	prefResp.mlngPageSize = mlngPageSize;
        	prefResp.mlngRecCount = marrData.size();
        	prefResp.mlngPageCount = (int)Math.ceil((double)marrData.size() / (double)mlngPageSize);
        }

        public void SetPageSize(int plngSize)
        {
        	mlngCurrentPage = ( plngSize > 0 ? (int)((double)mlngCurrentPage / (double)plngSize * mlngPageSize) : 0);
        	mlngPageSize = plngSize;
        }

        public boolean PageForward()
        {
        	if ( (mlngPageSize < 0) ||
        			((mlngCurrentPage + 1) >= (int)Math.ceil((double)marrData.size() / (double)mlngPageSize)) )
        		return false;
        	mlngCurrentPage++;
        	return true;
        }

        public boolean PageBack()
        {
        	if ( mlngCurrentPage <= 0 )
        		return false;
        	mlngCurrentPage--;
        	return true;
        }

        public boolean PageFirst()
        {
        	if ( mlngCurrentPage <= 0 )
        		return false;
        	mlngCurrentPage = 0;
        	return true;
        }

        public boolean PageLast()
        {
        	if ( (mlngPageSize < 0) ||
        			((mlngCurrentPage + 1) >= (int)Math.ceil((double)marrData.size() / (double)mlngPageSize)) )
        		return false;
        	mlngCurrentPage = (int)Math.ceil((double)marrData.size() / (double)mlngPageSize) - 1;
        	return true;
        }

        public void ForceRefresh(boolean pbForceParam)
        	throws JewelMobileException
        {
            LoadQuery(pbForceParam, mobjParam, marrExternParams, marrMembers, marrValues);
        	Collections.sort(marrData);
        }

        public void SetSortOrder(int[] parrSortOrder)
        {
        	marrSortOrder = parrSortOrder;
        	Collections.sort(marrData);
        }

        public int[] GetSortOrder()
        {
        	return marrSortOrder;
        }

        public Row GetRow(int plngRow)
        {
        	int i;

        	for ( i = 0; i < marrData.size(); i++ )
        		if ( marrData.get(i).RowNum() == plngRow )
        			return marrData.get(i);

        	return null;
        }
        
        public int FindRow(UUID pidKey)
        {
        	int i;

        	if ( pidKey == null )
        		return -1;

        	for ( i = 0; i < marrData.size(); i++ )
        		if ( pidKey.equals(marrData.get(i).getKey()) )
        		{
        			if ( mlngPageSize > 0 )
        				mlngCurrentPage = (int)Math.ceil(((double)i + 0.1) / (double)mlngPageSize) - 1;
        			return marrData.get(i).RowNum();
        		}

        	return -1;
        }
        
        public boolean NewRow()
        {
            java.lang.Object[] larrRow;
            int llngRow;
            int i;

        	if ( !mrefQuery.getCanCreate() )
        		return false;

            larrRow = new java.lang.Object[mlngColCount - 1];
            for (i = 1; i < mlngColCount; i++)
        		larrRow[i - 1] = null;
            llngRow = marrData.size();
			marrData.add(new Row(this, llngRow, null, larrRow));

			if ( mlngPageSize > 0 )
				mlngCurrentPage = (int)Math.ceil(((double)marrData.size() - 0.1) / (double)mlngPageSize) - 1;

        	return true;
        }
        
        public boolean DeleteRow(int plngRow)
    		throws JewelMobileException
        {
        	int i;
			Row lrefRow;
			IEntity lrefEntity;
			MasterDB ldb;
        	
			lrefRow = null;
        	for ( i = 0; i < marrData.size(); i++ )
        		if ( marrData.get(i).RowNum() == plngRow )
        		{
        			lrefRow = marrData.get(i);
        			break;
        		}

            if ( lrefRow == null )
            	return false;

            if ( lrefRow.getKey() != null )
            {
	            try
	            {
	            	ldb = new MasterDB();
					lrefEntity = Entity.GetInstance(Engine.FindEntity(midNameSpace, mrefQuery.getDriverObject().getKey()));
					lrefEntity.Delete(ldb, lrefRow.getKey());
					ldb.Disconnect();
				}
	            catch (Throwable e)
	            {
	            	throw new JewelMobileException(e.getMessage(), e);
				}
            }

            marrData.remove(i);
        	if ( (mlngPageSize > 0) && (mlngCurrentPage > 0) &&
        			(mlngCurrentPage >= (int)Math.ceil((double)marrData.size() / (double)mlngPageSize)) )
        		mlngCurrentPage--;

            return true;
        }
        
        public String[] RefreshRow(int plngRow, ObjectBase pobjData)
        	throws JewelMobileException
        {
			Row lrefRow;
			MasterDB ldb;
			ResultSet lrsRows;
            ResultSetMetaData lrsMetaData;
			int i, llngCount;
            java.lang.Object[] larrRow;
            String lstrAux;
			String[] larrRes;
        	
            lrefRow = GetRow(plngRow);

            if ( lrefRow == null )
            	return null;

            try
            {
				ldb = new MasterDB();
				lrsRows = mrefQuery.SelectByKey(ldb, midNameSpace, pobjData.getKey());
	
				if ( !lrsRows.next() )
				{
					lrsRows.close();
					ldb.Disconnect();
					return null;
				}
	
	            lrsMetaData = lrsRows.getMetaData();
	            llngCount = lrsMetaData.getColumnCount();
	
	            larrRow = new java.lang.Object[llngCount - 1];
	            for (i = 1; i < llngCount; i++)
	            	if ( lrsMetaData.getColumnTypeName(i + 1).equals("uniqueidentifier") )
	            	{
	            		lstrAux = lrsRows.getString(i + 1);
	            		if ( lstrAux == null )
	                		larrRow[i - 1] = null;
	            		else
	            			larrRow[i - 1] = UUID.fromString(lstrAux);
	            	}
	            	else
	            		larrRow[i - 1] = lrsRows.getObject(i + 1);
	
	            lrsRows.close();
				ldb.Disconnect();
            }
            catch( Throwable e )
            {
            	throw new JewelMobileException(e.getMessage(), e);
            }

            lrefRow.setRefresh(pobjData, larrRow);
            larrRes = new String[llngCount - 1];
        	for ( i = 0; i < llngCount - 1; i++ )
        		larrRes[i] = lrefRow.get(i);

        	return larrRes;
        }

		public ObjectBase DataObject(int plngRow)
			throws JewelMobileException
		{
			Row lrefRow;
			ObjectBase lobjAux;
			UUID lidEntity, lidKey;
            IObject lrefObject;
            IQueryParam[] larrParams;
            int i;

            lrefRow = GetRow(plngRow);

            if ( lrefRow == null )
            	return null;

            lobjAux = lrefRow.getCache(); 
            if ( lobjAux == null )
            {
                try
                {
	                lidEntity = Engine.FindEntity(midNameSpace, mrefQuery.getDriverObject().getKey());
	                lrefObject = Entity.GetInstance(lidEntity).getDefObject();
	                lidKey = lrefRow.getKey();

					lobjAux = Engine.GetWorkInstance(lidEntity, lidKey);
				}
                catch (Throwable e)
                {
					throw new JewelMobileException(e.getMessage(), e);
				}

                if (lidKey == null)
                {
                	try
                	{
	                    if (mobjParam != null)
	                        lobjAux.setAt(lrefObject.MemberByNOrd(mrefQuery.getParamAppliesTo()), mobjParam);

	                    if (QueryTypeGUIDs.QT_AND.equals(mrefQuery.getQueryType()))
	                    {
	                        larrParams = mrefQuery.getParams();
	                        for (i = 0; i < larrParams.length; i++)
	                            lobjAux.setAt(lrefObject.MemberByNOrd(larrParams[i].getParamAppliesTo()),
	                            		larrParams[i].getParamValue());
	                    }
                	}
                    catch (Throwable e)
                    {
    					throw new JewelMobileException(e.getMessage(), e);
    				}
                }

                lrefRow.setCache(lobjAux);
            }

            return lobjAux;
		}
	}

	@SuppressWarnings("unchecked")
	public static Hashtable<UUID, QueryWSpace> GetQueryWSStorage()
	{
		Hashtable<UUID, QueryWSpace> larrAux;

        if (getSession() == null)
            return null;

        larrAux = (Hashtable<UUID, QueryWSpace>)getSession().getAttribute("MADDS_Query_Storage");
        if (larrAux == null)
        {
        	larrAux = new Hashtable<UUID, QueryWSpace>();
            getSession().setAttribute("MADDS_Query_Storage", larrAux);
        }

        return larrAux;
	}

	public GridResponse OpenQuery(String pstrQueryID, String pstrNameSpace, boolean pbForceParam, String pstrParam,
			String pstrFormID, ParamInfo[] parrExtParams, String pstrInitValue)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;
		UUID lidAux;
		java.lang.Object lobjParam;
		Hashtable<String, java.lang.Object> larrExtParams;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = new QueryWSpace(pstrQueryID, pstrNameSpace);
		lidAux = lrefWSpace.GetID();
		GetQueryWSStorage().put(lidAux, lrefWSpace);

		if ( parrExtParams == null )
			larrExtParams = null;
		else
			larrExtParams = FormDataBridge.ParseExtParams(UUID.fromString(pstrFormID), parrExtParams);

		if ( pstrParam == null )
			lobjParam = null;
		else
		{
			try
			{
				lobjParam = TypeDataBridge.ParseValue(lrefWSpace.GetParamType(), pstrParam);
			}
			catch (JewelMobileException e)
			{
				throw e;
			}
			catch (Throwable e)
			{
				throw new JewelMobileException(e.getMessage(), e);
			}
		}
		lrefWSpace.LoadQuery(pbForceParam, lobjParam, larrExtParams, null, null);

		lobjAux = new GridResponse();
		lobjAux.mstrWorkspaceID = lidAux.toString();
		lobjAux.marrColumns = lrefWSpace.GetColumns();
		lobjAux.mstrEditorID = lrefWSpace.GetEditorID().toString();
		lobjAux.mbCanCreate = lrefWSpace.CanCreate();
		if ( pstrInitValue != null )
			lobjAux.mlngCurrRow = lrefWSpace.FindRow(UUID.fromString(pstrInitValue));
		else
			lobjAux.mlngCurrRow = -1;
		lrefWSpace.GetCurrentPage(lobjAux);

		return lobjAux;
	}

	public GridResponse SetPageSize(String pstrWorkspace, int plngPageSize)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		lrefWSpace.SetPageSize(plngPageSize);

		lobjAux = new GridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);
		return lobjAux;
	}

	public GridResponse PageForward(String pstrWorkspace)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.PageForward() )
			throw new JewelMobileException("Page forward at last page.");

		lobjAux = new GridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);
		return lobjAux;
	}

	public GridResponse PageBack(String pstrWorkspace)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.PageBack() )
			throw new JewelMobileException("Page back at first page.");

		lobjAux = new GridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);
		return lobjAux;
	}

	public GridResponse PageFirst(String pstrWorkspace)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.PageFirst() )
			throw new JewelMobileException("Already at first page.");

		lobjAux = new GridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);
		return lobjAux;
	}

	public GridResponse PageLast(String pstrWorkspace)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.PageLast() )
			throw new JewelMobileException("Already at last page.");

		lobjAux = new GridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);
		return lobjAux;
	}

	public GridResponse ForceRefresh(String pstrWorkspace, boolean pbForceParam)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		lrefWSpace.ForceRefresh(pbForceParam);

		lobjAux = new GridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);
		return lobjAux;
	}

	public GridResponse ReloadAt(String pstrWorkspace, boolean pbForceParam, String pstrParam)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;
		java.lang.Object lobjParam;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		if ( pstrParam == null )
			lobjParam = null;
		else
		{
			try
			{
				lobjParam = TypeDataBridge.ParseValue(lrefWSpace.GetParamType(), pstrParam);
			}
			catch (JewelMobileException e)
			{
				throw e;
			}
			catch (Throwable e)
			{
				throw new JewelMobileException(e.getMessage(), e);
			}
		}
		lrefWSpace.ReloadAt(pbForceParam, lobjParam);

		lobjAux = new GridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);

		return lobjAux;
	}

	public GridResponse ApplySearch(String pstrWorkspace, String pstrFormID, String[] parrData)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;
		UUID lidForm;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		lidForm = UUID.fromString(pstrFormID);
		lobjAux = new GridResponse();
		lrefWSpace.ApplySearch(FormDataBridge.GetSearchIndexes(lidForm, parrData), FormDataBridge.GetSearchData(lidForm, parrData));
		lrefWSpace.GetCurrentPage(lobjAux);

		return lobjAux;
	}

	public GridResponse ApplySort(String pstrWorkspace, int[] parrOrder)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		lobjAux = new GridResponse();
		lrefWSpace.SetSortOrder(parrOrder);
		lrefWSpace.GetCurrentPage(lobjAux);

		return lobjAux;
	}
	
	public DataObject GetRow(String pstrWorkspace, int plngRow)
		throws JewelMobileException
	{
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		return ViewDataBridge.ServerToClient(lrefWSpace.GetEditorID(), lrefWSpace.DataObject(plngRow),
				lrefWSpace.GetNameSpace());
	}

	public GridResponse NewRow(String pstrWorkspace)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.NewRow() )
			throw new JewelMobileException("Access denied: Cannot create new rows.");

		lobjAux = new GridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);

		return lobjAux;
	}

	public GridActionResponse DoAction(String pstrWorkspace, int plngRow, int plngOrder, int plngAction, DataObject pobjData)
		throws JewelMobileException
	{
		QueryWSpace lrefWSpace;
		ObjectBase lobjData, lobjLocal;
		UUID lidView;
		java.lang.Object[] larrNonObject;
		IFormAction lrefAction;
		GridActionResponse laux;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		lobjData = lrefWSpace.DataObject(plngRow);
		lobjLocal = new ObjectMaster();
		try
		{
			lobjLocal.LoadAt(lobjData);
		}
		catch (Throwable e)
		{
			throw new JewelMobileException(e.getMessage(), e);
		}

		lidView = lrefWSpace.GetEditorID();
		ViewDataBridge.ClientToServer(lidView, lobjLocal, pobjData);
		larrNonObject = ViewDataBridge.GetNonObjectParams(lidView, plngOrder, lobjLocal, pobjData);

		try
		{
			lrefAction = Form.GetInstance(View.GetInstance(lidView).getTabs()[plngOrder].getFormID()).getActions()[plngAction];
			lrefAction.Run(lobjData, larrNonObject);
			lobjLocal.setDataRange(larrNonObject);
		}
		catch (InvocationTargetException e)
		{
        	Throwable x;

        	x = e;
        	while( x.getCause() != null )
        		x = x.getCause();

        	laux = new GridActionResponse();
        	laux.mstrResult = x.getMessage();
        	if ( laux.mstrResult == null )
        		laux.mstrResult = x.getClass().getName();
        	laux.mobjData = null;
        	return laux;
		}
		catch (Throwable e)
		{
        	throw new JewelMobileException(e.getMessage(), e);
		}

    	laux = new GridActionResponse();
    	laux.mstrResult = "";
    	laux.mobjData = ViewDataBridge.ServerToClient(lidView, lobjLocal, lrefWSpace.GetNameSpace());
    	ViewDataBridge.SetNonObjectParams(lidView, plngOrder, lobjLocal, laux.mobjData, larrNonObject, lobjData.getNameSpace());
    	return laux;
	}

	public GridSaveResponse SaveRow(String pstrWorkspace, int plngRow, DataObject pobjData)
		throws JewelMobileException
	{
		GridSaveResponse lobjRes;
		QueryWSpace lrefWSpace;
		ObjectBase lobjData, lobjLocal;
		MasterDB ldb;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		lobjData = lrefWSpace.DataObject(plngRow);
		lobjLocal = new ObjectMaster();
		try
		{
			lobjLocal.LoadAt(lobjData);
		}
		catch (Throwable e)
		{
			throw new JewelMobileException(e.getMessage(), e);
		}
		ViewDataBridge.ClientToServer(lrefWSpace.GetEditorID(), lobjLocal, pobjData);

		try
		{
			ldb = new MasterDB();
			lobjData.Validate(lobjLocal.getData());
			lobjData.SaveToDb(ldb);
		}
		catch (Throwable e)
		{
			throw new JewelMobileException(e.getMessage(), e);
		}

		lobjRes = new GridSaveResponse();
		lobjRes.mlngRow = plngRow;
		lobjRes.marrRow = lrefWSpace.RefreshRow(plngRow, lobjData);
		lobjRes.mobjData = ViewDataBridge.ServerToClient(lrefWSpace.GetEditorID(), lrefWSpace.DataObject(plngRow),
				lrefWSpace.GetNameSpace());
		return lobjRes;
	}

	public GridResponse DeleteRow(String pstrWorkspace, int plngRow)
		throws JewelMobileException
	{
		GridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelMobileException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.DeleteRow(plngRow) )
			throw new JewelMobileException("Unexpected: attempt to delete non-existant row.");

		lobjAux = new GridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);

		return lobjAux;
	}

	public String CloseQuery(String pstrWorkspace)
		throws JewelMobileException
	{
		if ( Engine.getCurrentUser() == null )
			return null;

		GetQueryWSStorage().remove(UUID.fromString(pstrWorkspace));

		return "";
	}
}
