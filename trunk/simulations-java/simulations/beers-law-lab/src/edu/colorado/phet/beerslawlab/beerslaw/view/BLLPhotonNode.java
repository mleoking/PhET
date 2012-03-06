// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import edu.colorado.phet.beerslawlab.beerslaw.model.Photon;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.photon.PhotonNode;

/**
* Specialization of PhotonNode that moves with the photon model element.
*
* @author Chris Malley (cmalley@pixelzoom.com)
*/
class BLLPhotonNode extends PhotonNode {

    private final Photon photon;
    private final VoidFunction1<ImmutableVector2D> locationObserver;

    public BLLPhotonNode( Photon photon, final ModelViewTransform mvt ) {
        super( photon.wavelength, mvt.modelToViewDeltaX( photon.diameter ) );
        this.photon = photon;

        // move to photon's location
        locationObserver = new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D location ) {
                setOffset( mvt.modelToView( location ).toPoint2D() );
            }
        };
        photon.location.addObserver( locationObserver );
    }

    public void cleanup() {
        photon.location.removeObserver( locationObserver );
    }
}
