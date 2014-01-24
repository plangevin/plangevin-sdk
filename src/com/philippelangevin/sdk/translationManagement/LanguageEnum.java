package com.philippelangevin.sdk.translationManagement;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: {@link LanguageEnum}
 * Description: This enum contains the languages supported by CTEC.
 * Company : C-Tec World
 * 
 * @author plefebvre
 * Copyright: (c) 2009, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * ------------------------------------------------
 * Date			Name		BT		Description
 * 2009-04-06	plefebvre			
 * 2009-06-01	ipainchaud			Added abbreviationValues() and valueOfFromAbbreviation(...)
 */

public enum LanguageEnum {
	ENGLISH,
	FRENCH;
	
	private static MessageBundle messageBundle = TranslationBundleFactory.getTranslationBundle();
	
	public String getAbbreviation() {
		return messageBundle.getString("Abbreviation" + super.toString());
	}
	
	@Override
	public String toString() {
		return messageBundle.getString("Language" + super.toString());
	}
	
	private static List<String> abbreviationList = new ArrayList<String>();
	static {
		LanguageEnum values[] = LanguageEnum.values();
		for( int i = 0; i < values.length; i++ ) {
			abbreviationList.add( values[i].getAbbreviation() );
		}
	}
	
	static public LanguageEnum valueOfFromAbbreviation( String abbreviation ) {
		if (abbreviation == null) {
			return null;
		} else {
			int index = abbreviationList.indexOf( abbreviation.toUpperCase() );
			
			if (index == -1) {
				System.err.println("LanguageEnum.valueOfFromAbbreviation() - Unknown abbreviation: " + abbreviation);
				return null;
			} else {
				return LanguageEnum.values()[index];
			}
		}
	}
	
	static public String[] abbreviationValues() {
		return abbreviationList.toArray( new String[0] );
	}
}
