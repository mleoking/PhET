// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.common;

import edu.colorado.phet.common.math.PhetVector;

import java.awt.geom.GeneralPath;

public class DoubleGeneralPath {

    public DoubleGeneralPath( PhetVector phetvector ) {
        this( phetvector.getX(), phetvector.getY() );
    }

    public DoubleGeneralPath( double d, double d1 ) {
        path = new GeneralPath();
        path.moveTo( (float)d, (float)d1 );
    }

    public void lineTo( double d, double d1 ) {
        path.lineTo( (float)d, (float)d1 );
    }

    public GeneralPath getGeneralPath() {
        return path;
    }

    public void lineTo( PhetVector phetvector ) {
        lineTo( phetvector.getX(), phetvector.getY() );
    }

    GeneralPath path;
}
