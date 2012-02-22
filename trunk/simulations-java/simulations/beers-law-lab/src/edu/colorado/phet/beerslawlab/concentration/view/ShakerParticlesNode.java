// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.util.HashMap;

import edu.colorado.phet.beerslawlab.concentration.model.ShakerParticle;
import edu.colorado.phet.beerslawlab.concentration.model.ShakerParticles;
import edu.colorado.phet.beerslawlab.concentration.model.ShakerParticles.ParticlesChangeListener;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Manages the solid solute particles as they travel between the shaker and their inevitable demise in the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ShakerParticlesNode extends PComposite {

    public ShakerParticlesNode( ShakerParticles shakerParticles ) {

        // Maps model elements to their corresponding nodes.
        final HashMap<ShakerParticle, edu.colorado.phet.beerslawlab.concentration.view.ParticleNode.ShakerParticleNode> map = new HashMap<ShakerParticle, edu.colorado.phet.beerslawlab.concentration.view.ParticleNode.ShakerParticleNode>();

        shakerParticles.addParticlesChangeListener( new ParticlesChangeListener() {

            // A particle was added.
            public void particleAdded( ShakerParticle particle ) {
                edu.colorado.phet.beerslawlab.concentration.view.ParticleNode.ShakerParticleNode particleNode = new edu.colorado.phet.beerslawlab.concentration.view.ParticleNode.ShakerParticleNode( particle );
                map.put( particle, particleNode );
                addChild( particleNode );
            }

            // A particle was removed.
            public void particleRemoved( ShakerParticle particle ) {
                edu.colorado.phet.beerslawlab.concentration.view.ParticleNode.ShakerParticleNode particleNode = map.get( particle );
                map.remove( particle );
                removeChild( particleNode );
                particleNode.cleanup();
            }
        } );
    }
}
