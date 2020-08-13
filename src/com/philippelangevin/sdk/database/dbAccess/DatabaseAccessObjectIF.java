package com.philippelangevin.sdk.database.dbAccess;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.philippelangevin.sdk.dataStructure.ObjectUtil;
import com.philippelangevin.sdk.database.exception.TONotFoundException;
import com.philippelangevin.sdk.database.tables.TableInfo;
import com.philippelangevin.sdk.database.transferableObject.ColumnInfo;
import com.philippelangevin.sdk.database.transferableObject.TransferableObject;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectInfo;

/**
 * <p> Title: {@link DatabaseAccessObjectIF} <p>
 * <p> Description: The interface for the class DatabasAccessObjectIF</p>
 * <p> Company : C-Tec <p>
 *
 * @author sroger
 * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * ------------------------------------------------
 * Date			Name		BT		Description
 * 2010-01-19		sroger				initial Revision
 */

public interface DatabaseAccessObjectIF {

	/**
	 * Closes the connection to the DB.
	 */
	public void close();
	
	/**
	 * Apply the modification to the DB.
	 */
	public void commit() throws SQLException;
	
	/**
	 * Defers all deferrable constraints, so that they are only checked on commit.
	 */
	public void deferConstraints();
	
	/**
	 * Makes all constraints immediate, so they are all checked immediately upon executing a statement.
	 */
	public void undeferConstraints();
	
	/**
	 * Closes and re-opens the connection to the DB.
	 */
	public void reconnect();

	/**
	 * Return if the AutoCommit is activated.
	 */
	public boolean isAutoCommit();
	
		
	/**
	 * Insère le TransferableObject envoyé en paramètre dans la table appropriée.
	 * Si le TO contient des colonnes "autonumbers", elles seront ajustées via set().
	 * Si le TO contient des valeurs par défaut, elles seront ajustées via set().
	 * @param TransferableObject
	 * @throws SQLException est lancé si il y a eu une erreur a l'insersion.
	 */
	public void insert (TransferableObject TO) throws SQLException;
	
	/** Update le TransferableObject envoyé en paramètre.
	 * @param 	TransferableObject
	 * @return 	False s'il y a eu une erreur.
	 *          True peu importe si le TO existe ou pas
	 */
	public boolean update (TransferableObject TO);
	
	/**Supprime le TransferableObject envoyé en paramètre
	 * @param	TransferableObject
	 * @return 	False s'il y a eu une erreur.
	 *          True peu importe si le TO existe ou pas.
	 * @deprecated Use {@link #deleteTO(TransferableObject)} instead.
	 */
	@Deprecated
	public boolean delete (TransferableObject TO);
	
	/**
	 * Deletes a TO, throws an exception if the TO was not found.
	 * @param to The TransferableObject to delete
	 * @throws NullPointerException Thrown if to == null.
	 * @throws TONotFoundException Thrown if the TO was not deleted.
	 * @throws SQLException Thrown if a SQL error occurred.
	 */
	public void deleteTO(TransferableObject to) throws NullPointerException, TONotFoundException, SQLException;
	
	/**
	 * Deletes a TO, won't throw an exception if the TO is not found (but will if
	 * a SQL connection exception is thrown).
	 * @param to The TransferableObject to delete
	 * @throws NullPointerException Thrown if to == null.
	 * @throws SQLException Thrown if a SQL error occurred.
	 */
	public void deleteTOSemiQuiet(TransferableObject to) throws NullPointerException, SQLException;
	
	/**
	 * This method will synchronize the list of TO before and after some manipulation.
	 * The TOs must have a shallowEquals(...) and deepEquals(...) methods correctly defined.
	 * It will insert TOs present in afterTOList but not in beforeTOList.
	 * It will delete TOs present in beforeTOList but not in afterTOList.
	 * It will update TOs present in both list if deepEquals returns false.
	 * @param beforeTO
	 * @param afterTO
	 * @throws SQLException Thrown if any of the delete/insert/update method fails (you should rollback!)
	 */
	public <T extends TransferableObject> void synchronizeTO(Collection<T> beforeTOList, Collection<T> afterTOList)  throws SQLException;

	/**
	 * Selects a TO based on the provided class and primary key values.
	 * Warning: Does not support additional fields.
	 * @param <T>
	 * @param clazz
	 * @param keys
	 * @return
	 * @throws SQLException
	 */
	public <T extends TransferableObject> T selectTO(Class<T> clazz, Object... keys) throws SQLException;
	
