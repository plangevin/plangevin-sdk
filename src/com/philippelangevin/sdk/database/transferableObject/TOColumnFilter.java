package com.philippelangevin.sdk.database.transferableObject;

import com.philippelangevin.sdk.dataStructure.Tuple.Triple;
import com.philippelangevin.sdk.database.dbAccess.AbstractDatabaseDAO;
import com.philippelangevin.sdk.database.dbAccess.DatabaseAccessObjectIF.QueryBuilder.QueryMathOperatorStruct;
import com.philippelangevin.sdk.database.dbAccess.DatabaseAccessObjectIF.QueryBuilder.QueryMathOperatorStructIF;


/**
 * <p> Title: {@link TOColumnFilter} </p>
 * <p> Description: Represents a TO column and his value to be filtered with the corresponding operator. </p>
 * <p> In order to support the {@linkplain QueryMathOperatorStruct#in in} and {@linkplain QueryMathOperatorStruct#nin not in} operators,
 * the third parametric type of Triple is set to Object.  If those two operators want to be used, a user
 * will now be able to but an array of value of type T.  The TOColumnFilter interpretor
 * (such as {@link AbstractDatabaseDAO#selectTOList(TransferableObject, java.util.List)} or
 * {@link AbstractDatabaseDAO#selectTOList(Class, java.util.List)} must manage those type arrays
 * <p> Company : C-Tec </p>
 *
 * @author Philippe Langevin	philippelangevin@ctecworld.com
 * Copyright: (c) 2011, C-Tec Inc. - All rights reserved
 */
/*
 * History
 * ------------------------------------------------
 * Date         Name        BT      Description
 * 2011-04-18   philippelangevin
 */
public class TOColumnFilter<T> extends Triple<TransferableObjectInfo<T>, QueryMathOperatorStructIF, Object> {
	public TOColumnFilter(ColumnInfo<T> column, QueryMathOperatorStructIF operator, Object value) {
		super(column, operator, value);
	}
	
	public TransferableObjectInfo<T> getColumn() {
		return super.getFirst();
	}
	
	public QueryMathOperatorStructIF getOperator()	{
		return super.getSecond() ;
	}
	
	public Object getValue() {
		return super.getThird();
	}
}
