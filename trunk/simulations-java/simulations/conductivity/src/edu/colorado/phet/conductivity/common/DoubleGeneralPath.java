// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.conductivity.common;

import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

public class DoubleGeneralPath {

    public DoubleGeneralPath( MutableVector2D phetvector ) {
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

    public void lineTo( MutableVector2D phetvector ) {
        lineTo( phetvector.getX(), phetvector.getY() );
    }

    GeneralPath path;

    public void lineTo( Vector2D phetvector2 ) {
        lineTo( phetvector2.getX(), phetvector2.getY() );
    }
}
