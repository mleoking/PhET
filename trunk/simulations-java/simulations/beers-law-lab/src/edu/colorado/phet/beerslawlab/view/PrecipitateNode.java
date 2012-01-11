// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.util.HashMap;

import edu.colorado.phet.beerslawlab.model.Precipitate;
import edu.colorado.phet.beerslawlab.model.Precipitate.PrecipitateListener;
import edu.colorado.phet.beerslawlab.model.SoluteParticle;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of the precipitate that forms on the bottom of the beaker when the solution is saturated.
 * Manages the creation and deletion of precipitate particle nodes.
 * Origin is at bottom center of the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecipitateNode extends PComposite {

    public PrecipitateNode( Precipitate precipitate ) {

        // Maps model elements to their corresponding nodes.
        final HashMap<SoluteParticle, PrecipitateParticleNode> map = new HashMap<SoluteParticle, PrecipitateParticleNode>();

        precipitate.addListener( new PrecipitateListener() {

            // A particle was added.
            public void particleAdded( SoluteParticle particle ) {
                PrecipitateParticleNode particleNode = new PrecipitateParticleNode( particle );
                map.put( particle, particleNode );
                addChild( particleNode );
            }

            // A particle was removed.
            public void particleRemoved( SoluteParticle particle ) {
                PrecipitateParticleNode particleNode = map.get( particle );
                map.remove( particle );
                removeChild( particleNode );
            }
        } );
    }
}
