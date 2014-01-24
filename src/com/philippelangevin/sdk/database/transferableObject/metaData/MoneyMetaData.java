package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;

import com.philippelangevin.sdk.dataStructure.Money;

/**
 * <p> Title: {@link MoneyMetaData} <p>
 * <p> Description: This class is use to parse an object in Money and
 *                  control the NullAllowed
 * <p> Company : C-Tec <p>
 *
 * @author Jonathan Giroux (jgiroux) jgiroux@ctecworld.com
 * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
 */
/*
 * History
 * ------------------------------------------------
 * Date        Name        BT      Description
 * 2010-09-28  jgiroux			   Initial implementation
 */
public class MoneyMetaData implements TOColumnMetaDataIF<Money> {

	private boolean allowNull = false;
	
	public MoneyMetaData(boolean allowNull){
		this.allowNull = allowNull;
	}
	
	@Override
	public Money parse(Object moneyValue) {
		if(moneyValue != null) {
			if(moneyValue instanceof Money) {
				// We ensure the Money object has a maximum of 2 decimal digits
				((Money) moneyValue).validateState();
				return (Money) moneyValue;
			}
			else{
				try{
					Money m = Money.parseMoney(moneyValue.toString());
					// We ensure the Money object has a maximum of 2 decimal digits
					m.validateState();
					return m;
				}catch(NumberFormatException e){
					e.printStackTrace();
					return null;
				}
			}
		}else if (allowNull){
			return null;
		}else{
			System.err.println("MoneyMetaData.parse() - A mandatory value has been set to null!");
			Thread.dumpStack();
			return null;
		}
	}

	@Override
	public int getSQLDataType() {
		return Types.NUMERIC;
	}
	
	@Override
	public String getSQLDeclarationString()	{
		return "NUMERIC" ;
	}

	@Override
	public Boolean isNullAllowed() {
		return this.allowNull;
	}

	@Override
	public boolean isAutoNumber() {
		return false;
	}

	@Override
	public boolean isText() {
		return false;
	}

	@Override
	public Object copy(Object toCopy) {
		return toCopy;
	}
}
