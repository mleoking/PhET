/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.qm.model.Complex;

import java.awt.*;

/**
 * From http://www.physics.brocku.ca/www/faculty/sternin/teaching/mirrors/qm/packet/WaveMap.java
 */
public class RealGrayColorMap implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    public double colorScale = 12;

    public RealGrayColorMap( SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;
    }

    public Color getPaint( int i, int k ) {
        Complex[][] wavefunction = schrodingerPanel.getDiscreteModel().getWavefunction();
        double re = wavefunction[i][k].getReal();
        re = Math.abs( re );
        if( re > 1 ) {
            System.out.println( "re = " + re );
        }
        Color color = new Color( (float)re, (float)re, (float)re );
        color = scaleUp( color );
        return color;
    }

    private Color scaleUp( Color color ) {
        Color c = new Color( scaleUp( color.getRed() ), scaleUp( color.getGreen() ), scaleUp( color.getBlue() ) );
        return c;
    }

    private float scaleUp( int color ) {
        float f = color / 255.0f;
        f *= colorScale;
        if( f > 1.0f ) {
            f = 1.0f;
        }
        return f;
    }

}
