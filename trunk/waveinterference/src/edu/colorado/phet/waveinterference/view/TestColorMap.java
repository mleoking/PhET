/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.waveinterference.model.Lattice2D;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:54:41 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class TestColorMap implements ColorMap {
    private Lattice2D lattice;
    Color root = new Color( 0, 0, 255 );

    public TestColorMap( Lattice2D lattice ) {
        this.lattice = lattice;
    }

    public Paint getColor( int i, int k ) {
        double value = ( lattice.valueAt( i, k ) + 1.0 ) / 2.0;
        value = MathUtil.clamp( 0, value, 1 );
        return new Color( (int)( root.getRed() * value ), (int)( root.getGreen() * value ), (int)( root.getBlue() * value ) );
//        double red = value < 0 ? 0.0 : 1.0;
////        value= MathUtil.clamp( 0,value, 1.0);
//        value = Math.abs( value );
//        value = MathUtil.clamp( 0, value, 1.0 );
//        return new Color( (float)red, (float)value, (float)value );
//        return Color.blue;
    }
}
