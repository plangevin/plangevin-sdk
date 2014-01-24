/**
 * 
 */
package com.philippelangevin.sdk.database.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import com.philippelangevin.sdk.database.transferableObject.TransferableObject;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectInfo;

/**
 * @author vgaudreault
 *
 */
public class ResultSetTranslator {
	
	/**
	 * This function is useful when you want to get more than one TO from one resultSet...
	 * BE CAREFULL though! If more than one column has the same name, the behavior of generating
	 * more than one TO might be "special" (Haven't tried this kind of exception yet!!!).
	 * @param rs
	 * @param TOClass
	 * @return The TO
	 */
	@SuppressWarnings("unchecked")
	public static <T extends TransferableObject> T getDistinctTOFromResultSetFromCurrentRow( ResultSet rs, Class<T> TOClass ){
		try {
			T TO = TOClass.newInstance();
			for(TransferableObjectInfo<?> column : TO.getTOStructure().getColumns()){
				try{
					TO.set((TransferableObjectInfo<Object>)column, rs.getObject(column.toString()));
				}catch (UnsupportedOperationException e){
					e.printStackTrace();
				} catch (Exception e) {
					/*
					 * WARNING!!!
					 * If we get here, that means that one of the column name wasn't found in
					 * the resultSet! This also means that the next time you want to retrieve
					 * the value of that column from the TO, null will be returned.
					 * We don't want to print this exception.
					 */
				}
			}
			
			for(TransferableObjectInfo<?> column : TO.getTOStructure().getAdditionalFields()){
				try{
					TO.set((TransferableObjectInfo<Object>)column, rs.getObject(column.toString()));
				}catch (UnsupportedOperationException e){
					e.printStackTrace();
				} catch (Exception e) {
					// TODO We might want to separate the additional fields between columns and
					//       other data, this method generates a lot of exceptions!
					/* Do nothing */
				}
			}
			return TO;
		}catch( Throwable t ){
			t.printStackTrace();
		}
		return null;
	}
	
	public static <T extends TransferableObject> T getDistinctTOFromResultSet( ResultSet rs, Class<T> TOClass ){
		try{
			if ( rs.next() ) {
				return getDistinctTOFromResultSetFromCurrentRow( rs, TOClass );
			}else{
				return null;
			}
		}catch(Throwable t){
			t.printStackTrace();
			return null;
		}
	}
	
