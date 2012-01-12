// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.conductivity.macro.circuit;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

public class LinearBranch {

    public LinearBranch( Vector2D phetvector, Vector2D phetvector1 ) {
        start = phetvector;
        end = phetvector1;
        dv = phetvector1.getSubtractedInstance( phetvector );
    }

    public double getLength() {
        return dv.getMagnitude();
    }

    public ImmutableVector2D getLocation( double d ) {
        return start.getAddedInstance( dv.getInstanceOfMagnitude( d ) );
    }

    public Vector2D getStartPosition() {
        return start;
    }

    public Vector2D getEndPosition() {
        return end;
    }

    Vector2D start;
    Vector2D end;
    private ImmutableVector2D dv;
}
