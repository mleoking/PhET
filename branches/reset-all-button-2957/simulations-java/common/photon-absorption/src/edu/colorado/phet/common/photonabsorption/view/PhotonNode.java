// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.photonabsorption.view;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.photonabsorption.PhotonAbsorptionResources;
import edu.colorado.phet.common.photonabsorption.model.Photon;
import edu.colorado.phet.common.photonabsorption.model.WavelengthConstants;
import edu.colorado.phet.common.piccolophet.util.PhotonImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * PNode that represents a photon in the view.
 *
 * @author John Blanco
 */
public class PhotonNode extends PNode implements Observer {

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final PImage photonImage;
    private final Photon photon; // Model element represented by this node.
    private final ModelViewTransform2D mvt;

    // Map of photon wavelengths to visual images used for representing them.
    private static final HashMap<Double, String> mapWavelengthToImageName = new HashMap<Double, String>(){{
        put( WavelengthConstants.MICRO_WAVELENGTH, "microwave-photon.png");
        put( WavelengthConstants.IR_WAVELENGTH, "photon-660.png");
        put( WavelengthConstants.VISIBLE_WAVELENGTH, "thin2.png");
        put( WavelengthConstants.UV_WAVELENGTH, "photon-100.png");
    }};

    // For debug and testing.  TODO: Remove this if ultimately not used.
    private static final boolean USE_PHOTON_FACTORY = false;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    /**
     * Construct a photon node given only a wavelength.  This is intended for
     * use in places like control panels in the play area, where the node is
     * needed but doesn't really correspond to anything in the model.
     */
    public PhotonNode( double wavelength ){
        this( new Photon(wavelength), new ModelViewTransform2D());
    }

    /**
     * Primary constructor.
     */
    public PhotonNode( Photon photon, ModelViewTransform2D mvt ) {

        this.photon = photon;
        this.photon.addObserver( this );
        this.mvt = mvt;

        if ( USE_PHOTON_FACTORY ){
            if ( photon.getWavelength() == WavelengthConstants.MICRO_WAVELENGTH ) {
                // Special case for microwaves, since PhotonImageFactory makes all
                // photons with a wavelength longer than visible light look the
                // same.
                // TODO: Do we want to change PhotonImageFactory to handle this case?
                photonImage = new PImage( PhotonAbsorptionResources.getImage( "microwave-photon.png" ) );
            }
            else {
                photonImage = new PImage( PhotonImageFactory.lookupPhotonImage( photon.getWavelength() * 1E9, 35 ) );
            }
        }
        else{
            assert mapWavelengthToImageName.containsKey( photon.getWavelength() );
            photonImage = new PImage( PhotonAbsorptionResources.getImage( mapWavelengthToImageName.get( photon.getWavelength() ) ) );
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
}
