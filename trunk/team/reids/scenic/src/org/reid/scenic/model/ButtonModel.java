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

    public ButtonModel( Font font, String text, double x, double y, boolean pressed ) {
        this.font = font;
        this.text = text;
        this.x = x;
        this.y = y;
        this.pressed = pressed;
    }
}