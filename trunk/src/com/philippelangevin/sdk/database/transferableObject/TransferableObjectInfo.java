package com.philippelangevin.sdk.database.transferableObject;

import com.philippelangevin.sdk.database.transferableObject.metaData.TOColumnMetaDataIF;


/**
 * This interface is a sort of a tag for TransferableObject's information. It is used to contain both
 * ColumnNames and AdditionalFields.
 */
public interface TransferableObjectInfo<T> {
	public TOColumnMetaDataIF<T> getMetaData();
}
