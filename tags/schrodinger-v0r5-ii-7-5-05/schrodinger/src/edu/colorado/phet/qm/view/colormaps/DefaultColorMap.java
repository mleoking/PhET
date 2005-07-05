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
public class DefaultColorMap implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    private double colorScale;
    private double intensityScale;

    public DefaultColorMap( SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;
        intensityScale = 20;
        colorScale = 20;
    }

    public Color getColor( int i, int k ) {
        Wavefunction wavefunction = schrodingerPanel.getDiscreteModel().getWavefunction();

        double h = Math.abs( wavefunction.valueAt( i, k ).getReal() ) * colorScale;
        double s = Math.abs( wavefunction.valueAt( i, k ).getImaginary() ) * colorScale;
        double b = getBrightness( wavefunction.valueAt( i, k ).abs() );
        if( h > 1 ) {
            h = 1;
        }
        if( s > 1 ) {
            s = 1;
        }
        Color color = new Color( Color.HSBtoRGB( (float)h, (float)s, (float)b ) );
        return color;
    }


    public Potential getPotential() {
        return schrodingerPanel.getDiscreteModel().getPotential();
    }

    protected double getBrightness( double x ) {
        double b = x * intensityScale;
        if( b > 1 ) {
            b = 1;
        }
        return b;
    }
}