	/**
	 * Selects a TO based on another TO's primary key.
	 * Warning: Does not support additional fields.
	 * @param to A TO containing its primary key
	 * @return The selected TO
	 * @throws SQLException
	 */
	public <T extends TransferableObject> T selectTO(T to) throws SQLException;
	
	/**
	 * Selects a list of TOs based on the keys provided.
	 * Warning: Does not support additional fields.
	 * @param table
	 * @param ids
	 * @return
	 */
	public <TO extends TransferableObject, V> List<TO> selectTOListByKeys(TableInfo table, Collection<V> ids);
	
	/**
	 * Selects a list of TOs based on the IDs associated with the provided column.
	 * Warning: Does not support additional fields.
	 * @param table
	 * @param column
	 * @param ids
	 * @return
	 */
	public <TO extends TransferableObject, V> List<TO> selectTOListByIDs(TableInfo table, ColumnInfo<V> column, Collection<V> ids);
	
	/**
	 * Inserts a TO if it doesn't exist, otherwise the TO is updated.
	 * 
	 * Note that this method is not safe; a TO is selected from the DB, then an
	 * insert or update is being executed. If you want to be 100% safe you need
	 * to lock the DB table before calling this method, and unlock it afterwards.
	 * @param to The TO to insert or update
	 * @throws SQLException
	 */
	public void upsert(TransferableObject to) throws SQLException;
	
	public void beginTransaction() throws SQLException;
	
	public boolean rollback();
	
	/**
	 * Locks the specified table to ensure no concurrent access will be made.
	 * Note that this table will be completely blocked until the transaction is
	 * finished or aborted, which means other queries will wait until this is done.
	 * This method should only be invoked during a transaction.
	 * 
	 * IMPORTANT: Make sure to commit, rollback or close the DAO after calling
	 * this method, or the database table might completely freeze indefinitely.
	 * You should surround it with a try/finally block.
	 * @param table
	 * @throws SQLException
	 */
	public void lockTable(TableInfo table) throws SQLException;
	
	
	/*
	 *   ___                        ____        _ _     _
	 *  / _ \ _   _  ___ _ __ _   _| __ ) _   _(_) | __| | ___ _ __
	 * | | | | | | |/ _ \ '__| | | |  _ \| | | | | |/ _` |/ _ \ '__|
	 * | |_| | |_| |  __/ |  | |_| | |_) | |_| | | | (_| |  __/ |
	 *  \__\_\\__,_|\___|_|   \__, |____/ \__,_|_|_|\__,_|\___|_|
	 *                        |___/
	 * ... and the set of functions using it!
	 */
	//TODO: Correct the vars and class accessibility
	//TODO: Joining tables will not work unless you use Coolvision tables.
	public class QueryBuilder {
		
		/*protected*/public static class TblColStruct {
			/*protected*/public TransferableObject table;
			/*protected*/public String col;
			
			@Override
			public boolean equals(Object o) {
				if (o == null || !(o instanceof TblColStruct)) {
					return false;
				}
				
				TblColStruct tblCol = (TblColStruct) o;
				return this.table.equals(tblCol.table) && this.col.equals(tblCol.col);
			}
			
			/* (non-Javadoc)
			 * @see java.lang.Object#hashCode()
			 */
			@Override
			public int hashCode() {
				return ObjectUtil.hashCode(table, col);
			}
		}
		
		/**
		 * This interface represents an element in a query, it can basically
		 * be an operator or a condition.
		 */
		public interface QueryElementStruct {}
		
		/**
		 * This enum represents the supported types of operators.
		 */
		public enum QueryOperatorStruct implements QueryElementStruct {
			AND, OR, NOT, PARENTHESIS_OPEN, PARENTHESIS_CLOSE;
		}
		
		public interface QueryMathOperatorStructIF {
			public String sql();
			public String xml();
			public QueryMathOperatorStructIF not();
			@Override
			public String toString();
		}
		
