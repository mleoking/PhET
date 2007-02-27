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
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaTableHeaderUI extends BasicTableHeaderUI
{
    public static ComponentUI createUI(JComponent c)
    {
        return new OyoahaTableHeaderUI();
    }

    public void update(Graphics g, JComponent c)
    {     
      OyoahaUtilities.paintBackground(g,c);
      paint(g,c);
    }
    
    public void paint(Graphics g, JComponent c) 
    {
        if (header.getColumnModel().getColumnCount() <= 0) 
        { 
            return; 
        }
        
        Rectangle clip = g.getClipBounds(); 
        TableColumnModel cm = header.getColumnModel(); 
        
        int cMin = cm.getColumnIndexAtX(clip.x); 
        int cMax = cm.getColumnIndexAtX(clip.x + clip.width - 1);
        
        // This should never happen. 
        if (cMin == -1) 
        {
            cMin = 0;
        }
        
        // If the table does not have enough columns to fill the view we'll get -1.
        // Replace this with the index of the last column.
        if (cMax == -1) 
        {
            cMax = cm.getColumnCount()-1;
        }

        TableColumn draggedColumn = header.getDraggedColumn(); 
        Rectangle cellRect = header.getHeaderRect(cMin); 
    
        for(int column = cMin; column <= cMax ; column++) 
        { 
            TableColumn aColumn = cm.getColumn(column); 
            int columnWidth = aColumn.getWidth(); 
            cellRect.width = columnWidth;
        
            if (aColumn != draggedColumn) 
            {
    	        paintCell(g, cellRect, column);
            } 
        
            cellRect.x += columnWidth; 
        } 

        // Paint the dragged column if we are dragging. 
        if (draggedColumn != null) 
        { 
            int draggedColumnIndex = viewIndexForColumn(draggedColumn); 
            Rectangle draggedCellRect = header.getHeaderRect(draggedColumnIndex); 
            
            // Draw a gray well in place of the moving column. 
            OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject(header.getTable());
            Color color = header.getTable().getBackground();
            int state = OyoahaUtilities.getStatus(header.getTable());

            if(o!=null && color instanceof UIResource)
            {
                o.paintBackground(g, header, draggedCellRect.x, draggedCellRect.y, draggedCellRect.width-1, draggedCellRect.height, state);
            }
            else
            {
                OyoahaUtilities.paintColorBackground(g, header, draggedCellRect.x, draggedCellRect.y, draggedCellRect.width-1, draggedCellRect.height, color, state);
            }           

            draggedCellRect.x += header.getDraggedDistance();
            ;
            o = OyoahaUtilities.getBackgroundObject(header);
            color = header.getBackground();
            state = OyoahaUtilities.getStatus(header);
            
            if(o!=null && color instanceof UIResource)
            {
                o.paintBackground(g, header, draggedCellRect.x, draggedCellRect.y, draggedCellRect.width, draggedCellRect.height, state);
            }
            else
            {
                OyoahaUtilities.paintColorBackground(g, header, draggedCellRect.x, draggedCellRect.y, draggedCellRect.width, draggedCellRect.height, color, state);
            } 
            
            paintCell(g, draggedCellRect, draggedColumnIndex);
        }

        // Remove all components in the rendererPane. 
        rendererPane.removeAll(); 
    }

    private Component getHeaderRenderer(int columnIndex) 
    { 
        TableColumn aColumn = header.getColumnModel().getColumn(columnIndex); 
        TableCellRenderer renderer = aColumn.getHeaderRenderer(); 
        
        if (renderer==null) 
        { 
            renderer = header.getDefaultRenderer();
        }
        
        return renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false, false, -1, columnIndex);
    }

    private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) 
    {
        Component component = getHeaderRenderer(columnIndex);
        component.setEnabled(header.getTable().isEnabled());
        
        if(component instanceof DefaultTableCellRenderer)
        ((javax.swing.table.DefaultTableCellRenderer)component).setOpaque(false);
        
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    private int viewIndexForColumn(TableColumn aColumn) 
    {
        TableColumnModel cm = header.getColumnModel();
        
        for (int column = 0; column < cm.getColumnCount(); column++) 
        {
            if (cm.getColumn(column) == aColumn) 
            {
                return column;
            }
        }
        
        return -1;
    }
    
}