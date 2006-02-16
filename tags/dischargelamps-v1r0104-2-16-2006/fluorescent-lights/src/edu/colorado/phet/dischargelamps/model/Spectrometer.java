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
import edu.colorado.phet.common.util.ModelEventChannel;
import edu.colorado.phet.quantum.model.PhotonEmittedEvent;
import edu.colorado.phet.quantum.model.PhotonEmittedListener;

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

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        isRunning = false;
        changeListenerProxy.stopped( new StateChangeEvent( this ) );
    }

    public void start() {
        isRunning = true;
        changeListenerProxy.started( new StateChangeEvent( this ) );
    }

    public void reset() {
        changeListenerProxy.reset( new StateChangeEvent( this ) );
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
    public void photonEmitted( PhotonEmittedEvent event ) {
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
    private EventChannel changeListenerChannel = new ModelEventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeListenerChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeListenerChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeListenerChannel.removeListener( listener );
    }

    public interface ChangeListener extends EventListener {
        void countChanged( CountChangeEvent eventCount );

        void started( StateChangeEvent event );

        void stopped( StateChangeEvent event );

        void reset( StateChangeEvent event );
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

    public class StateChangeEvent extends EventObject {
        public StateChangeEvent( Object source ) {
            super( source );
        }

        public Spectrometer getSpectrometer() {
            return (Spectrometer)getSource();
        }
    }
}
