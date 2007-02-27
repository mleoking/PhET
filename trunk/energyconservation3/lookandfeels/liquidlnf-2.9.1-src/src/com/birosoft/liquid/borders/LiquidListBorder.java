/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*	Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.birosoft.liquid.borders;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Color;

import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

/**
 * The border for a Textfield.
 */
public class LiquidListBorder extends AbstractBorder implements UIResource
{
    
    private static final Insets defaultInsets = new Insets(1, 1, 1, 1);
    
    private Insets insets;
    
    public LiquidListBorder()
    {
        insets=defaultInsets;
    }
    
    public LiquidListBorder(Insets insets)
    {
        this.insets=insets;
    }
    
    /**
     * Gets the border insets for a given component.
     *
     * @param c The component to get its border insets.
     * @return Always returns the same insets as defined in <code>insets</code>.
     */
    public Insets getBorderInsets(Component c)
    {
        return insets;
    }
    
    /**
     * Use the skin to paint the border
     * @see javax.swing.border.Border#paintBorder(Component, Graphics, int, int, int, int)
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
    {
        g.setColor(new Color(189,188,188));
        g.drawLine(x, y, w-1, y);
        g.drawLine(x, y, x, h-1);
        g.setColor(Color.white);
        g.drawLine(w-2, y+1, w-2, h-1);
        g.drawLine(w-1, y+1, w-1, h-1);
        g.drawLine(x+1, h-2, w-1, h-2);
        g.drawLine(x+1, h-1, w-1, h-1);
        g.setColor(new Color(223,222,221));
        g.drawLine(x+1, y+1, w-3, y+1);
        g.drawLine(x+1, y+1, x+1, h-3);
        g.drawLine(x, h-1, x, h-1);
        g.setColor(new Color(213,212,211));
        g.drawLine(x, y, x, y);
        g.drawLine(w-1, y, w-1, y);
        g.setColor(new Color(248,247,246));
        g.drawLine(w-1, h-1, w-1, h-1);
    }
}