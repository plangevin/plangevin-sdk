package com.philippelangevin.sdk.time;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import com.philippelangevin.sdk.translationManagement.MessageBundle;
import com.philippelangevin.sdk.translationManagement.TranslationBundleFactory;

/**
  * <p> Title: {@link WeekDaysEnum} <p>
  * <p> Description: Enum representing the days of the week. </p>
  * <p> Company : C-Tec <p>
  * 
  * @author plefebvre
  * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
  */

 /*
  * History
  * ------------------------------------------------
  * Date			Name		BT		Description
  * 2010-06-10		plefebvre
  */

public enum WeekDaysEnum {
	Sunday(Calendar.SUNDAY),
	Monday(Calendar.MONDAY),
	Tuesday(Calendar.TUESDAY),
	Wednesday(Calendar.WEDNESDAY),
	Thursday(Calendar.THURSDAY),
	Friday(Calendar.FRIDAY),
	Saturday(Calendar.SATURDAY),
	;
	
	private static final MessageBundle messageBundle = TranslationBundleFactory.getTranslationBundle();
	private Integer ID = null;
	private static Map<Integer, WeekDaysEnum> daysMap = null;
	
	private WeekDaysEnum(Integer id) {
		this.ID = id;
	}
	
	private Integer getID() {
		return ID;
	}
	
	public static Integer getIDFromEnum(WeekDaysEnum dayEnum) {
		if (dayEnum == null) {
			return null;
		} else {
			return dayEnum.getID();
		}
	}
	
	public static WeekDaysEnum getEnumFromID(Integer ID) {
		if (ID == null) {
			return null;
		} else {
			if (daysMap == null) {
				daysMap = new TreeMap<Integer, WeekDaysEnum>();
				for (WeekDaysEnum type: WeekDaysEnum.values()) {
					daysMap.put(type.getID(), type);
				}
			}
			
			return daysMap.get(ID);
		}
	}
	
	/**
	 * toString() returns the enum string itself, you might want getTranslation().
	 */
	@Override
	public String toString() {
		return super.toString();
	}
	
	public String getTranslation() {
		return messageBundle.getString("WeekDaysEnum." + super.toString());
	}
	
	public String getLowerTranslation() {
		return messageBundle.getString("WeekDaysEnum." + super.toString()).toLowerCase();
	}
}
