package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;
import java.util.Arrays;

import com.philippelangevin.sdk.dataStructure.Money;

/**
 * MetaData class for an array of Money.
 * <p><b>Warning:</b> This class has not been tested, and neither has PostgreSQLMoneyArray
 * (used by ClassicSQLRequests).
 * @author ftaillefer
 *
 */
public class ArrayMoneyMetaData implements TOColumnMetaDataIF<Money[]> {
	private boolean allowNull = false;
	
	public ArrayMoneyMetaData(boolean allowNull) {
		this.allowNull=allowNull;
	}
	
	@Override
	public Object copy(Object toCopy) {
		if(toCopy == null){return null;}
		
		if(toCopy instanceof Money[]){
			Money[] a = (Money[])toCopy;
			return Arrays.copyOf(a, a.length);
		}
		return null;
	}
	
	@Override
	public int getSQLDataType() {
		return Types.ARRAY;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "ARRAY" ;
	}
	
	@Override
	public boolean isAutoNumber() {
		return false;
	}
	
	@Override
	public Boolean isNullAllowed() {
		return this.allowNull;
	}
	
	@Override
	public boolean isText() {
		return false;
	}
	
	@Override
	public Money[] parse(Object value) {
		if(value != null) {
			if(value instanceof Money[]) {
				return (Money[])value;
			} else {
				try {
					String s = value.toString();
					String[] sTab = s.substring(1, s.length()-1).split(",");
					Money[] i = new Money[sTab.length];
					
					for (int j=0; j<sTab.length; ++j) {
						if(!sTab[j].isEmpty() && !sTab[j].equalsIgnoreCase("NULL")){
							i[j] = (Money.parseMoney(sTab[j]));
						}
					}
					return i;
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return null;
				}
			}
		} else if (allowNull) {
			return null;
		} else {
			System.err.println("ArrayMoneyMetaData.parse() - A mandatory value has been set to null!");
			Thread.dumpStack();
			return null;
		}
	}
}
