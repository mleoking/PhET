// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro.circuit;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2DInterface;
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

    public AbstractVector2DInterface getLocation( double d ) {
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
    private AbstractVector2DInterface dv;
}
