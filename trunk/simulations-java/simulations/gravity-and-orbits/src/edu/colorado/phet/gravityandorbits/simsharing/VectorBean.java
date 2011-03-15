// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.simsharing;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * @author Sam Reid
 */
public class VectorBean implements IProguardKeepClass {
    double x;
    double y;

    public VectorBean( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public VectorBean() {
    }

    public VectorBean( ImmutableVector2D value ) {
        this( value.getX(), value.getY() );
    }

    public double getX() {
        return x;
    }

    public void setX( double x ) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY( double y ) {
        this.y = y;
    }

    public ImmutableVector2D toImmutableVector2D() {
        return new ImmutableVector2D( x, y );
    }
}
