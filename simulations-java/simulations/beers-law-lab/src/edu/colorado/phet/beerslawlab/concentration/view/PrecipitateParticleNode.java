// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import edu.colorado.phet.beerslawlab.concentration.model.PrecipitateParticle;

/**
 * Visual representation of a precipitate particle.
 * The class exists solely to constrain the constructor to a specific type of particle.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PrecipitateParticleNode extends ParticleNode {
    public PrecipitateParticleNode( PrecipitateParticle particle ) {
        super( particle );
    }
}
