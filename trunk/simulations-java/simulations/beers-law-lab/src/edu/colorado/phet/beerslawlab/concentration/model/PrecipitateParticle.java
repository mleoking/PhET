// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * One particle that makes up the precipitate that forms on the bottom of the beaker.
 * Precipitate particles are static (they don't move). They have no associated animation,
 * and they magically appear on the bottom of the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecipitateParticle extends SoluteParticle {
    public PrecipitateParticle( Solute solute, ImmutableVector2D location, double orientation ) {
        super( solute.getParticleColor(), solute.particleSize, location, orientation );
    }
}
