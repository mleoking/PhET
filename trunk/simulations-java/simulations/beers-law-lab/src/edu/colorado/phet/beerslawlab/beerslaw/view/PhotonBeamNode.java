// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.util.HashMap;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.Photon;
import edu.colorado.phet.beerslawlab.beerslaw.model.PhotonBeam;
import edu.colorado.phet.beerslawlab.beerslaw.model.PhotonBeam.PhotonBeamChangeListener;
import edu.colorado.phet.beerslawlab.beerslaw.view.BeersLawCanvas.LightRepresentation;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Representation of light as a collection of photons.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PhotonBeamNode extends PhetPNode {

    public PhotonBeamNode( Property<BeersLawSolution> solution, final PhotonBeam photonBeam,
                           final Property<LightRepresentation> representation, final ModelViewTransform mvt ) {
        setPickable( false );
        setChildrenPickable( false );

        // When the solution changes, remove all photons.
        solution.addObserver( new SimpleObserver() {
            public void update() {
                photonBeam.removeAllPhotons();
            }
        } );

        // Make this node visible when light representation is "photons".
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                setVisible( representation.get() == LightRepresentation.PHOTONS );
            }
        };
        visibilityObserver.observe( representation );

        // Node management
        final HashMap<Photon, BLLPhotonNode> map = new HashMap<Photon, BLLPhotonNode>();
        photonBeam.addPhotonBeamChangeListener( new PhotonBeamChangeListener() {

            // A photon was added.
            public void photonAdded( Photon photon ) {
                BLLPhotonNode photonNode = new BLLPhotonNode( photon, mvt );
                map.put( photon, photonNode );
                addChild( photonNode );
            }

            // A photon was removed.
            public void photonRemoved( Photon photon ) {
                BLLPhotonNode photonNode = map.get( photon );
                removeChild( photonNode );
                photonNode.cleanup();
            }
        } );
    }
}
