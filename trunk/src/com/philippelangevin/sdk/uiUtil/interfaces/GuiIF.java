package com.philippelangevin.sdk.uiUtil.interfaces;

import com.philippelangevin.sdk.uiUtil.DialogRunningModes;


public interface GuiIF {
	public void setEventRouter(EventRouterIF eventRouter) ;
	public String getTitle() ;
	public boolean setRunningMode(DialogRunningModes runningMode) ;
}
