/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.wavefunctioncolormaps;

import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.colormaps.WavefunctionColorMap;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 2:04:07 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */
public class MagnitudeInGrayscale2 implements WavefunctionColorMap {
    private double intensityScale = 20;

    public Paint getColor( Wavefunction wavefunction, int i, int k ) {
        double abs = wavefunction.valueAt( i, k ).abs() * intensityScale;
        if( abs > 1 ) {
            abs = 1;
        }
        return new Color( (float)abs, (float)abs, (float)abs );
    }
}
