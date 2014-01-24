/**
 * 
 */
package com.philippelangevin.sdk.database.transferableObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import com.philippelangevin.sdk.database.transferableObject.metaData.TOColumnMetaDataIF;
import com.philippelangevin.sdk.util.StringUtil;

/**
 * @author pcharette
 * @date 2010-12-30
 */
public abstract class TransferableObjectInfoContainer {
	
	private static final int PUBLIC_STATIC_FINAL_MODIFIERS = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;
	
	private static class TransferableObjectInfoImpl<T> implements TransferableObjectInfo<T>, Comparable<TransferableObjectInfoImpl<?>> {
		private String name;
		private TOColumnMetaDataIF<T> metaData;
		
		public TransferableObjectInfoImpl(TOColumnMetaDataIF<T> metaData) {
			this.metaData = metaData;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return name;
		}
		
		/* (non-Javadoc)
		 * @see ctec.sdk.database.transferableObject.TransferableObjectInfo#getMetaData()
		 */
		@Override
		public TOColumnMetaDataIF<T> getMetaData() {
			return metaData;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return name == null ? 0 : name.hashCode();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof TransferableObjectInfoImpl<?>){
				TransferableObjectInfoImpl<?> transferableObjectInfoImpl = (TransferableObjectInfoImpl<?>) obj; 
				return name.equals(transferableObjectInfoImpl.name) && metaData.equals(transferableObjectInfoImpl.metaData);
			}
			return super.equals(obj);
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(TransferableObjectInfoImpl<?> o) {
			return StringUtil.compare(name, o.name);
		}
	}
	
	private static final class ColumnInfoImpl<T> extends TransferableObjectInfoImpl<T> implements ColumnInfo<T> {
		public ColumnInfoImpl(TOColumnMetaDataIF<T> metaData) {
			super(metaData);
		}
	}
	
	private static final class AdditionalFieldInfoImpl<T> extends TransferableObjectInfoImpl<T> implements AdditionalFieldInfo<T> {
		public AdditionalFieldInfoImpl(TOColumnMetaDataIF<T> metaData) {
			super(metaData);
		}
	}
	
	public static <T> ColumnInfo<T> createColumn(TOColumnMetaDataIF<T> metaData) {
		return new ColumnInfoImpl<T>(metaData);
	}
	
	public static <T> AdditionalFieldInfo<T> createAdditionalField(TOColumnMetaDataIF<T> metaData) {
		return new AdditionalFieldInfoImpl<T>(metaData);
	}
	
	private ColumnInfo<?>[] columns;
	private AdditionalFieldInfo<?>[] AdditionalFields;
	
	public ColumnInfo<?>[] getColumns() {
		return columns;
	}
	
	public AdditionalFieldInfo<?>[] getAdditionalFields() {
		return AdditionalFields;
	}
	
	protected TransferableObjectInfoContainer() {
		ArrayList<ColumnInfo<?>> columns = new ArrayList<ColumnInfo<?>>();
		ArrayList<AdditionalFieldInfo<?>> additionals = new ArrayList<AdditionalFieldInfo<?>>();
		try {
			for (Field f : getClass().getFields()) {
				try {
					if ((f.getModifiers() & PUBLIC_STATIC_FINAL_MODIFIERS) == PUBLIC_STATIC_FINAL_MODIFIERS && TransferableObjectInfo.class.isAssignableFrom(f.getType())) {
						TransferableObjectInfo<?> ci = (TransferableObjectInfo<?>)f.get(this);
						if (ci instanceof TransferableObjectInfoImpl<?>) {
							((TransferableObjectInfoImpl<?>)ci).name = f.getName();
							if (ci instanceof ColumnInfoImpl<?>) {
								columns.add((ColumnInfo<?>) ci);
							} else { //AdditionalFieldInfoImpl
								additionals.add((AdditionalFieldInfo<?>) ci);
							}
						} else {
							System.err.println(getClass()+"."+f.getName()+" was not created with TransferableObjectInfoContainer.create*() and thus, its name cannot be set");
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		this.columns = columns.toArray(new ColumnInfo<?>[columns.size()]);
		this.AdditionalFields = additionals.toArray(new AdditionalFieldInfo<?>[additionals.size()]);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (columns.length != 0) {
			return Arrays.toString(columns);
		}
		return Arrays.toString(AdditionalFields);
	}
}
