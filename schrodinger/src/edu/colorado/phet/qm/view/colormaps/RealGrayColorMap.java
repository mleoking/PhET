/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import java.awt.*;

public class RealGrayColorMap implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    public double colorScale = 12;

    public RealGrayColorMap( SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;
    }

    public Paint getColor( int i, int k ) {
        Wavefunction wavefunction = schrodingerPanel.getDiscreteModel().getWavefunction();
        double re = wavefunction.valueAt( i, k ).getReal();
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
