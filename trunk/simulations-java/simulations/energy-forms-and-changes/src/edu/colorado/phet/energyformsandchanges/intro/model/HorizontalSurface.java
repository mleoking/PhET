// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * A simple horizontal surface.  This is represented by a range of x values
 * and a single y value.
 *
 * @author John Blanco
 */
public class HorizontalSurface {
    public final DoubleRange xRange;
    public final double yPos;

    private final ModelElement owner;

    public HorizontalSurface( DoubleRange xRange, double yPos, ModelElement owner ) {
        this.xRange = xRange;
        this.yPos = yPos;
        this.owner = owner;
    }

    public boolean overlapsWith( HorizontalSurface surface ) {
        return ( xRange.intersectsExclusive( surface.xRange ) );
    }

    public double getCenterX() {
        return xRange.getCenter();
    }

    public ModelElement getOwner() {
        return owner;
    }

    @Override
    public boolean equals( final Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        final HorizontalSurface that = (HorizontalSurface) o;

        if ( Double.compare( that.yPos, yPos ) != 0 ) { return false; }
        if ( xRange != null ? !xRange.equals( that.xRange ) : that.xRange != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = xRange != null ? xRange.hashCode() : 0;
        temp = yPos != +0.0d ? Double.doubleToLongBits( yPos ) : 0L;
        result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
        return result;
    }
}
