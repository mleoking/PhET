// Copyright 2002-2011, University of Colorado
package org.reid.scenic.model;

import java.awt.Font;

/**
 * @author Sam Reid
 */
public class ButtonModel {
    public final Font font;
    public final String text;
    public final double x;
    public final double y;
    public final boolean pressed;
    //is the mouse hovering over the button
    public final boolean hover;

    public ButtonModel( Font font, String text, double x, double y, boolean pressed, boolean hover ) {
        this.font = font;
        this.text = text;
        this.x = x;
        this.y = y;
        this.pressed = pressed;
        this.hover = hover;
    }

    public ButtonModel pressed( boolean pressed ) {
        return new ButtonModel( font, text, x, y, pressed, hover );
    }

    public ButtonModel hover( boolean hover ) {
        return new ButtonModel( font, text, x, y, pressed, hover );
    }
}