		/**
		 * This enum represents the supported types of math operators.
		 * 
		 * WARNING! The 'like' and 'nlike' operators are not as flexible in XML
		 * as they are in SQL. If they are used, the wildcard symbols '%' will
		 * simply be removed from the string and contains() will be called on
		 * the string (i.e. as if you used '%something%'). The '_' SQL wildcard
		 * is not a wildcard in XML mode, it is treated as a normal character.
		 * To prevent XML injection, we also stript the chars: < > / ' = "
		 */
		public enum QueryMathOperatorStruct implements QueryMathOperatorStructIF {
			eq			{@Override public String sql() { return " = "; }
						 @Override public String xml() { return " = "; }
						 @Override public QueryMathOperatorStructIF not() { return neq; }},
			neq			{@Override public String sql() { return " <> "; }
						 @Override public String xml() { return " != "; }
						 @Override public QueryMathOperatorStructIF not() { return eq; }},
			lt			{@Override public String sql() { return " < "; }
						 @Override public String xml() { return " < "; }
						 @Override public QueryMathOperatorStructIF not() { return gte; }},
			lte			{@Override public String sql() { return " <= "; }
						 @Override public String xml() { return " <= "; }
						 @Override public QueryMathOperatorStructIF not() { return gt; }},
			gt			{@Override public String sql() { return " > "; }
						 @Override public String xml() { return " > "; }
						 @Override public QueryMathOperatorStructIF not() { return lte; }},
			gte			{@Override public String sql() { return " >= "; }
						 @Override public String xml() { return " >= "; }
						 @Override public QueryMathOperatorStructIF not() { return lt; }},
			contains	{@Override public String sql() { return " like "; }
						 @Override public String xml() { return "contains(?, ?)"; }
						 @Override public QueryMathOperatorStructIF not() { return nlike; }},
			ncontains	{@Override public String sql() { return " not like "; }
						 @Override public String xml() { return "not (contains(?, ?))"; }
						 @Override public QueryMathOperatorStructIF not() { return like; }},
			like		{@Override public String sql() { return " like "; }
						 @Override public String xml() { return "contains(?, ?)"; }
						 @Override public QueryMathOperatorStructIF not() { return nlike; }},
			nlike		{@Override public String sql() { return " not like "; }
						 @Override public String xml() { return "not (contains(?, ?))"; }
						 @Override public QueryMathOperatorStructIF not() { return like; }},
			in			{@Override public String sql() { return " in "; }
						 @Override public String xml() { return " ? "; }
						 @Override public QueryMathOperatorStructIF not() { return nin; }},
			nin			{@Override public String sql() { return " not in "; }
						 @Override public String xml() { return "not ( ? )"; }
						 @Override public QueryMathOperatorStructIF not() { return in; }
			};

			@Override
			public String toString() {
				return sql();
			}
		}


		
		/**
		 * This class represents a condition in a query. The elements are all
		 * contained in lists because the conditions separated by the AND
		 * operator will be merged into single elements. No optimisations are
		 * made at the present time for the merging process, so identical
		 * conditions could repeat in the lists.
		 * 
		 * Comparing dates: If you require to compare a Timestamp object, the
		 * currently supported format for the string value is "yyyy-mm-dd".
		 * If value differs from this format, the query might not result any result.
		 * Note that it isn't possible at this point to compare a time value.
		 */
		public static class QueryConditionStruct implements QueryElementStruct {
			/*
			 * These lists stay synchronized at all times.
			 */
			/*protected*/public List<Class<? extends TransferableObject>> tables = new LinkedList<Class<? extends TransferableObject>>();
			/*protected*/public List<TransferableObjectInfo<?>> columns = new LinkedList<TransferableObjectInfo<?>>();
			/*protected*/public List<QueryMathOperatorStructIF> mathOperators = new LinkedList<QueryMathOperatorStructIF>();
			/*protected*/public List<Object> values = new LinkedList<Object>();
			
			/*
			 * Regular constructor, adds a single element to the condition.
			 */
			public <T> QueryConditionStruct(Class<? extends TransferableObject> table, TransferableObjectInfo<T> column, QueryMathOperatorStructIF mathOperator, T value) {
				tables.add(table);
				columns.add(column);
				mathOperators.add(mathOperator);
				values.add(value);
			}
			
			/*
			 * Copy constructor
			 */
			public QueryConditionStruct(QueryConditionStruct condition) {
				merge(condition);
			}
			
			/*
			 * Merges two conditions together.
			 */
			public void merge(QueryConditionStruct condition) {
				this.tables.addAll(condition.tables);
				this.columns.addAll(condition.columns);
				this.mathOperators.addAll(condition.mathOperators);
				this.values.addAll(condition.values);
			}
			
