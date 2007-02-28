// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro.circuit;

import edu.colorado.phet.common.math.PhetVector;

public class LinearBranch {

    public LinearBranch( PhetVector phetvector, PhetVector phetvector1 ) {
        start = phetvector;
        end = phetvector1;
        dv = phetvector1.getSubtractedInstance( phetvector );
    }

    public double getLength() {
        return dv.getMagnitude();
    }

    public PhetVector getLocation( double d ) {
        return start.getAddedInstance( dv.getInstanceForMagnitude( d ) );
    }

    public PhetVector getStartPosition() {
        return start;
    }

    public PhetVector getEndPosition() {
        return end;
    }

    PhetVector start;
    PhetVector end;
    private PhetVector dv;
}
