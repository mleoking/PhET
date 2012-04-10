// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * @author John Blanco
 */
public class HorizontalSurface {
    public final DoubleRange xRange;
    public final double yPos;

    public HorizontalSurface( DoubleRange xRange, double yPos ) {
        this.xRange = xRange;
        this.yPos = yPos;
    }

    public boolean overlapsWith( HorizontalSurface surface ) {
        return ( xRange.intersects( surface.xRange ) );
    }

    public double getCenterX() {
        return xRange.getCenter();
    }
}
