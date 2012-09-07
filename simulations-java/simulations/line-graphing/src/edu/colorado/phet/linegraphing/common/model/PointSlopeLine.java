// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import java.awt.Color;

/**
 * An immutable line, specified in point-slope form: (y - y1) = m(x - x1)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeLine extends PointPointLine {

    public PointSlopeLine( double x1, double y1, double rise, double run, Color color ) {
        super( x1, y1, x1 + run, y1 + rise, color );
    }
}
