/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps3;

import edu.colorado.phet.qm.model.Complex;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 2:04:07 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */
public class MagnitudeInGrayscale3 implements ComplexColorMap {
    private double intensityScale = 20;

    public Paint getColor( Complex value ) {
        double abs = value.abs() * intensityScale;
        if( abs > 1 ) {
            abs = 1;
        }
        return new Color( (float)abs, (float)abs, (float)abs );
    }
}
