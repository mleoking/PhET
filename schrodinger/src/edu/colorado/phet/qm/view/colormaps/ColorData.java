/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.common.view.util.VisibleColor;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 18, 2005
 * Time: 10:58:38 PM
 * Copyright (c) Dec 18, 2005 by Sam Reid
 */
public class ColorData {
    private float r;
    private float g;
    private float b;

    public ColorData( double wavelength ) {
        this( new VisibleColor( wavelength ) );
    }

    public ColorData( Color c ) {
        this.r = c.getRed() / 255.0f;
        this.g = c.getGreen() / 255.0f;
        this.b = c.getBlue() / 255.0f;
    }

    public Color toColor( double abs ) {
        return new Color( (float)abs * r, (float)abs * g, (float)abs * b );
    }
}
