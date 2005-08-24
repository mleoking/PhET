/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.view;

import edu.colorado.phet.lasers.model.photon.PhotonEmittedListener;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.view.PhotonGraphic;
import edu.colorado.phet.photoelectric.module.PhotoelectricModule;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;

/**
 * PhotonGraphicManager
 * <p>
 * Creates, adds and removes graphics for photons
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
/**
 */
public class PhotonGraphicManager implements PhotonEmittedListener {
    private PhotoelectricModule module;

    public PhotonGraphicManager( PhotoelectricModule module ) {
        this.module = module;
    }

    public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
        if( module.getPhotonViewEnabled() ) {
            Photon photon = event.getPhoton();
            final PhotonGraphic pg = PhotonGraphic.getInstance( module.getApparatusPanel(), photon );
            module.getApparatusPanel().addGraphic( pg, PhotoelectricConfig.BEAM_LAYER );

            photon.addLeftSystemListener( new Photon.LeftSystemEventListener() {
                public void leftSystemEventOccurred( Photon.LeftSystemEvent event ) {
                    module.getApparatusPanel().removeGraphic( pg );
                }
            } );
        }
    }
}
