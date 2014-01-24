package com.philippelangevin.sdk.database.xml;

import com.philippelangevin.sdk.database.transferableObject.metaData.TOColumnMetaDataIF;

public class IDParentTagInfo implements XMLTagInfo {
	String m_parentTagName;
	XMLTagInfo m_greatGrandParent;
	public IDParentTagInfo( String parentTagName, XMLTagInfo greatGrandParent ) {
		m_parentTagName = parentTagName;
		m_greatGrandParent = greatGrandParent;
	}
	@Override
	public TOColumnMetaDataIF<?> getMetaData() { return null; }
	@Override
	public XMLNameSpaceEnum getNameSpace() { return XMLNameSpaceEnum.project; }
	@Override
	public XMLTagInfo getParentTagInfo() { return m_greatGrandParent; }
	@Override
	public String getTagName() { return m_parentTagName; }
	@Override
	public boolean isATagAttribute() { return false; }
}

