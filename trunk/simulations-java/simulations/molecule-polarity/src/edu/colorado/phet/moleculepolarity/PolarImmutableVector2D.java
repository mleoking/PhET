// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.PolarCartesianConverter;

//TODO integrate into ImmutableVector2D, either as an inner class or creation method

/**
 * Subclass of ImmutableVector2D that construction using polar coordinates.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PolarImmutableVector2D extends ImmutableVector2D {

    public PolarImmutableVector2D( double magnitude, double angle ) {
        super( PolarCartesianConverter.getX( magnitude, angle ), PolarCartesianConverter.getY( magnitude, angle ) );
    }
}