			/*
			 * Applies the NOT operator on each condition.
			 */
			public void applyNot() {
				for (int i = 0; i < mathOperators.size(); i++) {
					mathOperators.set(i, mathOperators.get(i).not());
				}
			}
			
			@Override
			public boolean equals(Object o) {
				if (o != null && o instanceof QueryConditionStruct) {
					QueryConditionStruct cond = (QueryConditionStruct) o;
					return  this.tables.equals(cond.tables) && this.columns.equals(cond.columns) &&
							this.mathOperators.equals(cond.mathOperators) && this.values.equals(cond.values);
				} else {
					return false;
				}
			}
			
			/* (non-Javadoc)
			 * @see java.lang.Object#hashCode()
			 */
			@Override
			public int hashCode() {
				return ObjectUtil.hashCode(tables, columns, mathOperators, values);
			}
			
			public List<TransferableObjectInfo<?>> getColumns() {
				return this.columns;
			}
			
			public List<QueryMathOperatorStructIF> getMathOperators() {
				return this.mathOperators;
			}
			
			public List<Object> getValues() {
				return this.values;
			}
		}
		
		/*
		 * The selected tables and fields.
		 */
		/*protected*/public Map<Class<? extends TransferableObject>, TransferableObject> neededTables = new HashMap<Class<? extends TransferableObject>,TransferableObject>();
		/*protected*/public List<TblColStruct> selectedFields = new ArrayList<TblColStruct>();
		
		/*
		 * The list of elements in our query, it is private since we should only
		 * be able to access it via getDNFQueryElements().
		 */
		private List<QueryElementStruct> elements = new LinkedList<QueryElementStruct>();
		
		/*
		 * These variables are using during the construction of the query, to
		 * make sure the new elements are valid with the expected content.
		 */
		private int openParentheses = 0;
		private boolean awaitingCondition = true;
		
		/**
		 * Regular constructor
		 */
		public QueryBuilder() {
		}
		
		/**
		 * Copy constructor
		 * @param builder
		 */
		public QueryBuilder(QueryBuilder builder) {
			this.neededTables.putAll(builder.neededTables);
			this.selectedFields.addAll(builder.selectedFields);
			this.openParentheses = builder.openParentheses;
			this.awaitingCondition = builder.awaitingCondition;
			
			for (QueryElementStruct element: builder.elements) {
				/*
				 * The conditions are not immutable, so we need to explicitly
				 * use the copy constructor to get a copy. The operators come
				 * from an enum so they are immutable.
				 */
				if (element instanceof QueryConditionStruct) {
					this.elements.add(new QueryConditionStruct((QueryConditionStruct)element));
				} else {
					this.elements.add(element);
				}
			}
		}
		
		protected TransferableObject manageNeededTables( Class<? extends TransferableObject> toClass ) throws InstantiationException, IllegalAccessException {
			TransferableObject to = this.neededTables.get( toClass );
			if( null == to ) {
				to = toClass.newInstance();
				this.neededTables.put( toClass, to );
			}
			return to;
		}
		
		public void addJoiningTable( TableInfo table ) throws InstantiationException, IllegalAccessException {
			manageNeededTables( table.getTransferableObjectClass() );
		}
		
		@SuppressWarnings("unchecked")
		public void addJoiningTable( String tableTOCanonicalName ) throws InstantiationException, IllegalAccessException{
			try {
				manageNeededTables( (Class<? extends TransferableObject>)this.getClass().getClassLoader().loadClass( tableTOCanonicalName ) );
			} catch( ClassNotFoundException e ) {
				e.printStackTrace();
			}
		}
		
		public void addJoiningTable( Class<? extends TransferableObject> tableTOClass ) throws InstantiationException, IllegalAccessException {
			manageNeededTables( tableTOClass );
		}
		
		public void addSelection(
				TableInfo table,
				String col
				) {
			addSelection( table.getTransferableObjectClass(), col );
		}
		
		@SuppressWarnings("unchecked")
		public void addSelection(
				ColumnInfo<?> col
				) {
			addSelection(
					(Class<? extends TransferableObject>)col.getClass().getDeclaringClass(),
					col.toString()
			);
		}
		
