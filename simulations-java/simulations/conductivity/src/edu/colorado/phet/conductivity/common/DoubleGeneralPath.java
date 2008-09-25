// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.common;

import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

public class DoubleGeneralPath {

    public DoubleGeneralPath( Vector2D.Double phetvector ) {
        this( phetvector.getX(), phetvector.getY() );
    }

    public DoubleGeneralPath( double d, double d1 ) {
        path = new GeneralPath();
        path.moveTo( (float) d, (float) d1 );
    }

    public void lineTo( double d, double d1 ) {
        path.lineTo( (float) d, (float) d1 );
    }

    public GeneralPath getGeneralPath() {
        return path;
    }

    public void lineTo( Vector2D.Double phetvector ) {
        lineTo( phetvector.getX(), phetvector.getY() );
    }

    GeneralPath path;

    public void lineTo( AbstractVector2D phetvector2 ) {
        lineTo( phetvector2.getX(), phetvector2.getY() );
    }
}
