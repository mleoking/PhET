// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.conductivity.macro.circuit;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;

public class LinearBranch {

    public LinearBranch( MutableVector2D phetvector, MutableVector2D phetvector1 ) {
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

    public MutableVector2D getStartPosition() {
        return start;
    }

    public MutableVector2D getEndPosition() {
        return end;
    }

    MutableVector2D start;
    MutableVector2D end;
    private ImmutableVector2D dv;
}