		@SuppressWarnings("unchecked")
		public void addSelection(
				String tableTOCanonicalName,
				String col
				) throws ClassNotFoundException {
			addSelection(
					(Class<? extends TransferableObject>)this.getClass().getClassLoader().loadClass( tableTOCanonicalName ),
					col
			);
		}
		
		public void addSelection(
				Class<? extends TransferableObject> tableTOClass,
				String col
				) {
			try {
				TransferableObject to = manageNeededTables( tableTOClass );
				TblColStruct selection = new TblColStruct();
				selection.table = to;
				selection.col = col;
				selectedFields.add( selection );
			} catch( InstantiationException e ) {
				e.printStackTrace();
			} catch( IllegalAccessException e ) {
				e.printStackTrace();
			}
		}
		
		/*
		 * Sorts the tables by primary key size.
		 */
		public TransferableObject[] getSortedTables() {
			TransferableObject[] sortedTables = neededTables.values().toArray(new TransferableObject[0]);
			
			Arrays.sort(sortedTables, new Comparator<TransferableObject>() {
				@Override
				public int compare(TransferableObject to1, TransferableObject to2) {
					return to1.getTOStructure().getPrimaryKeySize() - to2.getTOStructure().getPrimaryKeySize();
				}
			});
			return sortedTables;
		}
		
		/*
		 * Adds an operator to the query.
		 * Will return an exception if the operator is not valid at this position.
		 */
		
		public void addOperator(QueryOperatorStruct operator) throws
					IllegalStateException, NullPointerException, UnsupportedOperationException {
			
			if (operator == null) {
				throw new NullPointerException();
			}
			
			switch (operator) {
			case PARENTHESIS_OPEN:
				if (!awaitingCondition) {
					throw new IllegalStateException();
				}
				openParentheses++;
				break;
				
			case PARENTHESIS_CLOSE:
				if (openParentheses <= 0 || awaitingCondition) {
					throw new IllegalStateException();
				} else {
					openParentheses--;
				}
				break;
				
			case NOT:
				if (!awaitingCondition) {
					throw new IllegalStateException();
				}
				break;
				
			case AND: // Continue to OR
			case OR:
				if (awaitingCondition) {
					throw new IllegalStateException();
				}
				awaitingCondition = true;
				break;
				
			default:
				throw new UnsupportedOperationException();
			}
			
			elements.add(operator);
		}
		
		@SuppressWarnings("unchecked")
		public <T> void addCondition(String tableTOCanonicalName, TransferableObjectInfo<T> column, QueryMathOperatorStructIF mathOperator, T value) throws IllegalStateException, NullPointerException, ClassNotFoundException {
			addCondition(new QueryConditionStruct(
					(Class<? extends TransferableObject>)this.getClass().getClassLoader().loadClass(tableTOCanonicalName),
					column, mathOperator, value));
		}
		
		public <T> void addCondition(TableInfo tableInfo, TransferableObjectInfo<T> column, QueryMathOperatorStructIF mathOperator, T value) {
			addCondition(new QueryConditionStruct(tableInfo.getTransferableObjectClass(), column, mathOperator, value));
		}
		
		public <T> void addCondition(Class<? extends TransferableObject> table, TransferableObjectInfo<T> column, QueryMathOperatorStructIF mathOperator, T value) {
			addCondition(new QueryConditionStruct(table, column, mathOperator, value));
		}
		
		/*
		 * Adds a condition to the query.
		 * Will return an exception if the condition is not valid at this position.
		 */
		public void addCondition(QueryConditionStruct condition) throws IllegalStateException, NullPointerException {
			if (condition == null) {
				throw new NullPointerException();
			} else if (!awaitingCondition) {
				throw new IllegalStateException();
			}
			
			/*
			 * contains and ncontains are special cases that are wrapped with %
			 */
			QueryMathOperatorStructIF op = condition.mathOperators.get(0);
			if (op == QueryMathOperatorStruct.contains || op == QueryMathOperatorStruct.ncontains) {
				condition.values.set(0, "%" + condition.values.get(0) + "%");
			}
			
			awaitingCondition = false;
			elements.add(condition);
		}
		
		/**
		 * Adds an element to the query (either condition or operator).
		 * @param element
		 */
		public void addQueryElement(QueryElementStruct element) {
			if (element instanceof QueryOperatorStruct) {
				addOperator((QueryOperatorStruct)element);
			} else if (element instanceof QueryConditionStruct) {
				addCondition((QueryConditionStruct)element);
			} else {
				throw new UnsupportedOperationException();
			}
		}
		
