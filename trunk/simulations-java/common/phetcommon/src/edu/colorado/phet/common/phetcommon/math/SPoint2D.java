/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.phetcommon.math;

import java.awt.geom.Point2D;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SPoint2D extends Point2D.Double implements Externalizable {
    public SPoint2D() {
        super();
    }

    public SPoint2D( double x, double y ) {
        super( x, y );
    }

    public SPoint2D( Point2D pt ) {
        this( pt.getX(), pt.getY() );
    }

    public String toString() {
        return getClass().getName() + " [" + getX() + ", " + getY() + "]";
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        x = in.readDouble();
        y = in.readDouble();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeDouble( x );
        out.writeDouble( y );
    }
}
