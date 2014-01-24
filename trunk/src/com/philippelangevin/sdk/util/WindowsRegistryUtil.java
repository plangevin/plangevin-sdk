package com.philippelangevin.sdk.util;

import java.lang.reflect.Method;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * <p> Title: {@link WindowsRegistryUtil} <p>
 * <p> Description: This class reads data from the Windows registry. </p>
 * <p> Company : C-Tec <p>
 *
 * @author plefebvre
 * Copyright: (c) 2010, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * ------------------------------------------------
 * Date			Name		BT		Description
 * 2010-11-17	plefebvre
 */

// From: http://www.davidc.net/programming/java/reading-windows-registry-java-without-jni
// If we ever need to write, see also: http://www.rgagnon.com/javadetails/java-0630.html
public class WindowsRegistryUtil {
	public static enum REGISTRY_HIVE {
		HKEY_CURRENT_USER {
			@Override
			public int getInt() {
				return 0x80000001;
			}
		},
		HKEY_LOCAL_MACHINE {
			@Override
			public int getInt() {
				return 0x80000002;
			}
		},
		;
		
		abstract public int getInt();
	}
	
	/* Windows security masks */
	private static final int KEY_READ = 0x20019;
	
	/* Constants used to interpret returns of native functions */
	private static final int NATIVE_HANDLE = 0;
	private static final int ERROR_CODE = 1;
	
	/* Windows error codes. */
	private static final int ERROR_SUCCESS = 0;
	private static final int ERROR_FILE_NOT_FOUND = 2;
	
	@SuppressWarnings("unchecked")
	public static String getKeySz(REGISTRY_HIVE hive, String keyName, String valueName) throws BackingStoreException {
		@SuppressWarnings("rawtypes")
		final Class clazz = Preferences.userRoot().getClass();
		
		try {
			final Method openKeyMethod = clazz.getDeclaredMethod("WindowsRegOpenKey", int.class, byte[].class, int.class);
			openKeyMethod.setAccessible(true);
			
			final Method closeKeyMethod = clazz.getDeclaredMethod("WindowsRegCloseKey", int.class);
			closeKeyMethod.setAccessible(true);
			
			final Method queryValueMethod = clazz.getDeclaredMethod("WindowsRegQueryValueEx", int.class, byte[].class);
			queryValueMethod.setAccessible(true);
			
			int[] result = (int[]) openKeyMethod.invoke(null, hive.getInt(), stringToByteArray(keyName), KEY_READ);
			if (result[ERROR_CODE] != ERROR_SUCCESS) {
				if (result[ERROR_CODE] == ERROR_FILE_NOT_FOUND) {
					throw new BackingStoreException("Not Found error opening key " + keyName);
				} else {
					throw new BackingStoreException("Error " + result[ERROR_CODE] + " opening key " + keyName);
				}
			}
			
			int hKey = result[NATIVE_HANDLE];
			
			byte[] b = (byte[]) queryValueMethod.invoke(null, hKey, stringToByteArray(valueName));
			closeKeyMethod.invoke(null, hKey);
			
			if (b == null) {
				return null;
			} else {
				return byteArrayToString(b);
			}
		} catch (Throwable t) {
			throw new BackingStoreException(t);
		}
	}
	
	/**
	 * Returns this java string as a null-terminated byte array
	 * 
	 * @param str The string to convert
	 * @return The resulting null-terminated byte array
	 */
	private static byte[] stringToByteArray(String str) {
		byte[] result = new byte[str.length() + 1];
		for (int i = 0; i < str.length(); i++) {
			result[i] = (byte) str.charAt(i);
		}
		result[str.length()] = 0;
		return result;
	}
	
	/**
	 * Converts a null-terminated byte array to java string
	 * 
	 * @param array The null-terminated byte array to convert
	 * @return The resulting string
	 */
	private static String byteArrayToString(byte[] array) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < array.length - 1; i++) {
			result.append((char) array[i]);
		}
		return result.toString();
	}
	
	/**
	 * Returns the requested key.
	 * @param hive The registry hive to use.
	 * @param keyName The path leading to the requested value (ending with a backslash).
	 * @param valueName The name of the value to read.
	 * @return
	 * @throws BackingStoreException
	 */
	public static String getKey(REGISTRY_HIVE hive, String keyName, String valueName) throws BackingStoreException {
		return getKeySz(hive, keyName, valueName);
	}
	
	/**
	 * Returns the requested key without throwing an exception (null returned if a problem occurs).
	 * @param hive The registry hive to use.
	 * @param keyName The path leading to the requested value (ending with a backslash).
	 * @param valueName The name of the value to read.
	 * @return
	 */
	public static String getKeySilent(REGISTRY_HIVE hive, String keyName, String valueName) {
		try {
			return getKeySz(hive, keyName, valueName);
		} catch (BackingStoreException e) {
			return null;
		}
	}
	
	/**
	 * Convenient method to return the registry's local appdata path setting.
	 * If a problem occurs, null will be returned.
	 * @return
	 */
	public static String getLocalAppDataKeySilent() {
		return getKeySilent(REGISTRY_HIVE.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\\", "Local AppData");
	}
	
	/**
	 * Convenient method to return the registry's roaming appdata path setting.
	 * If a problem occurs, null will be returned.
	 * @return
	 */
	public static String getRoamingAppDataKeySilent() {
		return getKeySilent(REGISTRY_HIVE.HKEY_CURRENT_USER, "Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\\", "AppData");
	}
}
