/* ====================================================================
 * Copyright (c) 2001-2003 OYOAHA. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The names "OYOAHA" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. 
 *    For written permission, please contact email@oyoaha.com.
 *
 * 3. Products derived from this software may not be called "OYOAHA",
 *    nor may "OYOAHA" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OYOAHA OR ITS CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oyoaha.swing.plaf.oyoaha.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaTableUI extends BasicTableUI
{
  public static ComponentUI createUI(JComponent c)
  {
    return new OyoahaTableUI();
  }

  public void update(Graphics g, JComponent c)
  {
      OyoahaUtilities.paintBackground(g,c);
      paint(g,c);
  }
  
  public void paint(Graphics g, JComponent c) 
  { 
	if (table.getRowCount() <= 0 || table.getColumnCount() <= 0) 
    { 
	    return; 
	}
    
	Rectangle clip = g.getClipBounds(); 
	Point minLocation = clip.getLocation(); 
	Point maxLocation = new Point(clip.x + clip.width - 1, clip.y + clip.height - 1); 
    int rMin = table.rowAtPoint(minLocation);
    int rMax = table.rowAtPoint(maxLocation);
    
    // This should never happen.
    if (rMin == -1) 
    {
	    rMin = 0;
    }
    
    // If the table does not have enough rows to fill the view we'll get -1.
    // Replace this with the index of the last row.
    if (rMax == -1) 
    {
	    rMax = table.getRowCount()-1;
    }
    
	int cMin = table.columnAtPoint(minLocation); 
    int cMax = table.columnAtPoint(maxLocation);
    
    //This should never happen.
    if (cMin == -1) 
    {
	    cMin = 0;
    }        
	
    // If the table does not have enough columns to fill the view we'll get -1.
    // Replace this with the index of the last column.
    if (cMax == -1) 
    {
	    cMax = table.getColumnCount()-1;
    }

    // Paint the grid.
    paintGrid(g, rMin, rMax, cMin, cMax);

    // Paint the cells. 
	paintCells(g, rMin, rMax, cMin, cMax);
 }
    
 private void paintGrid(Graphics g, int rMin, int rMax, int cMin, int cMax) 
 {
    if(table.isEnabled())
    g.setColor(table.getGridColor());
    else
    g.setColor(OyoahaUtilities.getColorFromScheme(OyoahaUtilities.GRAY));    

	Rectangle minCell = table.getCellRect(rMin, cMin, true); 
	Rectangle maxCell = table.getCellRect(rMax, cMax, true); 

    if (table.getShowHorizontalLines()) 
    {
	    int tableWidth = maxCell.x + maxCell.width;
	    int y = minCell.y; 
	
        for (int row = rMin; row <= rMax; row++) 
        { 
		    y += table.getRowHeight(row);
		    g.drawLine(0, y - 1, tableWidth - 1, y - 1);
	    }     
	}
    
    if (table.getShowVerticalLines()) 
    {
	    TableColumnModel cm = table.getColumnModel(); 
	    int tableHeight = maxCell.y + maxCell.height; 
	    int x = minCell.x;
    
	    for (int column = cMin; column <= cMax ; column++) 
        {
		    x += cm.getColumn(column).getWidth(); 
		    g.drawLine(x - 1, 0, x - 1, tableHeight - 1);
	    }
    }
 }

 private int viewIndexForColumn(TableColumn aColumn) 
 {
    TableColumnModel cm = table.getColumnModel();
    
    for (int column = 0; column < cm.getColumnCount(); column++) 
    {
        if (cm.getColumn(column) == aColumn) 
        {
            return column;
        }
    }
    
    return -1;
 }

 private void paintCells(Graphics g, int rMin, int rMax, int cMin, int cMax) 
 {
	JTableHeader header = table.getTableHeader(); 
	TableColumn draggedColumn = (header == null) ? null : header.getDraggedColumn(); 

	TableColumnModel cm = table.getColumnModel(); 
	int columnMargin = cm.getColumnMargin(); 

	for(int row = rMin; row <= rMax; row++) 
    { 
	    Rectangle cellRect = table.getCellRect(row, cMin, false); 
	    
        for(int column = cMin; column <= cMax ; column++) 
        { 
		    TableColumn aColumn = cm.getColumn(column); 
		    int columnWidth = aColumn.getWidth(); 
		    cellRect.width = columnWidth - columnMargin;
	
            if (aColumn != draggedColumn) 
            {
		        paintCell(g, cellRect, row, column);
		    } 
	
            cellRect.x += columnWidth; 
	    } 
	}

    // Paint the dragged column if we are dragging. 
    if (draggedColumn != null) 
    { 
	    paintDraggedArea(g, rMin, rMax, draggedColumn, header.getDraggedDistance()); 
	}

	// Remove any renderers that may be left in the rendererPane. 
	rendererPane.removeAll(); 
 }

 private void paintDraggedArea(Graphics g, int rMin, int rMax, TableColumn draggedColumn, int distance) 
 {
    int draggedColumnIndex = viewIndexForColumn(draggedColumn); 
    
    Rectangle minCell = table.getCellRect(rMin, draggedColumnIndex, true); 
	Rectangle maxCell = table.getCellRect(rMax, draggedColumnIndex, true); 
	
	Rectangle vacatedColumnRect = minCell.union(maxCell); 

	// Paint a gray well in place of the moving column.
    OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject(table);
    Color color = table.getBackground();
    int state = OyoahaUtilities.getStatus(table);

    if(o!=null && color instanceof UIResource)
    {
        o.paintBackground(g, table, vacatedColumnRect.x, vacatedColumnRect.y, vacatedColumnRect.width-1, vacatedColumnRect.height, state);
    }
    else
    {
        OyoahaUtilities.paintColorBackground(g, table, vacatedColumnRect.x, vacatedColumnRect.y, vacatedColumnRect.width-1, vacatedColumnRect.height, color, state);
    }

	// Move to the where the cell has been dragged. 
	vacatedColumnRect.x += distance;

	// Fill the background.
    if(o!=null && color instanceof UIResource)
    {
        o.paintBackground(g, table, vacatedColumnRect.x, vacatedColumnRect.y, vacatedColumnRect.width-1, vacatedColumnRect.height, state);
    }
    else
    {
        OyoahaUtilities.paintColorBackground(g, table, vacatedColumnRect.x, vacatedColumnRect.y, vacatedColumnRect.width-1, vacatedColumnRect.height, color, state);
    }
 
	// Paint the vertical grid lines if necessary.
	if (table.getShowVerticalLines()) 
    {
	    g.setColor(table.getGridColor());
	    int x1 = vacatedColumnRect.x;
	    int y1 = vacatedColumnRect.y;
	    int x2 = x1 + vacatedColumnRect.width - 1;
	    int y2 = y1 + vacatedColumnRect.height - 1;
	    // Left
	    g.drawLine(x1-1, y1, x1-1, y2);
	    // Right
	    g.drawLine(x2, y1, x2, y2);
	}

	for(int row = rMin; row <= rMax; row++) 
    { 
	    // Render the cell value
	    Rectangle r = table.getCellRect(row, draggedColumnIndex, false); 
	    r.x += distance;
	    paintCell(g, r, row, draggedColumnIndex);
 
	    // Paint the (lower) horizontal grid line if necessary.
	    if (table.getShowHorizontalLines()) 
        {
		    g.setColor(table.getGridColor());
		    Rectangle rcr = table.getCellRect(row, draggedColumnIndex, true); 
		    rcr.x += distance;
		    int x1 = rcr.x;
		    int y1 = rcr.y;
		    int x2 = x1 + rcr.width - 1;
		    int y2 = y1 + rcr.height - 1;
		    g.drawLine(x1, y2, x2, y2);
	    }
	}
 }

 private void paintCell(Graphics g, Rectangle cellRect, int row, int column) 
 {
    if (table.isEditing() && table.getEditingRow()==row && table.getEditingColumn()==column) 
    {
        Component component = table.getEditorComponent();
	    component.setBounds(cellRect);
        component.validate();
    }
    else 
    {
        TableCellRenderer renderer = table.getCellRenderer(row, column);
        Component component = table.prepareRenderer(renderer, row, column);
        component.setEnabled(table.isEnabled());
        
        rendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }
 }
}