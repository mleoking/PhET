// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.model.waves;

import edu.colorado.phet.quantumwaveinterference.model.Wave;
import edu.colorado.phet.quantumwaveinterference.model.math.Complex;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 12:04:22 PM
 */

public class PlaneWave implements Wave {
    private double k;
    private double gridHeight;

    private double scale = 1.0;
    private double phase = 0.0;

    public PlaneWave( double k, double gridHeight ) {
        this.k = k;
        this.gridHeight = gridHeight;
    }

    public double getScale() {
        return scale;
    }

    public void setMagnitude( double scale ) {
        this.scale = scale;
    }

    public Complex getValue( int i, int j, double simulationTime ) {
//        System.out.println( "simulationTime = " + simulationTime );
//        Complex complex = new Complex( Math.cos( phase ),Math.sin( phase ) );
        Complex complex = new Complex( Math.cos( phase ), Math.sin( phase ) );
//        Complex complex = new Complex( Math.cos( k * j / gridHeight - k * k * simulationTime + phase ),
//                                       Math.sin( k * j / gridHeight - k * k * simulationTime + phase ) );

        complex.scale( scale );
//        System.out.println( "phase=" + phase + ", re=" + complex.getReal() );
        return complex;
    }

    public void setPhase( double phase ) {
        this.phase = phase;
    }
}
