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

    void addRateChangeListener( Beam.RateChangeListener rateChangeListener );

    void addWavelengthChangeListener( Beam.WavelengthChangeListener wavelengthChangeListener );

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
        public void rateChangeOccurred( Beam.RateChangeEvent event );
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
        public void wavelengthChanged( Beam.WavelengthChangeEvent event );
    }
}