		/**
		 * Convenience method to add multiple elements at once, whether
		 * they are conditions or operators.
		 * @param elements
		 */
		public void addQueryElements(List<QueryElementStruct> elements) {
			if (elements != null) {
				for (QueryElementStruct element: elements) {
					addQueryElement(element);
				}
			}
		}
		
		/**
		 * Returns the elements in an equivalent Disjunctive Normal Form to
		 * facilitate building the query. The list returned will contain only
		 * QueryConditionStruct elements, which are logically disjoint.
		 * 
		 * WARNING! The list of elements is actually modified, so you might not
		 * have the expected result if you call .equals() afterwards, so it is
		 * recommended to use the copy constructor if you need to invoke equals().
		 * 
		 * The strategy used to get the DNF form is not the most efficient, but
		 * it promises an accurate result in all cases. It consists of:
		 *  - We get rid of all NOT operators by applying it to all elements
		 *    (it carries on the parentheses)
		 *  - While there are still parentheses, we handle the deepest level
		 *    and then merge the elements together whenever possible until
		 *    there are no more parentheses.
		 *  - The final result will contain a list of conditions separated by
		 *    OR operators... we simply build a list with the conditions.
		 * 
		 *  @return Will return null if there are no elements.
		 *          Will throw an exception if the query is not in a valid state.
		 */
		public List<QueryConditionStruct> getDNFQueryElements() throws IllegalStateException {
			if (elements.isEmpty()) {
				return null;
			} else if (awaitingCondition || openParentheses != 0) {
				throw new IllegalStateException();
			}
			
			removeNotOperators();
			
			int indexDeepest = getIndexDeepestParenthesisLevel();
			while (indexDeepest != -1) {
				mergeAndOperators();
				/*
				 * We need to recalculate the position of the deepest
				 * parentheses at each step since the list will change.
				 */
				indexDeepest = getIndexDeepestParenthesisLevel();
				mergeParenthesis(indexDeepest);
				indexDeepest = getIndexDeepestParenthesisLevel();
			}
			
			/*
			 * We apply a final AND merge, and then afterwards we're certain
			 * of only having conditions separated by ORs.
			 */
			mergeAndOperators();
			
			List<QueryConditionStruct> dnfElements = new LinkedList<QueryConditionStruct>();
			for (int i = 0; i < elements.size(); i+=2) {
				dnfElements.add((QueryConditionStruct) elements.get(i));
			}
			
			return dnfElements;
		}
		
		/*
		 * This removes all the NOT operators by applying it to the different
		 * conditions and operators.
		 */
		private void removeNotOperators() {
			/*
			 * index of the element we're visiting
			 */
			int index = 0;
			
			/*
			 * Whether the NOT operator has been applied yet or not. This is
			 * useful to ensure it carries on parentheses, but in front of
			 * single condition elements it is a one-time use.
			 */
			boolean appliedNotOp = true;
			
			/*
			 * Whether we apply the NOT operator or not at any given time.
			 */
			boolean notModifier = false;
			
			/*
			 * Stack of notModifier states to make sure the parentheses
			 * are handled correctly (upon opening and closing a parenthesis,
			 * we add and pop the notModifier state).
			 */
			Stack<Boolean> notModifierStack = new Stack<Boolean>();
			
			while (index < elements.size()) {
				QueryElementStruct next = elements.get(index);
				
				if (next == QueryOperatorStruct.NOT) {
					/*
					 * appliedNoOp is reversed to handle both cases;
					 * whether it's a new NOT, and the case NOT NOT.
					 */
					elements.remove(index);
					notModifier = !notModifier;
					appliedNotOp = !appliedNotOp;
					/*
					 * We do not modify the index since we removed an element.
					 */
					
				} else if (next == QueryOperatorStruct.PARENTHESIS_OPEN) {
					/*
					 * We store the notModifier state upon entering a
					 * parenthesis. If the NOT operator is new, we only apply
					 * it on the elements contained in this specific block...
					 * if it isn't new we have to keep applying it after on the
					 * elements after the closing parenthesis.
					 */
					if (!appliedNotOp) {
						notModifierStack.add(!notModifier); // !notModifier
						appliedNotOp = true;
					} else {
						notModifierStack.add(notModifier);  // notModifier
					}
					index++;
					
				} else if (next == QueryOperatorStruct.PARENTHESIS_CLOSE) {
					notModifier = notModifierStack.pop();
					index++;
					
				} else if (next == QueryOperatorStruct.AND) {
					if (notModifier) {
						elements.set(index, QueryOperatorStruct.OR);
					}
					index++;
					
				} else if (next == QueryOperatorStruct.OR) {
					if (notModifier) {
						elements.set(index, QueryOperatorStruct.AND);
					}
					index++;
					
				} else if (next instanceof QueryConditionStruct) {
					if (notModifier) {
						((QueryConditionStruct)next).applyNot();
					}
					
					if (!appliedNotOp) {
						/*
						 * The NOT operator is only applied on this condition.
						 */
						notModifier = !notModifier;
						appliedNotOp = true;
					}
					index++;
					
				} else {
					throw new UnsupportedOperationException();
				}
			}
		}
		
