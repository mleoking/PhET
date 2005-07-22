/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 12:04:22 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class PlaneWave2D implements Wave {
    private AbstractVector2D k;
    private double gridDim;

    private double scale = 1.0;
    private double phase = 0.0;

    public PlaneWave2D( AbstractVector2D k, double gridDim ) {
        this.k = k;
        this.gridDim = gridDim;
    }

    public double getScale() {
        return scale;
    }

    public void setMagnitude( double scale ) {
        this.scale = scale;
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        Vector2D loc = new Vector2D.Double( i, j );
        double kDotJ = k.dot( loc );
        double kk = k.dot( k );
        Complex complex = new Complex( Math.cos( kDotJ / gridDim - kk * simulationTime + phase ), Math.sin( kDotJ / gridDim - kk * simulationTime + phase ) );
        complex.scale( scale );
        return complex;
    }

    public void setPhase( double phase ) {
        this.phase = phase;
    }
}
