package com.philippelangevin.sdk.database.util;

import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.philippelangevin.sdk.dataStructure.Money;
import com.philippelangevin.sdk.database.transferableObject.metaData.DurationMetaData;

public class ClassicSQLRequests {

	private final static Object dbSyncObj = new Object();

	public static void setPreparedStatement( PreparedStatement ps, int index, Object o, int type ) throws SQLException {
	
		synchronized( dbSyncObj ) {
			if( null == o ) {
				ps.setNull(index, type);
			} else if (o instanceof Integer[]) {
				//TODO Find a cleaner way to do this, and test with MS SQL Server.
				Integer[] array = (Integer[]) o;
				ps.setObject(index, new PostgreSQLInt4Array( array == null || array.length == 0? null: array), type);
			} else if (o instanceof String[]){
				//TODO Same
				String[] array = (String[]) o;
				PostgreSQLTextArray temp = new PostgreSQLTextArray( array == null || array.length == 0? null: array );
				ps.setObject(index, temp, type);
			} else if (o instanceof Money[]){
				//TODO Same
				Money[] array = (Money[]) o;
				PostgreSQLMoneyArray temp = new PostgreSQLMoneyArray( array == null || array.length == 0? null: array );
				ps.setObject(index, temp, type);
			}  else if (o instanceof Long[]){
				//TODO Same
				Long[] array = (Long[]) o;
				PostgreSQLLongArray temp = new PostgreSQLLongArray( array == null || array.length == 0? null: array );
				ps.setObject(index, temp, type);
			} else if (o instanceof List<?>){
				
				//If the list is empty, the value that represents it is null regardless of inner type
				List<?> oList = ((List<?>)o);
				if (oList.size() <= 0){
					ps.setNull(index, type);
				} else {
					
					/* If the list isn't empty, we transform it into an array that matches the first element
					 * and then we re-call this method recursively so that the correct array type is handled. */
					Object firstElement = oList.get(0);
					setPreparedStatement(ps, index, oList.toArray((Object[])Array.newInstance(firstElement.getClass(), 0)), type);
				}
			} else if (o instanceof Duration){
				//TODO remove this patch by creating a format() method in the TOColumnMetaDataIF and use it in AbstractDatabaseDAO for formatting object instead of using toString()
				ps.setLong(index, DurationMetaData.format((Duration)o));
			} else if (o instanceof LocalTime){
				//TODO remove this patch by creating a format() method in the TOColumnMetaDataIF and use it in AbstractDatabaseDAO for formatting object instead of using toString()
				Calendar cal = Calendar.getInstance();
				cal.clear();
				//weird patch for the time that shift the value with the current time zone
				cal.setTimeInMillis(((LocalTime)o).getMillisOfDay()-cal.get(Calendar.ZONE_OFFSET));
				ps.setTime(index, new java.sql.Time(cal.getTimeInMillis()));
			} else if (o instanceof LocalDate){
				//TODO remove this patch by creating a format() method in the TOColumnMetaDataIF and use it in AbstractDatabaseDAO for formatting object instead of using toString()
				ps.setDate(index, new java.sql.Date(((LocalDate)o).toDateMidnight().getMillis()));
			} else if (o instanceof DateTime){
				//TODO remove this patch by creating a format() method in the TOColumnMetaDataIF and use it in AbstractDatabaseDAO for formatting object instead of using toString()
				ps.setDate(index, new java.sql.Date(((DateTime)o).toDate().getTime()));
			} else {
				ps.setObject(index, o, type);
			}
		}
	}

	static public void setPreparedStatement( PreparedStatement ps, int index, Object o ) throws SQLException {
		int type;
		
		if( null == o ) { // when the object is null
			type = Types.VARCHAR; // any type will do!!! Check setPreparedStatement( ps, index, o, type );
		} else if( o instanceof String ) {
			type = Types.VARCHAR;
		} else if( o instanceof Integer ) {
			type = Types.INTEGER;
		} else if( o instanceof Long ) {
			type = Types.BIGINT;
		} else if( o instanceof Short ) {
			type = Types.SMALLINT;
		} else if( o instanceof Byte ) {
			type = Types.TINYINT;
		} else if( o instanceof Float ) {
			type = Types.FLOAT;
		} else if( o instanceof Double ) {
			type = Types.DOUBLE;
		} else if( o instanceof Boolean ) {
			type = Types.BIT;
		} else if( o instanceof Timestamp ) {
			type = Types.TIMESTAMP;
		} else if(o instanceof Character){
			type = Types.CHAR;
		}else{
			throw new AssertionError( "Unhandled object type " + o.getClass().getCanonicalName() );

		}
		setPreparedStatement( ps, index, o, type );
	}
}