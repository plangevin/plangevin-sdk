package com.philippelangevin.sdk.uiUtil;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

public class CustomJTable extends JTable {

	private static final long serialVersionUID = 3481860966808743144L;
	
    public CustomJTable() {
		super() ;

		init() ;
	}
    
    public CustomJTable(AbstractTableModel model)	{
    	super(model) ;
    	
    	init() ;
    }
    
    private void init()	{
		setFillsViewportHeight(true) ;
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION) ;
    }

	@Override
    public String getToolTipText(MouseEvent event) { 
        String tip = null; 
        Point p = event.getPoint(); 
 
        // Locate the renderer under the event location 
        int hitColumnIndex = columnAtPoint(p); 
        int hitRowIndex = rowAtPoint(p); 
 
        if ((hitColumnIndex != -1) && (hitRowIndex != -1)) { 
            TableCellRenderer renderer = getCellRenderer(hitRowIndex, hitColumnIndex); 
            Component component = prepareRenderer(renderer, hitRowIndex, hitColumnIndex); 
 
            // Now have to see if the component is a JComponent before 
            // getting the tip 
            if (component instanceof JComponent) { 
                // Convert the event to the renderer's coordinate system 
                Rectangle cellRect = getCellRect(hitRowIndex, hitColumnIndex, false); 
                if(cellRect.width>=component.getPreferredSize().width) 
                    return null; 
                p.translate(-cellRect.x, -cellRect.y); 
                MouseEvent newEvent = new MouseEvent(component, event.getID(), 
                                          event.getWhen(), event.getModifiers(), 
                                          p.x, p.y, event.getClickCount(), 
                                          event.isPopupTrigger()); 
 
                tip = ((JComponent)component).getToolTipText(newEvent); 
            } 
        } 
 
        // No tip from the renderer, see whether any tooltip is set on JTable 
        if (tip == null)	{
            tip = getToolTipText();
        }
 
        // calculate tooltip from cell value 
        if(tip==null && hitRowIndex >= 0 && hitColumnIndex >= 0){ 
            Object value = getValueAt(hitRowIndex, hitColumnIndex); 
            tip = convertValueToText(value, hitRowIndex, hitColumnIndex); 
            if(tip.length()==0) 
                tip = null; // don't show empty tooltips 
        } 
 
        return tip; 
    } 
 
    // makes the tooltip's location to match table cell location 
    // also avoids showing empty tooltips 
    @Override
    public Point getToolTipLocation(MouseEvent event){ 
        int row = rowAtPoint( event.getPoint() ); 
        if(row==-1) 
            return null; 
        int col = columnAtPoint( event.getPoint() ); 
        if(col==-1) 
            return null; 
 
        // to avoid empty tooltips - return null location 
        boolean hasTooltip = getToolTipText()==null 
                ? getToolTipText(event)!=null 
                : true; 
 
        return hasTooltip 
                ? getCellRect(row, col, false).getLocation() 
                : null; 
    } 
    
    public String convertValueToText(Object value, int row, int column){ 
        if(value != null) { 
            String sValue = value.toString(); 
            if (sValue != null) 
                return sValue; 
        } 
        return ""; 
    } 
}
