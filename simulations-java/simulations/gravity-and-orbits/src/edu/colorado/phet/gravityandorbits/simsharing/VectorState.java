// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.gravityandorbits.simsharing;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;

/**
 * Serializable state for simsharing, stores the state of a 2d vector with a javabean interface for serialization.
 *
 * @author Sam Reid
 */
public class VectorState implements IProguardKeepClass {

    public final double x;
    public final double y;

    public VectorState( double x, double y ) {
        this.x = x;
        this.y = y;
    }

    public VectorState( Vector2D value ) {
        this( value.getX(), value.getY() );
    }

    public Vector2D toImmutableVector2D() {
        return new Vector2D( x, y );
    }

    @Override public String toString() {
        return "VectorState{" +
               "x=" + x +
               ", y=" + y +
               '}';
    }
}