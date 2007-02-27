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
 * This is a simple 3d border class used for menu popups.
 *
 * @author Taoufik Romdhane
 */
public class LiquidPopupMenuBorder extends AbstractBorder implements UIResource
{
    
    /**
     * The border insets.
     */
    protected static Insets insets = new Insets(2, 2, 2, 2);
    
    /**
     * Draws a simple 3d border for the given component.
     *
     * @param c The component to draw its border.
     * @param g The graphics context.
     * @param x The x coordinate of the top left corner.
     * @param y The y coordinate of the top left corner.
     * @param w The width.
     * @param h The height.
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
    {
        g.setColor(new Color(255,255,255));
        g.drawRect(1, 1, w-3, h-3);
        g.setColor(new Color(175,174,174));
        g.drawRect(0, 0, w-1, h-1);        
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
}