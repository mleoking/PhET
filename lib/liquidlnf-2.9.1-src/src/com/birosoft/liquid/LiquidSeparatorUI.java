/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	Liquid Look and Feel                                                   *
 *                                                                              *
 *  Author, Miroslav Lazarevic                                                  *
 *                                                                              *
 *   For licensing information and credits, please refer to the                 *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
 *                                                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.birosoft.liquid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

public class LiquidSeparatorUI extends BasicSeparatorUI {

    public static ComponentUI createUI( JComponent c ) {
        return new LiquidSeparatorUI();
    }
    
    /** paint the seperator manually */
    public void paint( Graphics g, JComponent c ) {
        Dimension s = c.getSize();
        
        if ( ((JSeparator)c).getOrientation() == JSeparator.VERTICAL ) {
            g.setColor(new Color(189,188,188));
            g.drawLine( 0, 0, 0, s.height );
            
            g.setColor(new Color(255,255,255));
            g.drawLine( 1, 0, 1, s.height );
        } else {
            JComponent p = (JComponent) c.getParent();
            
            Integer maxValueInt = (Integer) p.getClientProperty(LiquidMenuItemUI.MAX_ICON_WIDTH);
            int maxValue = maxValueInt == null ? 16 : maxValueInt.intValue();
            
            Rectangle rect = new Rectangle(0, 0, maxValue + LiquidMenuItemUI.defaultTextIconGap, s.height);
            g.setColor(new Color(189,188,188));
            g.drawLine(rect.x,0,s.width,0);
            
            g.setColor(new Color(255,255,255));
            g.drawLine(rect.x,1,s.width,1);
        }
    }
}