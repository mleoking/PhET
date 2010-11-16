package edu.colorado.phet.insidemagnets;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author Sam Reid
 */
public class Cell {
    //Spin vector components
    double sx;
    double sy;

    //demagnetization field components at sites
    double bx;
    double by;

    //angular rotation rate for sx, sy. Replacement for 'sz' if we were doing a 3d model
    double omega;

    public ImmutableVector2D getSpinVector() {
        return new ImmutableVector2D( sx, sy );
    }
}
