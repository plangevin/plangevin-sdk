package com.philippelangevin.sdk.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Title: {@link ClassHacker}
 * Description: This class allows adding a path to the classpath, and to print
 * 				objects using reflection.
 * Company : C-Tec World
 * 
 * @author plefebvre, adapted from http://forums.sun.com/thread.jspa?threadID=300557
 * Copyright: (c) 2009, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * ------------------------------------------------
 * Date			Name		BT		Description
 * 2009-04-09	plefebvre			
 * 2010-03-26	plefebvre			Renamed class, added printObjectFields()
 */

public class ClassHacker {
	private static final Class<?>[] parameters = new Class[]{URL.class};
	
	public static void addClassPathFile(String s) throws IOException {
		File f = new File(s);
		addClassPathFile(f);
	}
	
	public static void addClassPathFile(File f) throws IOException {
		addClassPathURI(f.toURI());
	}
	
	public static void addClassPathURI(URI uri) throws IOException {
		addClassPathURL(uri.toURL());
	}
	
	public static void addClassPathURL(URL url) throws IOException {
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;
		
		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[]{ url });
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader.");
		}
	}
	
	/**
	 * Prints all fields and values for a specified object.
	 * All arrays and iterable objects will be recursively printed.
	 * @param o
	 */
	public static void printObjectFields(Object o) {
		if (o == null) {
			System.out.println("null");
		} else {
			Class<?> oClass = o.getClass();
			System.out.println("Printing all fields for an instance of " + oClass.getCanonicalName() + ":");
			System.out.println("---------------");
			
			System.out.print("toString(): ");
			if (oClass.getComponentType() != null) {
				System.out.println("(recursively printing array content)");
				Object[] array = (Object[]) o;
				for (Object element: array) {
					printObjectFields(element);
				}
			} else if (o instanceof Iterable<?>) {
				System.out.println("(recursively printing iterable content)");
				Iterable<?> iterable = (Iterable<?>) o;
				for (Object element: iterable) {
					printObjectFields(element);
				}
				
			} else {
				System.out.println(o.toString());
				System.out.println("---------------");
				
				Field fields[] = oClass.getDeclaredFields();
				for (int i = 0; i < fields.length; i++){ 
					System.out.print(fields[i].getName() + "=");
					fields[i].setAccessible(true);
					try {
						Object value = fields[i].get(o);
						
						if (value == null) {
							System.out.println("(null)");
						} else {
							if (fields[i].getDeclaringClass().getComponentType() != null) {
								System.out.println("(recursively printing array content)");
								Object[] array = (Object[]) value;
								for (Object element: array) {
									printObjectFields(element);
								}
							} else if (o instanceof Iterable<?>) {
								System.out.println("(recursively printing iterable content)");
								Iterable<?> iterable = (Iterable<?>) value;
								for (Object element: iterable) {
									printObjectFields(element);
								}
							} else {
								System.out.println(value);
							}
						}
					} catch (Exception e) {
						System.out.println("(failed to access the field)");
					} 
				}
			}
			
			System.out.println("---------------");
		}
	}
}
