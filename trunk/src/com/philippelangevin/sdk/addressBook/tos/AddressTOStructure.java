package com.philippelangevin.sdk.addressBook.tos;

import com.philippelangevin.sdk.database.tables.TableInfo;
import com.philippelangevin.sdk.database.transferableObject.AdditionalFieldInfo;
import com.philippelangevin.sdk.database.transferableObject.ColumnInfo;
import com.philippelangevin.sdk.database.transferableObject.TransferableObject;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectInfoContainer;
import com.philippelangevin.sdk.database.transferableObject.TransferableObjectStructureIF;
import com.philippelangevin.sdk.database.transferableObject.metaData.IntegerMetaData;
import com.philippelangevin.sdk.database.transferableObject.metaData.StringMetaData;

public class AddressTOStructure implements TransferableObjectStructureIF {

	private static final long serialVersionUID = -4804154593557739003L;
	
	public static final class ColumnNames extends TransferableObjectInfoContainer {
		public static final ColumnInfo<Integer> contactId = createColumn(new IntegerMetaData( false, false ));
		public static final ColumnInfo<Integer> id = createColumn(new IntegerMetaData( false, false ));
		public static final ColumnInfo<Integer> civicNumber = createColumn(new IntegerMetaData(true));
		public static final ColumnInfo<String> street = createColumn(new StringMetaData(true,150));
		public static final ColumnInfo<String> city = createColumn(new StringMetaData(true,150));
		public static final ColumnInfo<String> zipCode = createColumn(new StringMetaData(true,7));
		public static final ColumnInfo<Integer> contactCategory = createColumn(new IntegerMetaData(false, false)) ;
		
		private static final ColumnNames INSTANCE = new ColumnNames();
	}


	@Override
	public TableInfo getRepresentedTable() {
		return Tables.ADDRESS ;
	}

	@Override
	public Class<? extends TransferableObject> getRepresentedTOClass() {
		return getRepresentedTable().getTransferableObjectClass() ;
	}

	@Override
	public Integer getPrimaryKeySize() {
		return 2 ;
	}

	@Override
	public ColumnInfo<?>[] getColumns() {
		return ColumnNames.INSTANCE.getColumns() ;
	}

	@Override
	public AdditionalFieldInfo<?>[] getAdditionalFields() {
		return EMPTY_ADDITIONAL_FIELDS ;
	}

}
