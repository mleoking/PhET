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

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.mechanics.Body;

import java.util.EventObject;
import java.util.EventListener;
import java.awt.geom.Point2D;

/**
 * ElectronSource
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class ElectronSource extends Body {
    private double electronsPerSecond;
    private double timeSincelastElectronEmitted;
    private BaseModel model;

    protected ElectronSource( BaseModel model) {
        this.model = model;
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------
    private EventChannel listenerChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)listenerChannel.getListenerProxy();

    public void stepInTime( double dt ) {
        timeSincelastElectronEmitted += dt;
        if( timeSincelastElectronEmitted > 1 / electronsPerSecond ) {
            int numElectrons = (int)( electronsPerSecond * timeSincelastElectronEmitted );
            timeSincelastElectronEmitted = 0;
            for( int i = 0; i < numElectrons; i++ ) {
                produceElectron();
            }
        }
    }

    /**
     * Produce an electron, and notify all listeners that it has happened
     */
    private void produceElectron() {
        Electron electron = new Electron();
        electron.setPosition( this.getPosition() );
        electron.setVelocity( 1, 0 );
        model.addModelElement( electron );
        listenerProxy.electronProduced( new ElectronSourceEvent( this, electron ));
    }

    public double getElectronsPerSecond() {
        return electronsPerSecond;
    }

    public void setElectronsPerSecond( double electronsPerSecond ) {
        this.electronsPerSecond = electronsPerSecond;
    }

    public interface Listener extends EventListener {
        void electronProduced( ElectronSourceEvent event );
    }

    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    /**
     * Event class for the production of electrons
     */
    public class ElectronSourceEvent extends EventObject {
        private Electron electron;

        public ElectronSourceEvent( Object source, Electron electron ) {
            super( source );
            this.electron = electron;
        }

        public Electron getElectron() {
            return electron;
        }
    }

    public void addListener( Listener listener ) {
        listenerChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        listenerChannel.removeListener( listener );
    }
}