		/*
		 * Returns the index of the (first) deepest opening parenthesis.
		 * Returns -1 if there are no parentheses.
		 */
		private int getIndexDeepestParenthesisLevel() {
			int currentParenthesis = 0;
			int maxParenthesis = 0;
			int indexMaxParenthesis = -1;
			
			for (int i = 0; i < elements.size(); i++) {
				QueryElementStruct element = elements.get(i);
				
				if (element == QueryOperatorStruct.PARENTHESIS_OPEN) {
					currentParenthesis++;
					if (currentParenthesis > maxParenthesis) {
						maxParenthesis = currentParenthesis;
						indexMaxParenthesis = i;
					}
				} else if (element == QueryOperatorStruct.PARENTHESIS_CLOSE) {
					currentParenthesis--;
				}
			}
			
			return indexMaxParenthesis;
		}
		
		/*
		 * Merges all conditions that are directly separated by an AND operator.
		 */
		private void mergeAndOperators() {
			int index = 0;
			
			while (index < elements.size() - 2) {
				QueryElementStruct condition1 = elements.get(index);
				QueryElementStruct operator   = elements.get(index+1);
				QueryElementStruct condition2 = elements.get(index+2);
				
				if (condition1 instanceof QueryConditionStruct && operator == QueryOperatorStruct.AND && condition2 instanceof QueryConditionStruct) {
					((QueryConditionStruct)condition1).merge((QueryConditionStruct)condition2);
					elements.remove(index+1); // AND
					elements.remove(index+1); // condition2
					/*
					 * We do not modify the index since the next one might be a merge again.
					 */
				} else {
					index++;
				}
			}
		}
		
