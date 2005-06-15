/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colormaps;

import edu.colorado.phet.qm.model.Complex;
import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import java.awt.*;

/**
 * From http://www.physics.brocku.ca/www/faculty/sternin/teaching/mirrors/qm/packet/WaveMap.java
 */
public class VisualColorMap implements ColorMap {
    private SchrodingerPanel schrodingerPanel;
    public double colorScale = 12;

    public VisualColorMap( SchrodingerPanel schrodingerPanel ) {
        this.schrodingerPanel = schrodingerPanel;
    }

    public Color getPaint( int i, int k ) {
        Complex[][] wavefunction = schrodingerPanel.getDiscreteModel().getWavefunction();

        Color color = VisZ( wavefunction[i][k] );
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

    private Color VisZ( Complex z ) {
        double x, y, red, green, blue, a, b, d, r;
        x = z.getReal();
        y = z.getImaginary();
        r = Math.sqrt( x * x + y * y );
        a = 0.40824829046386301636 * x;
        b = 0.70710678118654752440 * y;
        d = 1.0 / ( 1. + r * r );
        red = 0.5 + 0.81649658092772603273 * x * d;
        green = 0.5 - d * ( a - b );
        blue = 0.5 - d * ( a + b );
        d = 0.5 - r * d;
        if( r < 1 ) {
            d = -d;
        }
        red += d;
        green += d;
        blue += d;
        return ( new Color( (float)red, (float)green, (float)blue ) );
    }

}
