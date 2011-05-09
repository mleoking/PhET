// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.insidemagnets;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author Sam Reid
 */
public class Cell {
    private static Random random = new Random();
    //Spin vector components
    double sx;
    double sy;

    //demagnetization field components at sites
    double bx;
    double by;

    //angular rotation rate for sx, sy. Replacement for 'sz' if we were doing a 3d model
    double omega;

    public Cell() {
//        ImmutableVector2D vector = new ImmutableVector2D( random.nextDouble()*2-1,random.nextDouble()*2-1 ).getNormalizedInstance();
        ImmutableVector2D vector = new ImmutableVector2D( 1, 0 ).getNormalizedInstance();
        sx = vector.getX();
        sy = vector.getY();
    }

    public ImmutableVector2D getSpinVector() {
        return new ImmutableVector2D( sx, sy );
    }
}
