/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.complexcolormaps;

import edu.colorado.phet.qm.model.math.Complex;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 17, 2005
 * Time: 5:32:34 PM
 * Copyright (c) Dec 17, 2005 by Sam Reid
 * <p/>
 * <p/>
 * From http://www.physics.brocku.ca/www/faculty/sternin/teaching/mirrors/qm/packet/WaveMap.java
 */

public class VisualColorMap3 implements ComplexColorMap {
    private double brightness = 30;

    private float scaleFloat( float f ) {
        f *= brightness;
        if( f > 1.0f ) {
            f = 1.0f;
        }
        return f;
    }

    public Paint getColor( Complex z ) {
        double x = z.getReal();
        double y = z.getImaginary();
        double r = Math.sqrt( x * x + y * y );
        double a = 0.40824829046386301636 * x;
        double b = 0.70710678118654752440 * y;
        double d = 1.0 / ( 1. + r * r );
        double red = 0.5 + 0.81649658092772603273 * x * d;
        double green = 0.5 - d * ( a - b );
        double blue = 0.5 - d * ( a + b );
        d = 0.5 - r * d;
        if( r < 1 ) {
            d = -d;
        }
        red += d;
        green += d;
        blue += d;

        float rVal = scaleFloat( (float)red );
        float gVal = scaleFloat( (float)green );
        float blueVal = scaleFloat( (float)blue );

        return new Color( rVal, gVal, blueVal );
    }

}
