// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.model.waves;

import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.quantumwaveinterference.model.Wave;
import edu.colorado.phet.quantumwaveinterference.model.math.Complex;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 12:04:22 PM
 */

public class PlaneWave2D implements Wave {
    private Vector2D k;
    private double gridDim;

    private double scale = 1.0;
    private double phase = 0.0;
    private double dPhase;

    public PlaneWave2D( Vector2D k, double gridDim ) {
        this.k = k;
        this.gridDim = gridDim;
    }

    public void setWaveVector( Vector2D k ) {
        this.k = k;
    }

    public double getScale() {
        return scale;
    }

    public void setMagnitude( double scale ) {
        this.scale = scale;
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        MutableVector2D loc = new MutableVector2D( i, j );
        double kDotJ = k.dot( loc );
        double w = 1.0 / k.magnitude();
        phase = 0 + dPhase;
//        System.out.println( "w = " + w );
        Complex complex = new Complex(
                Math.cos( kDotJ / gridDim - w * simulationTime + phase ),
                Math.sin( kDotJ / gridDim - w * simulationTime + phase ) );
        complex.scale( scale );
        return complex;
    }

    public void setPhase( double phase ) {
        this.phase = phase;
    }

    public void setPhaseOffset( double dPhase ) {
        this.dPhase = dPhase;
    }
}
