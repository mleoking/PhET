/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*        Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid.borders;

import com.birosoft.liquid.skin.Skin;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Color;

import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

/**
 * The border for a Textfield.
 */
public class LiquidProgressBarBorder extends AbstractBorder implements UIResource {
    private static final Insets defaultInsets = new Insets(3, 5, 3, 5);
//    static Skin skin;
    private Insets insets;

    public LiquidProgressBarBorder() {
        insets = defaultInsets;
    }

    public LiquidProgressBarBorder(Insets insets) {
        this.insets = insets;
    }

    /**
     * Gets the border insets for a given component.
     *
     * @param c The component to get its border insets.
     * @return Always returns the same insets as defined in <code>insets</code>.
     */
    public Insets getBorderInsets(Component c) {
        return insets;
    }
    
    public void setInsets(Insets i) {
        insets = i;
    }
    
    /**
     * lazy initialization of the skin
     */
//    public Skin getSkin() {
//        if (skin == null) {
//            skin = new Skin("textbox.png", 2, 3);
//        }

//        return skin;
//    }

    /**
     * Use the skin to paint the border
     * @see javax.swing.border.Border#paintBorder(Component, Graphics, int, int, int, int)
     */
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        g.setColor(Color.RED);
        g.drawLine(0, 0, 0, h);
        g.drawLine(w, w, w, h);
//        int index = c.isEnabled() ? 0 : 1;
//        getSkin().draw(g, index, w, h);

    }
    
}
