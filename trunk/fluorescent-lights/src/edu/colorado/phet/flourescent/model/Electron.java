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

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;
import edu.colorado.phet.collision.SphericalBody;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.flourescent.FluorescentLightsConfig;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Electron
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Electron extends SphericalBody implements Collidable {

    // A fudge factor that makes the energy of an electron enough to stimulate an atom
    // only if it is moving fast enough
    private static double ENERGY_FUDGE_FACTOR = 1E-6;

    // Mass of an electron, in kg
    private static final double ELECTRON_MASS = 9.11E-31 * ENERGY_FUDGE_FACTOR;
    // Radius of an electron. An arbitrary dimension based on how it looks on the screen
    private static final double ELECTRON_RADIUS = 2;

    private CollidableAdapter collidableAdapter;

    public Electron() {
        super( FluorescentLightsConfig.ELECTRON_RADIUS );
        collidableAdapter = new CollidableAdapter( this );
        setMass( ELECTRON_MASS );
        setRadius( ELECTRON_RADIUS );
    }

    public void setPosition( double x, double y ) {
        collidableAdapter.updatePosition();
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        collidableAdapter.updatePosition();
        super.setPosition( position );
    }

    public void setVelocity( Vector2D velocity ) {
        collidableAdapter.updateVelocity();
        super.setVelocity( velocity );
        changeListenerProxy.energyChanged( new ChangeEvent( this ) );
    }

    public void setVelocity( double vx, double vy ) {
        collidableAdapter.updateVelocity();
        super.setVelocity( vx, vy );
        changeListenerProxy.energyChanged( new ChangeEvent( this ) );
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    public double getEnergy() {
        return getVelocity().getMagnitudeSq() * getMass() / 2 ;
    }

    public void setEnergy( double e ) {
        // compute the speed of the electron
        double sNew = Math.sqrt( 2 * e / getMass() );
        double sCurr = getVelocity().getMagnitude();
        setVelocity( getVelocity().scale( sNew / sCurr ) );
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

        public Electron getElectrion() {
            return (Electron)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void leftSystem( ChangeEvent changeEvent );
        void energyChanged( ChangeEvent changeEvent );
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
