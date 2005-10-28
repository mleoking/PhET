/* Copyright 2004, Sam Reid */
package edu.colorado.phet.chart.controllers;

import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 27, 2005
 * Time: 2:02:33 AM
 * Copyright (c) Apr 27, 2005 by Sam Reid
 */

public class DottedLineBorder extends LineBorder {
    private Stroke stroke;

    /**
     * Creates a line border with the specified color and a
     * thickness = 1.
     *
     * @param color the color for the border
     */
    public DottedLineBorder( Color color, Stroke stroke ) {
        super( color );
        this.stroke = stroke;
    }

    public void paintBorder( Component c, Graphics g, int x, int y, int width, int height ) {
        if( g instanceof Graphics2D ) {
            Graphics2D g2 = (Graphics2D)g;
            Stroke orig = g2.getStroke();
            g2.setStroke( stroke );
            super.paintBorder( c, g, x, y, width, height );
            g2.setStroke( orig );
        }
        else {
            super.paintBorder( c, g, x, y, width, height );
        }

    }
}
