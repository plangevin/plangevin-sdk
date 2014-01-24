package com.philippelangevin.sdk.database.dbAccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.philippelangevin.sdk.database.dbAccess.DatabaseAccessObjectIF.QueryBuilder.QueryConditionStruct;
import com.philippelangevin.sdk.database.dbAccess.DatabaseAccessObjectIF.QueryBuilder.QueryMathOperatorStruct;
import com.philippelangevin.sdk.database.dbAccess.DatabaseAccessObjectIF.QueryBuilder.QueryMathOperatorStructIF;
import com.philippelangevin.sdk.database.dbAccess.DatabaseAccessObjectIF.QueryBuilder.TblColStruct;
import com.philippelangevin.sdk.database.exception.TONotFoundException;
import com.philippelangevin.sdk.database.tables.TableInfo;
import com.philippelangevin.sdk.database.transferableObject.ColumnInfo;
import com.philippelangevin.sdk.database.transferableObject.TOColumnFilter;
import com.philippelangevin.sdk.database.transferableObject.TOUtil;
import com.philippelangevin.sdk.database.transferableObject.TransferableObject;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectInfo;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectStructureIF;
import com.philippelangevin.sdk.database.transferableObject.metaData.TOColumnMetaDataIF;
import com.philippelangevin.sdk.database.util.ClassicSQLRequests;
import com.philippelangevin.sdk.database.util.ResultSetTranslator;

/**
   * <p> Title: {@link AbstractDatabaseDAO} <p>
   * <p> Description: This class is for the connection with the DataBase
   * where the method to open-close and refresh the connection.</p>
   * <p> Company : C-Tec <p>
   *
   * @author sroger
   * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
   */

public abstract class AbstractDatabaseDAO implements DatabaseAccessObjectIF{

	protected DatabaseConnection dbAccess=null;
	protected ConnectionModeEnum connectionMode = null;

	/**
	 * Test if the containing DatabaseConnection is connected
	 * @return True if connected
	 */
	public boolean isConnectionAlive()	{
		if (dbAccess == null)	{
			return false ;
		}
		
		return dbAccess.isConnectionAlive(false) ;
	}
	
	public AbstractDatabaseDAO(ConnectionModeEnum connectionMode){
		this.connectionMode = connectionMode;
	}
	
	@Override
	public void beginTransaction() throws SQLException{
		switch (connectionMode) {
		case READ_WRITE_MANUAL_COMMIT:
			dbAccess.setReadOnly(false);
			dbAccess.setAutoCommit(false);
			break;
			
		case READ_ONLY:
		case READ_WRITE_AUTO_COMMIT:
		default:
			throw new SQLException("Can't call beginTransaction() in the database mode " + connectionMode + ".");
		}
	}
	
