// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import edu.colorado.phet.beerslawlab.concentration.model.ShakerParticle;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Visual representation of a solid solute particle exiting the shaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ShakerParticleNode extends ParticleNode {

    private final ShakerParticle particle;
    private final VoidFunction1<ImmutableVector2D> locationObserver;

    public ShakerParticleNode( ShakerParticle particle ) {
        super( particle );

        this.particle = particle;

        // move to particle's location
        locationObserver = new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D location ) {
                setOffset( location.toPoint2D() );
            }
        };
        particle.addLocationObserver( locationObserver );
    }

    public void cleanup() {
        particle.removeLocationObserver( locationObserver );
    }
}
