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

import edu.colorado.phet.collision.SphericalBody;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.util.PhysicsUtil;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Electron
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Electron extends SphericalBody {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    // Radius of an electron. An arbitrary dimension based on how it looks on the screen
    public static final double ELECTRON_RADIUS = 2;

    //----------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------

    public Electron() {
        this( 0, 0 );
    }

    public Electron( double x, double y ) {
        super( ELECTRON_RADIUS );
        setMass( PhysicsUtil.ELECTRON_MASS );
        setRadius( ELECTRON_RADIUS );
        setPosition( x, y );
    }

    //----------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------

    public void setVelocity( Vector2D velocity ) {
        super.setVelocity( velocity );
        if( changeListenerProxy != null ) {
            changeListenerProxy.energyChanged( new ChangeEvent( this ) );
        }
    }

    public void setVelocity( double vx, double vy ) {
        super.setVelocity( vx, vy );
        if( changeListenerProxy != null ) {
            changeListenerProxy.energyChanged( new ChangeEvent( this ) );
        }
    }

    /**
     * Returns the the energy of the electron in Joules
     *
     * @return
     */
    public double getEnergy() {
        double ke = DischargeLampsConfig.PIXELS_PER_NM * DischargeLampsConfig.PIXELS_PER_NM * getVelocity().getMagnitudeSq() * getMass() / 2;
        double ev = ke * PhysicsUtil.EV_PER_JOULE;
        return ev;
    }

    /**
     * Sets the energy of the electron, in EV
     *
     * @param e
     */
    public void setEnergy( double e ) {
        double ke = e * PhysicsUtil.JOULES_PER_EV;

        // compute the speed of the electron
        double sNew = Math.sqrt( 2 * ke / getMass() );
        double sCurr = getVelocity().getMagnitude();
        setVelocity( getVelocity().scale( sNew / sCurr / DischargeLampsConfig.PIXELS_PER_NM ) );
        changeListenerProxy.energyChanged( new ChangeEvent( this ) );
    }

    public void leaveSystem() {
        changeListenerProxy.leftSystem( new ChangeEvent( this ) );
    }

    //----------------------------------------------------------------
    // Events and Listeners
    //----------------------------------------------------------------
    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public Electron getElectron() {
            return (Electron)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void leftSystem( ChangeEvent changeEvent );

        void energyChanged( ChangeEvent changeEvent );
    }

    public static class ChangeListenerAdapter implements ChangeListener {
        public void leftSystem( ChangeEvent changeEvent ) {
        }

        public void energyChanged( ChangeEvent changeEvent ) {
        }
    }

    private EventChannel listenerChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)listenerChannel.getListenerProxy();

    public void addChangeListener( ChangeListener changeListener ) {
        listenerChannel.addListener( changeListener );
    }

    public void removeListener( ChangeListener changeListener ) {
        listenerChannel.removeListener( changeListener );
    }
}
