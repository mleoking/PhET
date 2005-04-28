/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.model;

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedListener;

import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Spectrometer
 * <p/>
 * Counts photons according to their wavelengths
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Spectrometer implements PhotonEmittedListener {
    private Map wavelengthToPhotonNumberMap = new HashMap();
    private boolean isRunning = false;

    private double getCountAtWavelength( double wavelength ) {
        Integer count = (Integer)wavelengthToPhotonNumberMap.get( new Double( wavelength ) );
        return ( count != null ? count.intValue() : 0 );
    }

    public void stop() {
        isRunning = false;
    }

    public void start() {
        isRunning = true;
    }

    public void reset() {
        changeListenerProxy.reset();
        wavelengthToPhotonNumberMap.clear();
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    /**
     * Bumps the count of photons that have the wavelength of the photon in
     * the specified event
     *
     * @param event
     */
    public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
        if( isRunning ) {
            Double wavelength = new Double( event.getPhoton().getWavelength() );
            Integer photonCount = (Integer)wavelengthToPhotonNumberMap.get( wavelength );
            int cnt = 0;
            if( photonCount != null ) {
                cnt = photonCount.intValue();
            }
            cnt++;
            wavelengthToPhotonNumberMap.put( wavelength, new Integer( cnt ) );
            changeListenerProxy.countChanged( new CountChangeEvent( this, wavelength.doubleValue() ) );
        }
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
        void countChanged( CountChangeEvent eventCount );

        void reset();
    }

    public class CountChangeEvent extends EventObject {
        private double wavelength;

        public CountChangeEvent( Object source, double wavelength ) {
            super( source );
            this.wavelength = wavelength;
        }

        public double getWavelength() {
            return wavelength;
        }

        public double getPhotonCount() {
            return ( (Spectrometer)getSource() ).getCountAtWavelength( wavelength );
        }
    }
}
