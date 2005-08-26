/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.photon;

import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * PhotonSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface PhotonSource {

    Rectangle2D getBounds();

    double getPhotonsPerSecond();

    public double getMaxPhotonsPerSecond();

    double getWavelength();

    void addRateChangeListener( CollimatedBeam.RateChangeListener rateChangeListener );

    void addWavelengthChangeListener( CollimatedBeam.WavelengthChangeListener wavelengthChangeListener );

    void addPhotonEmittedListener( PhotonEmittedListener photonEmittedListener );

    void removeListener( EventListener listener );


    //----------------------------------------------------------------
    // Inner classes 
    //----------------------------------------------------------------

    public class RateChangeEvent extends EventObject {
        public RateChangeEvent( PhotonSource source ) {
            super( source );
        }

        public double getRate() {
            return ( (PhotonSource)getSource() ).getPhotonsPerSecond();
        }
    }

    public interface RateChangeListener extends EventListener {
        public void rateChangeOccurred( CollimatedBeam.RateChangeEvent event );
    }

    public class WavelengthChangeEvent extends EventObject {
        public WavelengthChangeEvent( PhotonSource source ) {
            super( source );
        }

        public double getWavelength() {
            return ( (PhotonSource)getSource() ).getWavelength();
        }
    }

    public interface WavelengthChangeListener extends EventListener {
        public void wavelengthChanged( CollimatedBeam.WavelengthChangeEvent event );
    }
}
