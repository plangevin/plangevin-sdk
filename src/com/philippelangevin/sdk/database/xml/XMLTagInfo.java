package com.philippelangevin.sdk.database.xml;

import com.philippelangevin.sdk.database.transferableObject.TransferableObjectInfo;

@SuppressWarnings("rawtypes")
public interface XMLTagInfo extends TransferableObjectInfo {
	public String getTagName();
	public boolean isATagAttribute();
	public XMLTagInfo getParentTagInfo();
	public XMLNameSpaceEnum getNameSpace();
}
