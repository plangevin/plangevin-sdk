package com.philippelangevin.sdk.addressBook.tos;

import com.philippelangevin.sdk.database.tables.TableInfo;
import com.philippelangevin.sdk.database.transferableObject.AdditionalFieldInfo;
import com.philippelangevin.sdk.database.transferableObject.ColumnInfo;
import com.philippelangevin.sdk.database.transferableObject.TransferableObject;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectInfoContainer;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectStructureIF;
import com.philippelangevin.sdk.database.transferableObject.metaData.IntegerMetaData;
import com.philippelangevin.sdk.database.transferableObject.metaData.StringMetaData;

public class ContactTypeTOStructure implements TransferableObjectStructureIF {

	private static final long serialVersionUID = 4569549358444479211L;
	
	public static final class ColumnNames extends TransferableObjectInfoContainer {
		public static final ColumnInfo<Integer> id = createColumn(new IntegerMetaData( false, false ));
		public static final ColumnInfo<String> nameKey = createColumn(new StringMetaData( false, 50 ));
		
		private static final ColumnNames INSTANCE = new ColumnNames();
	}

	@Override
	public TableInfo getRepresentedTable() {
		return Tables.CONTACTTYPE ;
	}

	@Override
	public Class<? extends TransferableObject> getRepresentedTOClass() {
		return getRepresentedTable().getTransferableObjectClass() ;
	}

	@Override
	public Integer getPrimaryKeySize() {
		return 1 ;
	}

	@Override
	public ColumnInfo<?>[] getColumns() {
		return ColumnNames.INSTANCE.getColumns();
	}

	@Override
	public AdditionalFieldInfo<?>[] getAdditionalFields() {
		return EMPTY_ADDITIONAL_FIELDS;
	}

}
