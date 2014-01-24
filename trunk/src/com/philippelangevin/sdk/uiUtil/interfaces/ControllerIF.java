package com.philippelangevin.sdk.uiUtil.interfaces;


public abstract class ControllerIF {
	protected ControllerIF parentController = null ;
	protected EventRouterIF eventRouter = null ;
	
	protected abstract EventRouterIF getEventRouter() ;
	
	public String getDialogTitle()	{	return getEventRouter().getGUI().getTitle() ;	}
	
	public void showMainGUI(boolean show)	{
		/*
		 * If we want to show it
		 * OR
		 * if we want to hide it AND eventRouter exists
		 */
		if (show == true || show == false && eventRouter != null){
			getEventRouter().showGUI(show) ;
		}
	}
	public void dispose()	{
		if (eventRouter != null){
			eventRouter.dispose() ;
		}
	}
	
	public void setParentController(ControllerIF parentController)	{
		this.parentController = parentController ;
	}

	public ControllerIF getParentController() {
		return parentController;
	}
	
	public void destroy()	{
		showMainGUI(false) ;
		dispose() ;
	}
}
