package Jewel.Web.server;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

import Jewel.Engine.*;
import Jewel.Engine.Constants.*;
import Jewel.Engine.DataAccess.*;
import Jewel.Engine.Implementation.*;
import Jewel.Engine.Interfaces.*;
import Jewel.Engine.SysObjects.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

public class DynaGridServiceImpl
	extends EngineImplementor
	implements DynaGridService
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

        private static final int PAGE_SIZE = 10;

		private UUID mid;
        private IQueryDef mrefQuery;
        private UUID midNameSpace;
		private java.lang.Object mobjParam;
		private int[] marrMembers;
		private java.lang.Object[] marrValues;
        private ArrayList<Row> marrData;
		private HashMap<String, java.lang.Object> marrExternParams;
        private int mlngCurrentPage;
        private int[] marrSortOrder;
        private int mlngColCount;

		public QueryWSpace(String pstrQueryID, String pstrNameSpace)
			throws JewelWebException
		{
			mid = UUID.randomUUID();
			try
			{
				mrefQuery = QueryDef.GetInstance(UUID.fromString(pstrQueryID));
			}
			catch (Throwable e)
			{
				throw new JewelWebException(e.getMessage(), e);
			}
			midNameSpace = UUID.fromString(pstrNameSpace);

			marrSortOrder = new int[0];
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
			throws JewelWebException
		{
			try
			{
				return TypeDef.GetInstance(mrefQuery.getParamType());
			}
			catch (Throwable e)
			{
				throw new JewelWebException(e.getMessage(), e);
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

        public void LoadQuery(boolean pbForceParam, java.lang.Object pobjParamValue, HashMap<String, java.lang.Object> parrExtParams,
        		int[] parrMembers, java.lang.Object[] parrValues)
        	throws JewelWebException
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
				throw new JewelWebException(e.getMessage(), e);
			}

            mlngCurrentPage = 0;
		}
        
        public void ReloadAt(boolean pbForceParam, java.lang.Object pobjParamValue)
        	throws JewelWebException
		{
        	LoadQuery(pbForceParam, pobjParamValue, marrExternParams, marrMembers, marrValues);
        	Collections.sort(marrData);
		}

        public void ApplySearch(int[] parrMembers, java.lang.Object[] parrValues)
    		throws JewelWebException
		{
        	LoadQuery(false, null, marrExternParams, parrMembers, parrValues);
		}

        public void GetCurrentPage(DynaGridResponse prefResp)
        {
        	int llngSize, llngRow;
        	int i, j;

        	if ( PAGE_SIZE * (mlngCurrentPage + 1) > marrData.size() )
        		llngSize = marrData.size() - PAGE_SIZE * mlngCurrentPage;
        	else
        		llngSize = PAGE_SIZE;

        	prefResp.marrRows = new int[llngSize];
        	prefResp.marrData = new String[llngSize][];

        	for ( i = 0; i < llngSize; i++ )
        	{
        		llngRow = PAGE_SIZE * mlngCurrentPage + i;
        		prefResp.marrRows[i] = marrData.get(llngRow).RowNum();
        		prefResp.marrData[i] = new String[marrData.get(llngRow).length()];
        		for ( j = 0; j < marrData.get(llngRow).length(); j++ )
        			prefResp.marrData[i][j] = marrData.get(llngRow).get(j);
        	}

        	prefResp.mlngCurrPage = mlngCurrentPage;
        	prefResp.mlngPageSize = PAGE_SIZE;
        	prefResp.mlngRecCount = marrData.size();
        	prefResp.mlngPageCount = (int)Math.ceil((double)marrData.size() / (double)PAGE_SIZE);
        }

        public boolean PageForward()
        {
        	if ( (mlngCurrentPage + 1) >= (int)Math.ceil((double)marrData.size() / (double)PAGE_SIZE) )
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
        	if ( (mlngCurrentPage + 1) >= (int)Math.ceil((double)marrData.size() / (double)PAGE_SIZE) )
        		return false;
        	mlngCurrentPage = (int)Math.ceil((double)marrData.size() / (double)PAGE_SIZE) - 1;
        	return true;
        }

        public void ForceRefresh(boolean pbForceParam)
        	throws JewelWebException
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
                	mlngCurrentPage = (int)Math.ceil(((double)i + 0.1) / (double)PAGE_SIZE) - 1;
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

        	mlngCurrentPage = (int)Math.ceil(((double)marrData.size() - 0.1) / (double)PAGE_SIZE) - 1;

        	return true;
        }
        
        public boolean DeleteRow(int plngRow)
    		throws JewelWebException
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
	            	throw new JewelWebException(e.getMessage(), e);
				}
            }

            marrData.remove(i);
        	if ( (mlngCurrentPage > 0) && (mlngCurrentPage >= (int)Math.ceil((double)marrData.size() / (double)PAGE_SIZE)) )
        		mlngCurrentPage--;

            return true;
        }
        
        public String[] RefreshRow(int plngRow, ObjectBase pobjData)
        	throws JewelWebException
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
            	throw new JewelWebException(e.getMessage(), e);
            }

            lrefRow.setRefresh(pobjData, larrRow);
            larrRes = new String[llngCount - 1];
        	for ( i = 0; i < llngCount - 1; i++ )
        		larrRes[i] = lrefRow.get(i);

        	return larrRes;
        }

		public ObjectBase DataObject(int plngRow)
			throws JewelWebException
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
					throw new JewelWebException(e.getMessage(), e);
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
    					throw new JewelWebException(e.getMessage(), e);
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

	public DynaGridResponse OpenQuery(String pstrQueryID, String pstrNameSpace, boolean pbForceParam, String pstrParam,
			String pstrFormID, ParamInfo[] parrExtParams, String pstrInitValue)
		throws JewelWebException
	{
		DynaGridResponse lobjAux;
		QueryWSpace lrefWSpace;
		UUID lidAux;
		java.lang.Object lobjParam;
		HashMap<String, java.lang.Object> larrExtParams;

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
			catch (JewelWebException e)
			{
				throw e;
			}
			catch (Throwable e)
			{
				throw new JewelWebException(e.getMessage(), e);
			}
		}
		lrefWSpace.LoadQuery(pbForceParam, lobjParam, larrExtParams, null, null);

		lobjAux = new DynaGridResponse();
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

	public DynaGridResponse PageForward(String pstrWorkspace)
		throws JewelWebException
	{
		DynaGridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.PageForward() )
			throw new JewelWebException("Page forward at last page.");

		lobjAux = new DynaGridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);
		return lobjAux;
	}

	public DynaGridResponse PageBack(String pstrWorkspace)
		throws JewelWebException
	{
		DynaGridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.PageBack() )
			throw new JewelWebException("Page back at first page.");

		lobjAux = new DynaGridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);
		return lobjAux;
	}

	public DynaGridResponse PageFirst(String pstrWorkspace)
		throws JewelWebException
	{
		DynaGridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.PageFirst() )
			throw new JewelWebException("Already at first page.");

		lobjAux = new DynaGridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);
		return lobjAux;
	}

	public DynaGridResponse PageLast(String pstrWorkspace)
		throws JewelWebException
	{
		DynaGridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.PageLast() )
			throw new JewelWebException("Already at last page.");

		lobjAux = new DynaGridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);
		return lobjAux;
	}

	public DynaGridResponse ForceRefresh(String pstrWorkspace, boolean pbForceParam)
		throws JewelWebException
	{
		DynaGridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		lrefWSpace.ForceRefresh(pbForceParam);

		lobjAux = new DynaGridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);
		return lobjAux;
	}

	public DynaGridResponse ReloadAt(String pstrWorkspace, boolean pbForceParam, String pstrParam)
		throws JewelWebException
	{
		DynaGridResponse lobjAux;
		QueryWSpace lrefWSpace;
		java.lang.Object lobjParam;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		if ( pstrParam == null )
			lobjParam = null;
		else
		{
			try
			{
				lobjParam = TypeDataBridge.ParseValue(lrefWSpace.GetParamType(), pstrParam);
			}
			catch (JewelWebException e)
			{
				throw e;
			}
			catch (Throwable e)
			{
				throw new JewelWebException(e.getMessage(), e);
			}
		}
		lrefWSpace.ReloadAt(pbForceParam, lobjParam);

		lobjAux = new DynaGridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);

		return lobjAux;
	}

	public DynaGridResponse ApplySearch(String pstrWorkspace, String pstrFormID, String[] parrData)
		throws JewelWebException
	{
		DynaGridResponse lobjAux;
		QueryWSpace lrefWSpace;
		UUID lidForm;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		lidForm = UUID.fromString(pstrFormID);
		lobjAux = new DynaGridResponse();
		lrefWSpace.ApplySearch(FormDataBridge.GetSearchIndexes(lidForm, parrData), FormDataBridge.GetSearchData(lidForm, parrData));
		lrefWSpace.GetCurrentPage(lobjAux);

		return lobjAux;
	}

	public DynaGridResponse ApplySort(String pstrWorkspace, int[] parrOrder)
		throws JewelWebException
	{
		DynaGridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		lobjAux = new DynaGridResponse();
		lrefWSpace.SetSortOrder(parrOrder);
		lrefWSpace.GetCurrentPage(lobjAux);

		return lobjAux;
	}
	
	public DataObject GetRow(String pstrWorkspace, int plngRow)
		throws JewelWebException
	{
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		return ViewDataBridge.ServerToClient(lrefWSpace.GetEditorID(), lrefWSpace.DataObject(plngRow),
				lrefWSpace.GetNameSpace());
	}

	public DynaGridResponse NewRow(String pstrWorkspace)
		throws JewelWebException
	{
		DynaGridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.NewRow() )
			throw new JewelWebException("Access denied: Cannot create new rows.");

		lobjAux = new DynaGridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);

		return lobjAux;
	}

	public GridActionResponse DoAction(String pstrWorkspace, int plngRow, int plngOrder, int plngAction, DataObject pobjData)
		throws JewelWebException
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
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		lobjData = lrefWSpace.DataObject(plngRow);
		lobjLocal = new ObjectMaster();
		try
		{
			lobjLocal.LoadAt(lobjData);
		}
		catch (Throwable e)
		{
			throw new JewelWebException(e.getMessage(), e);
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
        	throw new JewelWebException(e.getMessage(), e);
		}

    	laux = new GridActionResponse();
    	laux.mstrResult = "";
    	laux.mobjData = ViewDataBridge.ServerToClient(lidView, lobjLocal, lrefWSpace.GetNameSpace());
    	ViewDataBridge.SetNonObjectParams(lidView, plngOrder, lobjLocal, laux.mobjData, larrNonObject, lobjData.getNameSpace());
    	return laux;
	}

	public DynaGridSaveResponse SaveRow(String pstrWorkspace, int plngRow, DataObject pobjData)
		throws JewelWebException
	{
		DynaGridSaveResponse lobjRes;
		QueryWSpace lrefWSpace;
		ObjectBase lobjData, lobjLocal;
		MasterDB ldb;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		lobjData = lrefWSpace.DataObject(plngRow);
		lobjLocal = new ObjectMaster();
		try
		{
			lobjLocal.LoadAt(lobjData);
		}
		catch (Throwable e)
		{
			throw new JewelWebException(e.getMessage(), e);
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
			throw new JewelWebException(e.getMessage(), e);
		}

		lobjRes = new DynaGridSaveResponse();
		lobjRes.mlngRow = plngRow;
		lobjRes.marrRow = lrefWSpace.RefreshRow(plngRow, lobjData);
		lobjRes.mobjData = ViewDataBridge.ServerToClient(lrefWSpace.GetEditorID(), lrefWSpace.DataObject(plngRow),
				lrefWSpace.GetNameSpace());
		return lobjRes;
	}

	public DynaGridResponse DeleteRow(String pstrWorkspace, int plngRow)
		throws JewelWebException
	{
		DynaGridResponse lobjAux;
		QueryWSpace lrefWSpace;

		if ( Engine.getCurrentUser() == null )
			return null;

		lrefWSpace = GetQueryWSStorage().get(UUID.fromString(pstrWorkspace));
		if ( lrefWSpace == null )
			throw new JewelWebException("Unexpected: non-existant query workspace.");

		if ( !lrefWSpace.DeleteRow(plngRow) )
			throw new JewelWebException("Unexpected: attempt to delete non-existant row.");

		lobjAux = new DynaGridResponse();
		lrefWSpace.GetCurrentPage(lobjAux);

		return lobjAux;
	}

	public String CloseQuery(String pstrWorkspace)
		throws JewelWebException
	{
		if ( Engine.getCurrentUser() == null )
			return null;

		GetQueryWSStorage().remove(UUID.fromString(pstrWorkspace));

		return "";
	}
}
