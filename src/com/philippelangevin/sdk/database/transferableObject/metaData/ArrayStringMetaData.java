package com.philippelangevin.sdk.database.transferableObject.metaData;

import java.sql.Types;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ArrayStringMetaData implements TOColumnMetaDataIF<String[]> {
	private boolean allowNull = false;
	
	public ArrayStringMetaData(boolean allowNull) {
		this.allowNull=allowNull;
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
		return true;
	}

	@Override
	public String[] parse(Object value) {
		if(value != null) {
			if(value instanceof String[]) {
				return (String[]) value;
			} else {
				String s = value.toString();
				
				//Split by any commas that are not inside quotations
				Pattern pattern = Pattern.compile("(,)(?=(?:[^\"]|\"[^\"]*\")*$)");
				String[] sTab = pattern.split(s.substring(1, s.length()-1));
				
				//Iterate through each string to fix any storage artifacts.
				for (int i = 0; i < sTab.length; i++){
					String param = sTab[i];
					
					//Strip quotations from the string, if applicable
					if (param.charAt(0) == '"' && param.charAt(param.length()-1) == '"'){
						param = param.substring(1, param.length()-1);
					}
					
					/* Strip escape backslashes.
					 * This regex matches any backslash that is not preceded or followed by another backslash).
					 * This means escaped backslashes aren't fixed yet.
					 */
					param = param.replaceAll("(?<!\\\\)\\\\(?!\\\\)", "");
					
					/* Replace double backslashes by single ones. String.replace doesn't work properly.
					 * I think it returned 3 matches for a series of 4 backslashes and wound up crashing. */
					boolean found = false;
					for (int j = 0; j < param.length(); j++){
						if (param.charAt(j) == '\\'){
							if (found){
								param = param.substring(0,j) + (j+1 >= param.length() ? "" : param.subSequence(j+1, param.length()));
								found = false;
								j--;
							} else {
								found = true;
							}
						}
					}
					
					
					sTab[i] = param;
				}

				return sTab;
			}
		} else if (allowNull) {
			return null;
		} else {
			System.err.println("ArrayStringMetaData.parse() - A mandatory value has been set to null!");
			Thread.dumpStack();
			return null;
		}
	}
	
	@Override
	public Object copy(Object toCopy) {
		if(toCopy == null){return null;}
		
		if(toCopy instanceof String[]){
			String[] a = (String[])toCopy;
			return Arrays.copyOf(a, a.length);
		}
		return null;
	}

}
