// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.view.complexcolormaps;

import edu.colorado.phet.quantumwaveinterference.model.math.Complex;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 2:04:07 PM
 */
public class MagnitudeColorMap implements ComplexColorMap {
    private double intensityScale = 20;

    public Paint getColor( Complex value ) {
        double abs = value.abs() * intensityScale;
        if( abs > 1 ) {
            abs = 1;
        }
        return new Color( (float)abs, (float)abs, (float)abs );
    }
}
