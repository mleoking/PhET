// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.greenhouse.view;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.util.PhotonImageFactory;
import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.Photon;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * PNode that represents a photon in the view.
 *
 * @author John Blanco
 */
public class PhotonNode extends PNode implements Observer {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final PImage photonImage;
    private final Photon photon; // Model element represented by this node.
    private final ModelViewTransform2D mvt;

    // Map of photon wavelengths to visual images used for representing them.
    private static final HashMap<Double, String> mapWavelengthToImageName = new HashMap<Double, String>(){{
        put( GreenhouseConfig.microWavelength, "microwave-photon.png");
        put( GreenhouseConfig.irWavelength, "photon-660.png");
        put( GreenhouseConfig.visibleWaveLength, "thin2.png");
        put( GreenhouseConfig.uvWavelength, "photon-100.png");
    }};

    // For debug and testing.
    private static final boolean USE_PHOTON_FACTORY = false;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public PhotonNode( Photon photon, ModelViewTransform2D mvt ) {

        this.photon = photon;
        this.photon.addObserver( this );
        this.mvt = mvt;

        if ( USE_PHOTON_FACTORY ){
            if ( photon.getWavelength() == GreenhouseConfig.microWavelength ) {
                // Special case for microwaves, since PhotonImageFactory makes all
                // photons with a wavelength longer than visible light look the
                // same.
                // TODO: Do we want to change PhotonImageFactory to handle this case?
                photonImage = new PImage( GreenhouseResources.getImage( "microwave-photon.png" ) );
            }
            else {
                photonImage = new PImage( PhotonImageFactory.lookupPhotonImage( photon.getWavelength() * 1E9, 35 ) );
            }
        }
        else{
            assert mapWavelengthToImageName.containsKey( photon.getWavelength() );
            photonImage = new PImage( GreenhouseResources.getImage( mapWavelengthToImageName.get( photon.getWavelength() ) ) );
        }
        photonImage.setOffset( -photonImage.getFullBoundsReference().width / 2,
                -photonImage.getFullBoundsReference().height / 2 );
        addChild( photonImage );
        updatePosition();
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public void update( Observable o, Object arg ) {
        updatePosition();
    }

    private void updatePosition() {
        setOffset( mvt.modelToViewDouble( photon.getLocation() ) );
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