	@Deprecated
	@SuppressWarnings("unchecked")
	/**
	 * Use getTOListFromRS() instead.
	 */
	public static <T extends TransferableObject> Collection<T> getTOList (ResultSet rs, Class<T> TOClass){
		
		ArrayList<T> list = new ArrayList<T>();
		ArrayList<Class<T>> TOClassList = new ArrayList<Class<T>>();
		TOClassList.add(TOClass);
		
		getTOLists(rs, TOClassList, list);
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends TransferableObject> List<T> getTOListFromRS (ResultSet rs, Class<T> TOClass) {
		List<T> list = new ArrayList<T>();
		List<Class<T>> TOClassList = new ArrayList<Class<T>>();
		TOClassList.add(TOClass);
		getTOLists(rs, TOClassList,	list);
		
		return list;
	}
	
	/**
	 * This method allow you to fill many list with specified class types. A list
	 * represent a column in the ResultSet associated with the first class.
	 * 
	 * For example, the first list contain ObservationTO and the
	 * second one ObservationTypeTO associated with the first list.
	 * @param rs The ResultSet
	 * @param TOClass The list of class to fetch. The first one is the row and the other ones a column from this row result.
	 * @param lists The lists to populate
	 */
	public static <T extends TransferableObject> void getTOLists (ResultSet rs, List<Class<T>> TOClass, List<T>... lists){
		// We must have exactly the same number of class than collections
		if(lists.length != TOClass.size() || TOClass.isEmpty()){
			throw new RuntimeException("Invalid number of class for the specified list of collections.");
		}
		
		// Fill all lists
		try {
			T TO = getDistinctTOFromResultSet(rs, TOClass.get(0));
		
			while (null != TO) {
				lists[0].add(TO);
				for (int i = 1; i < lists.length; i++) {
					lists[i].add(getDistinctTOFromResultSetFromCurrentRow(rs, TOClass.get(i)));
				}
				TO = getDistinctTOFromResultSet(rs, TOClass.get(0));
			}

		} catch (Throwable t) {
			t.printStackTrace();
		}

	}
	

	
	/** Retourne un treeMap du resultSet en prenant pour clé sa première colonne
	 * et comme valeur sa deuxième
	 * @param 	rs
	 * @param 	keyColumn
	 * @param	valueColumn
	 * @return 	TreeMap
	 */
	public static TreeMap<String, String> getRSTreeMap(ResultSet rs, String keyColumn, String valueColumn){
		
		TreeMap<String, String> map = new TreeMap<String, String>();
		
		try{
			int keyColIndex = rs.findColumn(keyColumn);
			int valueColIndex = rs.findColumn(valueColumn);
			
			while(rs.next()){
				map.put(rs.getString( keyColIndex ), rs.getString( valueColIndex ));
			}
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/** Retourne un treeMap du resultSet en prenant pour clé un ID attribué
	 * à sa valeur
	 * et comme valeur sa deuxième
	 * @param 	rs
	 * @return 	TreeMap
	 */
	public static TreeMap<Integer, String> getRSTreeMapID(ResultSet rs){
		
		TreeMap<Integer, String> map = new TreeMap<Integer, String>();
		
		try{
			while(rs.next()){
				map.put(rs.getInt(1), rs.getString(2).trim());
			}
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Retourne un <code>ArrayList</code> en prenant les valeurs de la première colonne
	 * @param rs Le <code>ResultSet</code> contenant le résultat de la requête
	 * @return Un <code>ArrayList</code> contenant les valeurs de la première colonne de la requête
	 */
	public static ArrayList<String> getRSArrayList(ResultSet rs) {
		return getRSArrayList(rs, 1);
	}
	
	/**
	 * Generic override of the method {@link #getRSArrayList(ResultSet)} which returns a String and always
	 * prompt for column 1. If the returned object is a String, it is trimmed.
	 * @param rs The result set.
	 * @param column The column number in the result set. This index is 1-based.
	 * @return The list for the specified column.
	 * @throws IllegalArgumentException If column is <= 0.
	 */
	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> getRSArrayList(ResultSet rs, int column) throws IllegalArgumentException {
		if (column <= 0) {
			throw new IllegalArgumentException("column number 0 doesn't make sense in a resul set");
		}
		ArrayList<T> list = new ArrayList<T>();
		
		try {
			while(rs.next()) {
				T s = (T)rs.getObject(column);
				
				if(s != null && s instanceof String) {
					s = (T)((String)s).trim();
				}
				list.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
			list =  null;
		}
		
		return list;
	}
	
	/**
	 * Retourne un <code>ArrayList(String)</code> en prenant les valeurs de la colonne keyColumn
	 * @param rs Le <code>ResultSet</code> contenant le résultat de la requête
	 * @param keyColumn le nom de la colonne voulue
	 * @return Un <code>ArrayList</code> contenant les valeurs de la colonne spécifié
	 */
	public static ArrayList<String> getRSColumnArrayList(ResultSet rs, String keyColumn){
		int keyColIndex = 0;
		try {
			keyColIndex = rs.findColumn(keyColumn);
		} catch (SQLException e) {
			e.printStackTrace();
			
			return null ;
		}
		
		return getRSColumnArrayList(rs, keyColIndex) ;
	}
	
	public static ArrayList<String> getRSColumnArrayList(ResultSet rs, int columnIndex){
		ArrayList<String> list = new ArrayList<String>();

		try {
			while(rs.next()) {
				String s = rs.getString(columnIndex);
				
				if(s != null) {
					list.add(s.trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			list =  null;
		}
		
		return list;
	
	}
}