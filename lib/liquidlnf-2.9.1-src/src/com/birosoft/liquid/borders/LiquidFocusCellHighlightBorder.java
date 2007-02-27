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
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;

import javax.swing.border.LineBorder;
import javax.swing.plaf.UIResource;

/**
 * The border for a Textfield.
 */
public class LiquidFocusCellHighlightBorder extends LineBorder implements UIResource
{
    final static float dash1[] = {1.0f};
    final static BasicStroke dashed = new BasicStroke(1.0f,
                                                    BasicStroke.CAP_BUTT,
                                                    BasicStroke.JOIN_MITER,
                                                    10.0f, dash1, 0.0f);
    
    private Color color;
    
    public LiquidFocusCellHighlightBorder(Color c)
    {
        super(c);
        color = c;
    }
    
    public LiquidFocusCellHighlightBorder(Color c, int thickness)
    {
        super(c, thickness);
        color = c;
    }
    
    /**
     * Gets the border insets for a given component.
     *
     * @param c The component to get its border insets.
     * @return Always returns the same insets as defined in <code>insets</code>.
    public Insets getBorderInsets(Component c)
    {
        return insets;
    }
     */
    
    /**
     * Use the skin to paint the border
     * @see javax.swing.border.Border#paintBorder(Component, Graphics, int, int, int, int)
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
    {
        Color oldColor = g.getColor();

        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(color);
        g2.setStroke(dashed);
        g2.drawRect(x, y+1, w-1, h-1);
        g2.setColor(oldColor);
    }
}