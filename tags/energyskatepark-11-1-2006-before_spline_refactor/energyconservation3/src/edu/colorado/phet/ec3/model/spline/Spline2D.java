package edu.colorado.phet.ec3.model.spline;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Oct 17, 2006
 * Time: 4:40:52 PM
 * Copyright (c) Oct 17, 2006 by Sam Reid
 */

public interface Spline2D {
    Point2D evaluate( double x );
}
