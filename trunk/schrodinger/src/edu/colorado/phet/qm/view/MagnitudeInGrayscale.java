/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.qm.model.Complex;
import edu.colorado.phet.qm.model.Potential;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 2:04:07 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */
public class MagnitudeInGrayscale implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    public double intensityScale = 10;

    public MagnitudeInGrayscale( SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;
    }

    public Paint getPaint( int i, int k ) {
        Complex[][] wavefunction = schrodingerPanel.getDiscreteModel().getWavefunction();
        double abs = wavefunction[i][k].abs() * intensityScale;
        if( abs > 1 ) {
            abs = 1;
        }
        Color color = new Color( (float)abs, (float)abs, (float)abs );
        double potval = getPotential().getPotential( i, k, 0 );
        if( potval > 0 ) {
            color = new Color( 100, color.getGreen(), color.getBlue() );
        }
        return color;
    }

    protected double getBrightness( double x ) {
        double b = x * intensityScale;
        if( b > 1 ) {
            b = 1;
        }
        return b;
    }

    public Potential getPotential() {
        return schrodingerPanel.getDiscreteModel().getPotential();
    }
}
