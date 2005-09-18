/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 2:04:07 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */
public class MagnitudeInGrayscale implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    private double intensityScale = 20;

    public MagnitudeInGrayscale( SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;
    }

    public Paint getColor( int i, int k ) {
        Wavefunction wavefunction = schrodingerPanel.getDiscreteModel().getWavefunction();
        double abs = wavefunction.valueAt( i, k ).abs() * intensityScale;
        if( abs > 1 ) {
            abs = 1;
        }
        Color color = new Color( (float)abs, (float)abs, (float)abs );
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