		/*
		 * This transforms the deepest level of parentheses towards a DNF-friendly form.
		 * Within the parentheses there are conditions separated by ORs (no ANDs, no NOTs).
		 * We deal with one single case at a time, so a more complicated case such as
		 * (...) and (...) and (...) will require more than one method call.
		 * 
		 * We can be into these cases:
		 *  - AND in front of the parentheses: We merge this AND with each
		 *    element of the parentheses, the element is always a simple condition.
		 *  - AND after the parentheses: We either have a simple condition after,
		 *    or we might have another opening parentheses... two separate cases.
		 *  - Nothing or OR operators in front and behind the parentheses:
		 *    we can simply remove these parentheses.
		 * 
		 * This method guarantees progress whenever it is called (and parentheses
		 * are present), so several calls to this method will eventually remove
		 * all parentheses from the expression.
		 */
		private void mergeParenthesis(int indexDeepest) {
			/*
			 * We start by finding the closing parenthesis.
			 */
			int indexClosing = indexDeepest + 1;
			while (elements.get(indexClosing) != QueryOperatorStruct.PARENTHESIS_CLOSE) {
				indexClosing++;
			}
			
			if (indexDeepest != 0 && elements.get(indexDeepest) == QueryOperatorStruct.AND) {
				/*
				 * We merge on the left side with a single condition.
				 * Form: cond AND (...)
				 */
				
				/*
				 * We remove the element to merge from the list and re-adjust the indexes.
				 */
				QueryConditionStruct mergingElement = (QueryConditionStruct) elements.get(indexDeepest - 2);
				elements.remove(indexDeepest-2);
				elements.remove(indexDeepest-1);
				indexClosing -= 2;
				int indexElement = indexDeepest - 1;
				
				while (indexElement < indexClosing) {
					QueryElementStruct element = elements.get(indexElement);
					
					if (element instanceof QueryConditionStruct) {
						((QueryConditionStruct) element).merge(mergingElement);
						indexElement += 2;
					} else {
						throw new AssertionError();
					}
				}
				
			} else if (indexClosing < elements.size() - 1 && elements.get(indexClosing+1) == QueryOperatorStruct.AND) {
				/*
				 * We merge on the right side with either a condition or parentheses.
				 */
				
				if (elements.get(indexClosing + 2) instanceof QueryConditionStruct) {
					/*
					 * Form: (...) AND cond
					 */
					
					/*
					 * We remove the element to merge from the list.
					 */
					QueryConditionStruct mergingElement = (QueryConditionStruct) elements.get(indexClosing + 2);
					elements.remove(indexClosing+1);
					elements.remove(indexClosing+1);
					int indexElement = indexDeepest + 1;
					
					while (indexElement < indexClosing) {
						QueryElementStruct element = elements.get(indexElement);
						
						if (element instanceof QueryConditionStruct) {
							((QueryConditionStruct) element).merge(mergingElement);
							indexElement += 2; // We can step over the operators
						} else {
							throw new AssertionError();
						}
					}
					
				} else {
					/*
					 * Form: (...) AND (...)
					 */
					
					/*
					 * We get the list of elements requiring an update (first parenthesis).
					 */
					int indexElement = indexDeepest + 1;
					QueryElementStruct element;
					
					List<QueryConditionStruct> updatingConditions = new LinkedList<QueryConditionStruct>();
					element = elements.get(indexElement);
					while (element != QueryOperatorStruct.PARENTHESIS_CLOSE) {
						if (element instanceof QueryConditionStruct) {
							updatingConditions.add((QueryConditionStruct) element);
						}
						elements.remove(indexElement);
						element = elements.get(indexElement);
					}
					
					elements.remove(indexElement); // closing parenthesis
					elements.remove(indexElement); // AND operator
					elements.remove(indexElement); // opening parenthesis
					
					/*
					 * Now we get the list of elements in the second parentheses.
					 */
					List<QueryConditionStruct> mergingConditions = new LinkedList<QueryConditionStruct>();
					element = elements.get(indexElement);
					while (element != QueryOperatorStruct.PARENTHESIS_CLOSE) {
						if (element instanceof QueryConditionStruct) {
							mergingConditions.add((QueryConditionStruct)element);
						}
						elements.remove(indexElement);
						element = elements.get(indexElement);
					}
					
					/*
					 * We merge the elements of both parentheses together.
					 * The result will give (size1 * size2) merged elements.
					 */
					for (QueryConditionStruct updatingCondition: updatingConditions) {
						for (QueryConditionStruct mergingCondition: mergingConditions) {
							QueryConditionStruct newCondition = new QueryConditionStruct(updatingCondition);
							newCondition.merge(mergingCondition);
							
							elements.add(indexElement++, newCondition);
							elements.add(indexElement++, QueryOperatorStruct.OR);
						}
					}
					
					/*
					 * An extra OR has been added at the end... we simply remove it.
					 */
					elements.remove(indexElement - 1);
					
				}
				
			} else {
				/*
				 * In this case the parentheses aren't linked by an AND operator,
				 * so we can simply remove the parentheses.
				 */
				
				elements.remove(indexClosing);
				elements.remove(indexDeepest);
			}
		}
		
		/**
		 * We implement equals to indicate whether a query is identical to another.
		 * 
		 * WARNING! If getDNFQuery() has been called, the structure will have been
		 * modified... so you will need to call getDNFQuery() on both instances to
		 * ensure a valid result. This method will not call implicitly to avoid
		 * getting an IllegalOperatorException thrown.
		 */
		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof QueryBuilder)) {
				return false;
			}
			
			QueryBuilder q = (QueryBuilder) o;
			
			return	this.awaitingCondition == q.awaitingCondition &&
					this.openParentheses == q.openParentheses &&
					this.elements.equals(q.elements) &&
					this.selectedFields.equals(q.selectedFields) &&
					this.neededTables.equals(q.neededTables);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return ObjectUtil.hashCode(this.awaitingCondition, this.openParentheses, this.elements, this.selectedFields, this.neededTables);
		}
		
	}
	//public ResultSet select( QueryBuilder qb );
}
