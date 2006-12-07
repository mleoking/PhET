/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;

import java.awt.*;
import java.util.EventListener;
import java.util.EventObject;

/**
 * PhotonSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface PhotonSource {

    Shape getBounds();

    double getPhotonsPerSecond();

    public double getMaxPhotonsPerSecond();

    double getWavelength();

    void addChangeListener( ChangeListener rateChangeListener );

    void addPhotonEmissionListener( PhotonEmissionListener photonEmissionListener );

    void removePhotonEmissionListener( PhotonEmissionListener listener );


    //----------------------------------------------------------------
    // Inner classes 
    //----------------------------------------------------------------

    public class ChangeEvent extends EventObject {
        public ChangeEvent( PhotonSource source ) {
            super( source );
        }

        public PhotonSource getPhotonSource() {
            return (PhotonSource)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        public void rateChangeOccurred( ChangeEvent event );
        public void wavelengthChanged( ChangeEvent event );
    }
}
