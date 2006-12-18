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

    void addRateChangeListener( RateChangeListener rateChangeListener );

    void addWavelengthChangeListener( WavelengthChangeListener wavelengthChangeListener );

    void addPhotonEmittedListener( PhotonEmissionListener photonEmissionListener );

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
        public void rateChangeOccurred( RateChangeEvent event );
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
        public void wavelengthChanged( WavelengthChangeEvent event );
    }
}
