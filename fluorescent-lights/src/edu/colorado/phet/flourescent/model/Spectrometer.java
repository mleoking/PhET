/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent.model;

import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedListener;
import edu.colorado.phet.common.util.EventChannel;

import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Spectrometer
 * <p>
 * Counts photons according to their wavelengths
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Spectrometer implements PhotonEmittedListener {
    private Map wavelengthToPhotonNumberMap = new HashMap( );

    private double getCountAtWavelength( double wavelength ) {
        Integer count = (Integer)wavelengthToPhotonNumberMap.get( new Double( wavelength));
        return ( count != null ? count.intValue() : 0 );
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    /**
     * Bumps the count of photons that have the wavelength of the photon in
     * the specified event
     * @param event
     */
    public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
        Double wavelength = new Double( event.getPhoton().getWavelength() );
        Integer photonCount = (Integer)wavelengthToPhotonNumberMap.get( wavelength);
        int cnt = 0;
        if( photonCount != null ) {
            cnt = photonCount.intValue();
        }
        cnt++;
        wavelengthToPhotonNumberMap.put( wavelength, new Integer( cnt ) );
        changeListenerProxy.countChanged( new ChangeEvent( this, wavelength.doubleValue() ) );
    }

    //----------------------------------------------------------------
    // Listener and event definitions
    //----------------------------------------------------------------
    private EventChannel changeListenerChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeListenerChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeListenerChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeListenerChannel.removeListener( listener );
    }

    public interface ChangeListener extends EventListener {
        void countChanged( ChangeEvent event );
    }

    public class ChangeEvent extends EventObject {
        private double wavelength;
        private int photonCount;

        public ChangeEvent( Object source, double wavelength ) {
            super( source );
            this.wavelength = wavelength;            
        }

        public double getWavelength() {
            return wavelength;
        }

        public double getPhotonCount() {
            return ((Spectrometer)getSource()).getCountAtWavelength( wavelength );
        }
    }
}
