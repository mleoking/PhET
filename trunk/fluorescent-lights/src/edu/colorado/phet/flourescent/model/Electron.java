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

    private static final double ELECTRON_MASS = 1;

    private CollidableAdapter collidableAdapter;
    private double energy = Photon.wavelengthToEnergy( Photon.RED ) - AtomicState.minEnergy;

    public Electron() {
        super( FluorescentLightsConfig.ELECTRON_RADIUS );
        collidableAdapter = new CollidableAdapter( this );
        setMass( ELECTRON_MASS );
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
    }

    public void setVelocity( double vx, double vy ) {
        collidableAdapter.updateVelocity();
        super.setVelocity( vx, vy );
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    public double getEnergy() {
        return energy;
    }

    public void setEnergy( double e ) {
        double de = energy - e;

        // adjust the electron's velocity
        double vNewMag = Math.sqrt( getVelocity().getMagnitudeSq() + ( 2 * de / getMass() ));
        double vCurrMag = Math.sqrt( getVelocity().getMagnitudeSq() );
        setVelocity( getVelocity().scale( vCurrMag / vNewMag ) );
    }

    //----------------------------------------------------------------
    // Events and Listeners
    //----------------------------------------------------------------
    public class Event extends EventObject {
        public Event( Object source ) {
            super( source );
        }

        public Electron getElectrion() {
            return (Electron)getSource();
        }
    }

    public interface Listener extends EventListener {
        void leftSystem( Event event );
    }

    private EventChannel listenerChannel = new EventChannel( Listener.class );
    private Listener listenerProxy = (Listener)listenerChannel.getListenerProxy();

    public void addListener( Listener listener ) {
        listenerChannel.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        listenerChannel.removeListener( listener );
    }
}
