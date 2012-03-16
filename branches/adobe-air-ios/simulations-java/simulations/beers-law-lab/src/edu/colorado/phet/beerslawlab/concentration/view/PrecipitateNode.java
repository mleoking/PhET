// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.view;

import java.util.HashMap;

import edu.colorado.phet.beerslawlab.concentration.model.Precipitate;
import edu.colorado.phet.beerslawlab.concentration.model.Precipitate.ParticlesChangeListener;
import edu.colorado.phet.beerslawlab.concentration.model.PrecipitateParticle;
import edu.colorado.phet.beerslawlab.concentration.view.ParticleNode.PrecipitateParticleNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of the precipitate that forms on the bottom of the beaker when the solution is saturated.
 * Manages the creation and deletion of precipitate particle nodes.
 * Origin is at bottom center of the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PrecipitateNode extends PComposite {

    public PrecipitateNode( Precipitate precipitate ) {

        // Maps model elements to their corresponding nodes.
        final HashMap<PrecipitateParticle, PrecipitateParticleNode> map = new HashMap<PrecipitateParticle, PrecipitateParticleNode>();

        precipitate.addParticlesChangeListener( new ParticlesChangeListener() {

            // A particle was added.
            public void particleAdded( PrecipitateParticle particle ) {
                PrecipitateParticleNode particleNode = new PrecipitateParticleNode( particle );
                map.put( particle, particleNode );
                addChild( particleNode );
            }

            // A particle was removed.
            public void particleRemoved( PrecipitateParticle particle ) {
                PrecipitateParticleNode particleNode = map.get( particle );
                map.remove( particle );
                removeChild( particleNode );
            }
        } );
    }

}
