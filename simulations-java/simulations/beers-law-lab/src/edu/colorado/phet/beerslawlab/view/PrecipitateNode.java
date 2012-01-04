// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import edu.colorado.phet.beerslawlab.model.Precipitate;
import edu.colorado.phet.beerslawlab.model.Precipitate.PrecipitateListener;
import edu.colorado.phet.beerslawlab.model.PrecipitateParticle;
import edu.colorado.phet.beerslawlab.view.PrecipitateParticleNode.PrecipitateParticleNodeListener;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * This node manages the precipitate that forms on the bottom of the beaker when the solution is saturated.
 * Origin is at bottom center of the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecipitateNode extends PComposite {

    public PrecipitateNode( Precipitate precipitate ) {

        final PrecipitateParticleNodeListener nodeListener = new PrecipitateParticleNodeListener() {
            public void removeNode( PrecipitateParticleNode node ) {
                removeChild( node );
            }
        };

        precipitate.addListener( new PrecipitateListener() {
            public void particleAdded( PrecipitateParticle particle ) {
                PrecipitateParticleNode particleNode = new PrecipitateParticleNode( particle );
                particleNode.addListener( nodeListener );
                addChild( particleNode );
            }
        } );
    }
}
