package com.philippelangevin.sdk.util;

public class ObjectNameContainer<T> {
	private String name ;
	private T object = null ;
	
	public ObjectNameContainer(String name, T object)	{
		this.name = name ;
		this.object = object ;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public T getObject()	{
		return object ;
	}
}