	@Override
	public boolean rollback(){
		try {
			switch (connectionMode) {
			case READ_WRITE_MANUAL_COMMIT:
				dbAccess.dbConnection.rollback();
				dbAccess.setAutoCommit(true);
				dbAccess.setReadOnly(true);
				return true;
				
			case READ_ONLY:
			case READ_WRITE_AUTO_COMMIT:
			default:
				System.err.println("AbstractDatabaseDAO.rollback() - Can't call rollback() in the database mode " + connectionMode + ".");
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void close() {
		dbAccess.closeConnection();
		System.out.println("Closing " + getClass().getSimpleName() + "...");
	}
	
	@Override
	public void commit() throws SQLException {
		switch (connectionMode) {
		case READ_WRITE_MANUAL_COMMIT:
			dbAccess.commit();
			dbAccess.setAutoCommit(true);
			dbAccess.setReadOnly(true);
			break;
			
		case READ_ONLY:
		case READ_WRITE_AUTO_COMMIT:
		default:
			throw new SQLException("Can't call commit() in the database mode " + connectionMode + ".");
		}
	}
	
	@Override
	public void deferConstraints(){
		dbAccess.deferConstraints();
	}
	
	@Override
	public void undeferConstraints(){
		dbAccess.undeferConstraints();
	}
	
	@Override
	public void reconnect() {
		dbAccess.closeConnection();
		
		try {
			openConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean isAutoCommit() {
		return connectionMode == ConnectionModeEnum.READ_ONLY;
	}
	
	protected void openConnection() throws SQLException {
		dbAccess.openConnection();
		
		switch (connectionMode) {
		case READ_ONLY:
			dbAccess.setReadOnly(true);
			break;
			
		case READ_WRITE_AUTO_COMMIT:
			dbAccess.setAutoCommit(true);
			break;
			
		case READ_WRITE_MANUAL_COMMIT:
			dbAccess.setAutoCommit(true);
			dbAccess.setReadOnly(true);
			break;
			
		default:
			System.err.println("AbstractDatabaseDAO.openConnection() - Unknown connection mode: " + connectionMode);
			Thread.dumpStack();
		}
	}
	
	@Override
	public void insert(TransferableObject to) throws SQLException {
		if (to == null){
			throw new SQLException("insert() should never be called with a null value!");
		}
		
		// We retrieve all columns and column values we will be requiring
		@SuppressWarnings("unchecked")
		ColumnInfo<Object>[] toColumns = (ColumnInfo<Object>[]) to.getTOStructure().getColumns();
		Object[] columnValues = new Object[toColumns.length];
		for (int i = 0; i < toColumns.length; i++) {
			columnValues[i] = to.get(toColumns[i]);
		}
		
		// We start building the query
		StringBuilder query = new StringBuilder(50);
		query.append("INSERT INTO ");
		query.append(to.getTOStructure().getRepresentedTable());
		query.append("(");
		
		// We append the query columns
		for (int i = 0; i < toColumns.length; i++) {
			if (i != 0) {
				query.append(",");
			}
			query.append(toColumns[i]);
		}
		
		// We append the query values, while keeping track of the null valued columns
		List<Integer> nullColumnIndexes = new ArrayList<Integer>();
		List<Integer> nonNullColumnIndexes = new ArrayList<Integer>();
		query.append(") VALUES (");
		for (int i = 0; i < toColumns.length; i++) {
			if (i != 0) {
				query.append(",");
			}
			
			if (columnValues[i] == null) {
				nullColumnIndexes.add(i);
				query.append("DEFAULT");
			} else {
				nonNullColumnIndexes.add(i);
				query.append("?");
			}
		}
		
		query.append(")");
		
		// If the query contains null values, we will be using a RETURNING statement
		boolean returningStatement = (dbAccess.isReturningSupported() && nullColumnIndexes.size() != 0);
		
		if (returningStatement) {
			/*
			 * TODO RETURNING works with PostgreSQL but is not compatible with MS SQL Server.
			 * If we ever use auto-increment numbers or want to receive return values on
			 * a MS SQL Server BD, we will have to implement it using a different method
			 * (possibly stored procedures, see http://en.wikipedia.org/wiki/Insert_%28SQL%29).
			 */
			query.append(" RETURNING ");
			for (int i = 0; i < nullColumnIndexes.size(); i++) {
				if (i != 0) {
					query.append(",");
				}
				query.append(toColumns[nullColumnIndexes.get(i)]);
			}
		}
		
		try {
			// We prepare the query by setting all the values
			PreparedStatement ps = dbAccess.prepareStatement(query.toString());
			int sqlIndex = 1;
			for (Integer index: nonNullColumnIndexes) {
				ClassicSQLRequests.setPreparedStatement(ps, sqlIndex++, columnValues[index], toColumns[index].getMetaData().getSQLDataType());
			}
			
			/*
			 * We can now execute the query; if there were null values we have to use
			 * executeQuery() since we have return values, otherwise we use executeUpdate().
			 */
			if (!returningStatement) {
				ps.executeUpdate();
			} else {
				ResultSet rs = ps.executeQuery();
				rs.next();
				for (int i = 0; i < nullColumnIndexes.size(); i++) {
					to.set(toColumns[nullColumnIndexes.get(i)], rs.getObject(i+1));
				}
			}
			
		} catch (SQLException e) {
			System.err.println("Failed to insert the following TO: " + to);
			throw e;
		}
	}
	
	@Override
	public boolean update (TransferableObject TO) {
		if(TO==null){
			System.err.println("AbstractDatabaseDAO.update() has been called with TO=null!");
			Thread.dumpStack();
			return false;
		}
		
		String update = "UPDATE %s SET %s WHERE %s";
		TransferableObjectInfo<?> colObjList[] = null;
		StringBuilder columnList = new StringBuilder();
		StringBuilder primaryKeyList = new StringBuilder();
		/*
		 * generate the insert SQL request
		 */
		try {
			colObjList = TO.getTOStructure().getColumns();//getColumnList( TO );
			assert null != colObjList;
			assert 0 < colObjList.length && 0 < TO.getTOStructure().getPrimaryKeySize();
			assert TO.getTOStructure().getPrimaryKeySize() <= colObjList.length;
			//				long start = System.currentTimeMillis();
			/*
			 * Handles the Where statement first
			 */
			primaryKeyList.append( colObjList[0].toString() );
			primaryKeyList.append( " = ?" );
			int index = 1;
			while( index < TO.getTOStructure().getPrimaryKeySize() ) {
				primaryKeyList.append( " AND " );
				primaryKeyList.append( colObjList[index].toString() );
				primaryKeyList.append( " = ?" );
				index++;
			}
			/*
			 * Handles the new values to set
			 */
			columnList.append( colObjList[index].toString() );
			columnList.append( " = ?" );
			index++;
			while( index < colObjList.length ) {
				columnList.append( ", " );
				columnList.append( colObjList[index].toString() );
				columnList.append( " = ?" );
				index++;
			}
	
			String sqlQuery = String.format( update,
					TO.getTOStructure().getRepresentedTable().toString(),
					columnList.toString(),
					primaryKeyList.toString() ) ;
			
			PreparedStatement ps = dbAccess.prepareStatement( sqlQuery );
			/*
			 * insert the values into the SQL prepared statement
			 */
			for( int i = 0; i < colObjList.length; i++ ) {
				/*
				 * The modulo is to make sure that the Primary key will receive the right
				 * PreparedStatement index.
				 */
				ClassicSQLRequests.setPreparedStatement( ps, i + 1, TO.get( colObjList[(i + TO.getTOStructure().getPrimaryKeySize())%colObjList.length] ), colObjList[(i + TO.getTOStructure().getPrimaryKeySize())%colObjList.length].getMetaData().getSQLDataType() );
				
			}
			/*
			 * execute the update!!!
			 */
			 
			ps.executeUpdate();

			return true;
			
		}catch(SQLException e){
			System.err.println("Failed to update the following TO: " + TO);
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean delete (TransferableObject TO) {
		if(TO==null){
			System.err.println("AbstractDatabaseDAO.delete() has been called with TO=null!");
			Thread.dumpStack();
			return false;
		}
		
		String delete = "DELETE FROM %s WHERE %s ";
		TransferableObjectInfo<?> colObjList[] = null;
		StringBuilder primaryKeyList = new StringBuilder();
		/*
		 * generate the insert SQL request
		 */
		try {
			colObjList = TO.getTOStructure().getColumns();
			assert null != colObjList;
			assert 0 < colObjList.length && 0 < TO.getTOStructure().getPrimaryKeySize();
			assert TO.getTOStructure().getPrimaryKeySize() <= colObjList.length;
			/*
			 * Handles the Where statement first
			 */
			primaryKeyList.append( colObjList[0].toString() );
			primaryKeyList.append( " = ?" );
			int index = 1;
			while( index < TO.getTOStructure().getPrimaryKeySize() ) {
				primaryKeyList.append( " AND " );
				primaryKeyList.append( colObjList[index].toString() );
				primaryKeyList.append( " = ?" );
				index++;
			}
	
			String sqlQuery = String.format( delete, TO.getTOStructure().getRepresentedTable().toString(), primaryKeyList.toString() ) ;

			//			System.out.println( sqlQuery );
			PreparedStatement ps = dbAccess.prepareStatement( sqlQuery );
			/*
			 * insert the values into the SQL prepared statement
			 */
			for( int i = 0; i < TO.getTOStructure().getPrimaryKeySize(); i++ ) {
				//				System.out.println( colObjList[i] + " " + TO.get( colObjList[i] ) );
				ClassicSQLRequests.setPreparedStatement( ps, i + 1, TO.get( colObjList[i] ),colObjList[i].getMetaData().getSQLDataType()  );
			}
			/*
			 * execute the delete!!!
			 */
			ps.executeUpdate();
			return true;
			
		}catch(SQLException e){
			System.err.println("Failed to delete the following TO: " + TO);
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void deleteTO(TransferableObject to) throws NullPointerException, TONotFoundException, SQLException {
		if (to == null) {
			throw new NullPointerException();
		}
		
		String delete = "DELETE FROM %s WHERE %s ";
		TransferableObjectInfo<?> colObjList[] = null;
		StringBuilder primaryKeyList = new StringBuilder();
		
		colObjList = to.getTOStructure().getColumns();
		assert null != colObjList;
		assert 0 < colObjList.length && 0 < to.getTOStructure().getPrimaryKeySize();
		assert to.getTOStructure().getPrimaryKeySize() <= colObjList.length;
		
		/*
		 * Handles the Where statement first
		 */
		primaryKeyList.append( colObjList[0].toString() );
		primaryKeyList.append( " = ?" );
		int index = 1;
		while( index < to.getTOStructure().getPrimaryKeySize() ) {
			primaryKeyList.append( " AND " );
			primaryKeyList.append( colObjList[index].toString() );
			primaryKeyList.append( " = ?" );
			index++;
		}
		
		String sqlQuery = String.format( delete, to.getTOStructure().getRepresentedTable().toString(), primaryKeyList.toString() ) ;
		
		PreparedStatement ps = dbAccess.prepareStatement( sqlQuery );
		/*
		 * insert the values into the SQL prepared statement
		 */
		for( int i = 0; i < to.getTOStructure().getPrimaryKeySize(); i++ ) {
			ClassicSQLRequests.setPreparedStatement( ps, i + 1, to.get( colObjList[i] ),colObjList[i].getMetaData().getSQLDataType()  );
		}
		/*
		 * execute the delete!!!
		 */
		if (ps.executeUpdate() == 0) {
			throw new TONotFoundException("Failed to delete the following TO: " + to);
		}
	}
	
	@Override
	public void deleteTOSemiQuiet(TransferableObject to) throws NullPointerException, SQLException {
		try {
			deleteTO(to);
		} catch (TONotFoundException e) {
			// Do nothing of the TO is not found
		}
	}
	
	@Override
	public <T extends TransferableObject> void synchronizeTO(Collection<T> beforeTO, Collection<T> afterTO) throws SQLException {

		List<T> toAdd = new ArrayList<T>();
		if (afterTO != null) {
			toAdd.addAll(afterTO);
		}
		TOUtil.shallowRemoveAll(toAdd, beforeTO);
		
		List<T> toRemove = new ArrayList<T>();
		if (afterTO != null) {
			toRemove.addAll(beforeTO);
		}
		TOUtil.shallowRemoveAll(toRemove, afterTO);
		
		List<T> toUpdate = new ArrayList<T>();
		if (afterTO != null) {
			toUpdate.addAll(afterTO);
		}
		TOUtil.shallowRetainAll(toUpdate, beforeTO);
		
		//Remove missing TO
		for(T currTO : toRemove){
			deleteTO(currTO);
		}
		
		//Add TO
		for(T currTO : toAdd){
			insert(currTO);
		}
		
		//Update if necessary
		for(T currTO : toUpdate){
			//found this TO in the 'before' list
			for(T currBeforeTO : beforeTO){
				if(currTO.shallowEquals(currBeforeTO)){
					if(!currTO.deepEquals(currBeforeTO)){
						if (!update(currTO)) {
							throw new SQLException("Failed to update this TO: " + currTO);
						}
					}
				}
			}
		}
	}
	
	@Override
	public <T extends TransferableObject> T selectTO(Class<T> clazz, Object... keys) throws SQLException {
		return selectTO(TOUtil.createInstance(clazz, keys));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends TransferableObject> T selectTO(T to) throws SQLException {
		// Variables we will require
		TransferableObjectStructureIF structure = to.getTOStructure();
		int primaryKeySize = structure.getPrimaryKeySize();
		ColumnInfo<?>[] columns = structure.getColumns();
		
		// We build the select query
		StringBuilder selectQuery = new StringBuilder(50);
		selectQuery.append("SELECT * FROM ");
		selectQuery.append(structure.getRepresentedTable());
		selectQuery.append(" WHERE ");
		
		// We append the WHERE arguments
		for (int i = 0; i < primaryKeySize; i++) {
			if (i > 0) {
				selectQuery.append(" AND ");
			}
			selectQuery.append(columns[i]);
			selectQuery.append(" = ?");
		}
		
		// We prepare the query and set the variables
		PreparedStatement ps = dbAccess.prepareStatement(selectQuery.toString());
		for (int i = 0; i < primaryKeySize; i++) {
			ps.setObject(i+1, to.get(columns[i]));
		}
		
		// We execute the query
		return (T) ResultSetTranslator.getDistinctTOFromResultSet(ps.executeQuery(), structure.getRepresentedTOClass());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <TO extends TransferableObject, V> List<TO> selectTOListByKeys(TableInfo table, Collection<V> ids) {
		TransferableObjectStructureIF structure = TOUtil.getTOStructure(table.getTransferableObjectClass());
		return selectTOListByIDs(table, (ColumnInfo<V>)structure.getColumns()[0], ids);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <TO extends TransferableObject, V> List<TO> selectTOListByIDs(TableInfo table, ColumnInfo<V> column, Collection<V> ids) {
		if (ids == null || ids.isEmpty()) {
			return new ArrayList<TO>();
		}
		
		try {
			StringBuilder sb = new StringBuilder(200);
			sb.append(" SELECT * FROM " + table);
			sb.append(" WHERE " + column + " IN (");
			for (int i = 0; i < ids.size(); i++) {
				if (i != 0) {
					sb.append(", ");
				}
				sb.append("?");
			}
			sb.append(")");
			
			PreparedStatement ps = dbAccess.prepareStatement(sb.toString());
			
			int counter = 1;
			Iterator<V> iterator = ids.iterator();
			while (iterator.hasNext()) {
				ps.setObject(counter++, iterator.next());
			}
			
			return ResultSetTranslator.getTOListFromRS(ps.executeQuery(), (Class<TO>)table.getTransferableObjectClass());
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Select a list of TOs filtering by many different columns (Not necessarily the primary key).
	 * To select a TO by it's primary key, please use {@link AbstractDatabaseDAO#selectTO(TransferableObject)} instead.
	 * If some fields in the TO are initialized, they will be used in the where clause with the {@link QueryMathOperatorStruct#eq} operator
	 * @param <TO>	The geeric type of the selecting TO
	 * @param <V>	The generic type of
	 * @param to	A to with initialized members to be filtered by the query
	 * @return The list of matching TOs
	 * @throws SQLException
	 */
	public <TO extends TransferableObject> List<TO> selectTOList(TO to) throws SQLException {
		return selectTOList(to, null) ;
	}
	
	/**
	 * Select a list of TOs filtering by many different columns (Not necessarily the primary key).
	 * To select a TO by it's primary key, please use {@link AbstractDatabaseDAO#selectTO(TransferableObject)} instead.
	 * If some fields in the TO are initialized, they will be used in the where clause with the {@link QueryMathOperatorStruct#eq} operator
	 * Custom filters can be passed through the {@code filter} parameter
	 * @param <TO>	The generic type of the selecting TO
	 * @param to	A to with initialized members to be filtered by the query
	 * @param filters	A list of filters that are not representable with the {@link QueryMathOperatorStruct#eq} operator
	 * @return The list of matching TOs
	 * @throws SQLException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <TO extends TransferableObject> List<TO> selectTOList(TO to, List<TOColumnFilter<?> > filters) throws SQLException {
		if (to == null)	{
			return new ArrayList<TO>() ;
		}
		
		List<TOColumnFilter<?> > workFilters = new ArrayList<TOColumnFilter<?> >() ;
		
		Object valueToFilter = null ;
		
		for (ColumnInfo<?> column : to.getTOStructure().getColumns())	{
			valueToFilter = to.get(column) ;
			
			if (valueToFilter != null){
				workFilters.add(new TOColumnFilter(column, QueryMathOperatorStruct.eq, valueToFilter)) ;
			}
		}
		
		if (filters != null){
			workFilters.addAll(filters) ;
		}
		
		return (List<TO>) selectTOList(to.getClass(), workFilters) ;
	}
	
	
	public <TO extends TransferableObject> List<TO> selectTOList(Class<TO> clazz) throws SQLException {
		return selectTOList(clazz, null) ;
	}
	
	
	/**
	 * Select a list of TOs filtering by many different columns (Not necessarily the primary key).
	 * To select a TO by it's primary key, please use {@link AbstractDatabaseDAO#selectTO(TransferableObject)} instead.
	 * If some fields in the TO are initialised, they will be used in the where clause with the {@link QueryMathOperatorStruct#eq} operator
	 * Custom filters can be passed through the {@code filter} parameter
	 * @param <TO>	The generic type of the selecting TO
	 * @param clazz		The class of the TO type we look for
	 * @param filters	A list of filters that are not representable with the {@link QueryMathOperatorStruct#eq} operator
	 * @return The list of matching TOs
	 * @throws SQLException
	 */
	public <TO extends TransferableObject> List<TO> selectTOList(Class<TO> clazz, List<TOColumnFilter<?> > filters) throws SQLException {
		StringBuilder sb = new StringBuilder(500) ;
		sb.append("SELECT * FROM ") ;
		sb.append(TOUtil.getTOStructure(clazz).getRepresentedTable()) ;
		
		if (filters != null && filters.size() > 0){
			sb.append(" WHERE ");
	
			boolean firstItem = true ;
			for (TOColumnFilter<?> filter : filters)	{
				if (!firstItem){
					sb.append(" AND ") ;
				}
				else	{
					firstItem = false ;
				}
				
				sb.append(filter.getColumn()) ;
				sb.append(filter.getOperator().sql()) ;	// The operator contains spaces.  ie " = ", " <= "...
				
				/*
				 * in and not in must be managed differently because they have many values
				 */
				if (filter.getOperator() == QueryMathOperatorStruct.in || filter.getOperator() == QueryMathOperatorStruct.nin)	{
					sb.append('(') ;
					
					/*
					 * The value of the TOColumnFilter is an array in those cases
					 */
					Object[] valueFilters = (Object[]) filter.getValue() ;
	
					for (int i = 0; i < valueFilters.length; i++)	{
						if (i > 0){
							sb.append(',') ;
						}
						sb.append('?') ;
					}
	
					sb.append(')') ;
				}
				else	{
					sb.append("?") ;
				}
			}
		}

		PreparedStatement ps = dbAccess.prepareStatement(sb.toString()) ;
		
		if (filters != null){
			int i = 1 ;	// PreparedStatement index begins at 1
			for (TOColumnFilter<?> filter : filters)	{
				if (filter.getOperator() == QueryMathOperatorStruct.in || filter.getOperator() == QueryMathOperatorStruct.nin)	{
					/*
					 * The value of the TOColumnFilter is an array in those cases
					 */
					Object[] valueFilters = (Object[]) filter.getValue() ;
					
					for (Object valueFilter : valueFilters)	{
						ps.setObject(i++, valueFilter) ;
					}
				}
				else	{
					ps.setObject(i++, filter.getValue()) ;
				}
			}
		}
		
		return ResultSetTranslator.getTOListFromRS(ps.executeQuery(), clazz) ;
	}
	
	
	@Override
	public void upsert(TransferableObject to) throws SQLException {
		if (selectTO(to) == null) {
			insert(to);
		} else {
			if (!update(to)) {
				throw new SQLException();
			}
		}
	}
	
	public ResultSet select( QueryBuilder qb ) {
		StringBuilder sbSelect = new StringBuilder( 100 );
		/*
		 * Select
		 */
		sbSelect.append( "SELECT " );
		int nbOfFields = qb.selectedFields.size();
		if( 0 == nbOfFields ) {
			sbSelect.append( "* " );
		} else {
			TblColStruct field = qb.selectedFields.get( 0 );
			sbSelect.append( field.table.getTOStructure().getRepresentedTable().toString() );
			sbSelect.append( "." );
			sbSelect.append( field.col );
			for( int i = 1; i < nbOfFields; i++ ) {
				field = qb.selectedFields.get( i );
				sbSelect.append( "," );
				sbSelect.append( field.table.getTOStructure().getRepresentedTable().toString() );
				sbSelect.append( "." );
				sbSelect.append( field.col );
			}
			sbSelect.append( "\n" );
		}
		
		// find the largest key
		TransferableObject[] neededTables = qb.getSortedTables();
		int nbOfTables = neededTables.length - 1;
		TransferableObject bigestKeyTable = neededTables[nbOfTables];
		sbSelect.append( "FROM " );
		sbSelect.append( bigestKeyTable.getTOStructure().getRepresentedTable().toString() );
		sbSelect.append( "\n" );
		/*
		 * Inner Join
		 */
		for( int i = 0; i < nbOfTables; i++ ) {
			sbSelect.append( "INNER JOIN " );
			TransferableObject currentTable2Include = neededTables[i];
			sbSelect.append( currentTable2Include.getTOStructure().getRepresentedTable() );
			sbSelect.append( "\n\tON " );
			addJoiningCondition( sbSelect, bigestKeyTable, currentTable2Include, 0 );
			for( int j = 1; j < currentTable2Include.getTOStructure().getPrimaryKeySize(); j++ ) {
				sbSelect.append( "\n\tAND " );
				addJoiningCondition( sbSelect, bigestKeyTable, currentTable2Include, j );
			}
			sbSelect.append( "\n" );
		}
		
		/*
		 * Where
		 */
		List<QueryConditionStruct> elements = qb.getDNFQueryElements();
		Queue<String> valueQueue = new LinkedList<String>();
		if (elements != null) {
			sbSelect.append("WHERE ");
			
			for (int elementIndex = 0; elementIndex < elements.size(); elementIndex++) {
				if (elementIndex != 0) {
					sbSelect.append("\n\tOR ");
				}
				
				QueryConditionStruct condition = elements.get(elementIndex);
				for (int conditionIndex = 0; conditionIndex < condition.columns.size(); conditionIndex++) {
					if (conditionIndex != 0) {
						sbSelect.append(" AND ");
					}
					
					TransferableObjectInfo<?> column = condition.columns.get(conditionIndex);
					TOColumnMetaDataIF<?> metaData = column.getMetaData();
					String value;
					
					if(dbAccess.isCaseSensitive() && metaData.isText()) {
						sbSelect.append("LOWER(");
						sbSelect.append(qb.neededTables.get(condition.tables.get(conditionIndex)).getTOStructure().getRepresentedTable());
						sbSelect.append(".");
						sbSelect.append(column.toString());
						sbSelect.append(")");
						value = condition.values.get(conditionIndex).toString().toLowerCase();
					} else {
						sbSelect.append(qb.neededTables.get(condition.tables.get(conditionIndex)).getTOStructure().getRepresentedTable());
						sbSelect.append(".");
						sbSelect.append(column.toString());
						value = condition.values.get(conditionIndex).toString();
					}
					
					QueryMathOperatorStructIF operator = condition.mathOperators.get(conditionIndex);
					
					sbSelect.append(operator.sql());
					/*
					 * To protect against SQL injection, we queue the values to
					 * add and will be replacing the '?' via a PreparedStatement.
					 * 
					 * We need to treat "in" and "nin" differently because they
					 * contain multiple values.
					 */
					if (operator == QueryMathOperatorStruct.in || operator == QueryMathOperatorStruct.nin) {
						String[] values = value.split(",");
						
						sbSelect.append("(");
						for (int i = 0; i < values.length; i++) {
							if (i > 0) {
								sbSelect.append(",");
							}
							sbSelect.append("?");
							valueQueue.add(values[i].trim());
						}
						sbSelect.append(")");
						
					} else {
						sbSelect.append("?");
						valueQueue.add(value);
					}
					
				}
			}
		}
		
		/*
		 * Order By
		 */
		// OrderBy is still TODO
		
//		System.out.println( "dbgPrint iuhguhgdhjkbdf 1:\n" + sbSelect.toString() );
		try {
			PreparedStatement ps = dbAccess.prepareStatement(sbSelect.toString());
			
			/*
			 * We add the queued values to the prepared statement.
			 */
			int i = 1;
			while (!valueQueue.isEmpty()) {
//				System.out.println("dbgPrint rghnrgir 2: setString(" + i + ", " + valueQueue.peek() + ")");
				ps.setString(i++, valueQueue.poll());
			}
			//System.out.println(ps);
			return ps.executeQuery();
			
		} catch( SQLException e ) {
			/*
			 * The query didn't complete properly, this will happen if a value
			 * is not with the good type (i.e. secID = 'a'). We don't print
			 * the exception since it might be generated often when the users
			 * send invalid queries.
			 */
			return null;
		}
	}
	
	private void addJoiningCondition( StringBuilder sb, TransferableObject left, TransferableObject right, int index ) {
		/*
		 * This is an optimized searching function. Optimized because the index should be in most
		 * of the cases, the same for the left and the right sides.
		 */
		String leftColName = left.getTOStructure().getColumns()[index].toString();
		String rightColName = right.getTOStructure().getColumns()[index].toString();
		int i = 0;
		int nbOfCol = right.getTOStructure().getColumns().length;
		boolean found = leftColName.equals( rightColName );
		while( !found && i < nbOfCol ) {
			i++;
			rightColName = right.getTOStructure().getColumns()[(index + i) % nbOfCol].toString();
			found = leftColName.equals( rightColName );
		}
		if( !found ) {
			return;
		}
		sb.append( left.getTOStructure().getRepresentedTable() );
		sb.append( "." );
		sb.append( leftColName );
		sb.append( " = " );
		sb.append( right.getTOStructure().getRepresentedTable() );
		sb.append( "." );
		sb.append( rightColName );
	}
	
	@Override
	public void lockTable(TableInfo table) throws SQLException {
		if (dbAccess.dbConnection.getAutoCommit()) {
			throw new SQLException("lockTable() can only be called in a transaction.");
		} else {
			dbAccess.executeUpdate("lock table " + table);
		}
	}
}
