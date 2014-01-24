package com.philippelangevin.sdk.database.transferableObject;

import com.philippelangevin.sdk.dataStructure.Tuple.Pair;

/**
 * <p> Title: {@link TOColumnPair} <p>
 * <p> Description: Represents a TO column and a value. </p>
 * <p> Company : C-Tec <p>
 *
 * @author plefebvre
 * Copyright: (c) 2011, C-Tec Inc. - All rights reserved
 */
public class TOColumnPair<T> extends Pair<TransferableObjectInfo<T>, T> {
	public TOColumnPair(ColumnInfo<T> column, T value) {
		super(column, value);
	}
	
	public TransferableObjectInfo<T> getColumn() {
		return super.getFirst();
	}
	
	public T getValue() {
		return super.getSecond();
	}
	
}
