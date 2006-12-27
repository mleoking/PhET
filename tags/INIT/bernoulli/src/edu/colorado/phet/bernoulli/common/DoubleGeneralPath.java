package edu.colorado.phet.bernoulli.common;

import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 9:33:13 PM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public class DoubleGeneralPath {
    GeneralPath path;

    public DoubleGeneralPath( PhetVector pt ) {
        this( pt.getX(), pt.getY() );
    }

    public DoubleGeneralPath( Point2D.Double pt ) {
        this( pt.x, pt.y );
    }

    public DoubleGeneralPath( double x, double y ) {
        path = new GeneralPath();
        path.moveTo( (float)x, (float)y );
    }

    public void lineTo( double x, double y ) {
        path.lineTo( (float)x, (float)y );
    }

    public void lineTo( Point2D.Double pt ) {
        lineTo( pt.x, pt.y );
    }

    public void lineTo( PhetVector pt ) {
        lineTo( pt.getX(), pt.getY() );
    }

    public GeneralPath getGeneralPath() {
        return path;
    }
}
