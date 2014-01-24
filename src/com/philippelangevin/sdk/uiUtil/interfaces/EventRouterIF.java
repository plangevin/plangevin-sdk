package com.philippelangevin.sdk.uiUtil.interfaces;

public interface EventRouterIF {
	public void setController(ControllerIF controller) ;
	public ControllerIF getController() ;
	
	public GuiIF getGUI() ;
	public void showGUI(boolean show) ;
	public void dispose() ;
}
