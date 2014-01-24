/**
 * <p> Title: {@link StringParameterUtil} </p>
 * <p> Description: Utility class that handles packing and unpacking of
 * String Parameters. Strings parameters are 1 or n value(s) with keys
 * which are passed into a String. Ex. title:Predator;actor:Arnold </p>
 * <p> Copyright: Copyright (c) 2010</p>
 * <p> Company: C-Tec </p>
 * 
 * @author Christian Gendreau <cgendreau@colmatec.com>
 */
package com.philippelangevin.sdk.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class  StringParameterUtil{
	
	private final static String TAG_SEPARATOR = ":";
	private final static String PARAM_SEPERATOR = ";";
	
	
	/**
	 * Split a String into a LinkedHashMap<String,String> with tag(s) has key(s)
	 * @param STRING s
	 * @return key/value mapping
	 */
	public static LinkedHashMap<String, String> splitMapDataInfo(String s){
		if (s == null || s.isEmpty()) {
			return null;
		}
		
		LinkedHashMap<String, String> tagData = new LinkedHashMap<String, String>();
		//Split all the parameters
		String[] stringArray = s.split(PARAM_SEPERATOR);
		String[] tmpArray = null;
		for (int i = 0; i < stringArray.length; i++) {
			//Split tag and value
			tmpArray = stringArray[i].split(TAG_SEPARATOR);
			if(tmpArray.length == 2){
				tagData.put(tmpArray[0], tmpArray[1]);
			}
		}
		return tagData;
	}
	
	/**
	 * Pack all tags and their associated data into a StringBuffer.
	 * @param tagData
	 * @param buf
	 */
	public static void packMapDataInfo(LinkedHashMap<String, String> tagData, StringBuilder buf){
		for (Entry<String, String> entry : tagData.entrySet()) {
			packMapDataInfo(entry.getKey(), entry.getValue(), buf);
		}
	}
	
	/**
	 * Pack a tag and its associated data into a StringBuffer.
	 * @param tag
	 * @param data
	 * @param buf
	 */
	private static void packMapDataInfo(String tag, String data, StringBuilder buf) {
		
		if(buf.length() > 0){
			buf.append(PARAM_SEPERATOR);
		}
		buf.append(tag);
		buf.append(TAG_SEPARATOR);
		buf.append(data);
	}
	
	/**
	 * Split a String into a ArrayList<String>
	 * @param s
	 * @return List
	 */
	public static ArrayList<String> splitArrayDataInfo(String s){
		if (s == null || s.isEmpty()) {
			return null;
		}
		
		ArrayList<String> list = new ArrayList<String>();
		String[] stringArray = s.split(PARAM_SEPERATOR, -1);
		
		for (int i = 0; i < stringArray.length; i++) {
			list.add(stringArray[i].isEmpty()? null: stringArray[i]);
		}
		return list;
	}
	
	/**
	 * Pack the Array into a StringBuffer.
	 * @param dataArray
	 * @return
	 */
	public static String packArrayDataInfo(List<?> dataArray) {
		StringBuilder dataString = new StringBuilder(100);
		
		if(dataArray == null || dataArray.size() == 0){
			return null;
		}
		
		for(Object o : dataArray) {
			if (o != null){
				if(!o.toString().contains(PARAM_SEPERATOR)){
					if(dataString.length() > 0){
						dataString.append(PARAM_SEPERATOR);
					}
					dataString.append(o.toString());
				}
				else{
					System.out.println("packDataInfo : could not pack " + o.toString());
				}
			}
		}
		
		if (dataString.length() == 0) {
			return null;
		} else {
			return dataString.toString();
		}
	}
}
