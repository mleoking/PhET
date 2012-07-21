// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.quantum.model;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.EventObject;

import edu.colorado.phet.common.collision.Collidable;
import edu.colorado.phet.common.collision.CollidableAdapter;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Particle;
import edu.colorado.phet.common.phetcommon.util.EventChannel;
import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;

/**
 * Class: Photon
 * Package: edu.colorado.phet.quantum.model
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
public class Photon extends Particle implements Collidable {

    //----------------------------------------------------------------
    // Class data and methods
    //----------------------------------------------------------------

    //    static public double SPEED = 1;
    static public double DEFAULT_SPEED = 1;
    static public double RADIUS = 10;
    static public double RED = 680;
    static public double DEEP_RED = 640;
    static public double BLUE = 440;
    static public double MIN_VISIBLE_WAVELENGTH = 380;
    static public double MAX_VISIBLE_WAVELENGTH = 710;
    static public double GRAY = 5000;

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    // If this photon was produced by the stimulation of another, this
    // is a reference to that photon.
    private Photon parentPhoton;
    // If this photon has stimulated the production of another photon, this
    // is a reference to that photon
    private Photon childPhoton;
    private CollidableAdapter collidableAdapter;
    private double wavelength;

    /**
     * Constructor is protected so that clients of the class must use static createPrimaryBorder()
     * methods. This allows us to manage a free pool of photons and not hit the
     * heap so hard.
     */
    protected Photon() {
        collidableAdapter = new CollidableAdapter( this );
        setVelocity( DEFAULT_SPEED, 0 );
    }

    public Photon( Point2D location, MutableVector2D velocity ) {
        this();
        setPosition( location );
        setVelocity( velocity );
    }

    public Photon( double wavelength, Point2D location, MutableVector2D velocity ) {
        this( location, velocity );
        setWavelength( wavelength );
    }
//    /**
//     * If the photon is created by a CollimatedBeam, it should use this method,
//     * so that the photon can tell the CollimatedBeam if it is leaving the system.
//     */
//    static public Photon create( double wavelength, Point2D location, Vector2D velocity ) {
//        Photon newPhoton = create( location, velocity );
//        newPhoton.setWavelength( wavelength );
//        return newPhoton;
//    }


    /**
     * Rather than use the superclass behavior, the receiver
     * puts itself in the class free pool, so it can be used
     * again. This helps prevent us from flogging the heap.
     */
    public void removeFromSystem() {
        leftSystemListenerProxy.leftSystemEventOccurred( new LeftSystemEvent( this ) );
//        this.removeAllLeftSystemListeners();
        leftSystemEventChannel.removeAllListeners();
        velocityChangedEventChannel.removeAllListeners();
        this.removeAllObservers();
    }

    public double getWavelength() {
        return wavelength;
    }

    public void setWavelength( double wavelength ) {
        this.wavelength = wavelength;
    }

    public double getEnergy() {
        return PhysicsUtil.wavelengthToEnergy( wavelength );
    }

    public Photon getParentPhoton() {
        return parentPhoton;
    }

    public void setParentPhoton( Photon parentPhoton ) {
        this.parentPhoton = parentPhoton;
    }

    public Photon getChildPhoton() {
        return childPhoton;
    }

    public void setChildPhoton( Photon childPhoton ) {
        this.childPhoton = childPhoton;
    }

    public void setVelocity( double vx, double vy ) {
        collidableAdapter.updateVelocity();
        super.setVelocity( vx, vy );
        if ( !getVelocity().equals( getVelocityPrev() ) ) {
            VelocityChangedEvent vce = new VelocityChangedEvent();
            velocityChangedListenerProxy.velocityChanged( vce );
        }
    }

    public void setPosition( double x, double y ) {
        collidableAdapter.updatePosition();
        super.setPosition( x, y );
    }

    public void setPosition( Point2D position ) {
        collidableAdapter.updatePosition();
        super.setPosition( position );
    }

    public MutableVector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    //-------------------------------------------------------------------------------------
    // LeftSystemEvent handling
    //-------------------------------------------------------------------------------------
    private EventChannel leftSystemEventChannel = new EventChannel( LeftSystemEventListener.class );
    private LeftSystemEventListener leftSystemListenerProxy = (LeftSystemEventListener) leftSystemEventChannel.getListenerProxy();

    private EventChannel velocityChangedEventChannel = new EventChannel( VelocityChangedListener.class );
    private VelocityChangedListener velocityChangedListenerProxy = (VelocityChangedListener) velocityChangedEventChannel.getListenerProxy();

    public static class LeftSystemEvent extends EventObject {
        private Photon photon;

        public LeftSystemEvent( Photon photon ) {
            super( photon );
            this.photon = photon;
        }

        public Photon getPhoton() {
            return photon;
        }
    }

    public interface LeftSystemEventListener extends EventListener {
        public void leftSystemEventOccurred( LeftSystemEvent event );
    }

    public void addLeftSystemListener( LeftSystemEventListener listener ) {
        leftSystemEventChannel.addListener( listener );
    }

    public void removeLeftSystemListener( LeftSystemEventListener listener ) {
        leftSystemEventChannel.removeListener( listener );
    }

//    void removeAllLeftSystemListeners() {
//        leftSystemEventChannel.removeAllListeners();
//    }


    public class VelocityChangedEvent extends EventObject {
        public VelocityChangedEvent() {
            super( Photon.this );
        }

        public MutableVector2D getVelocity() {
            return Photon.this.getVelocity();
        }
    }

    public interface VelocityChangedListener extends EventListener {
        public void velocityChanged( VelocityChangedEvent event );
    }

    public void addVelocityChangedListener( VelocityChangedListener listener ) {
        velocityChangedEventChannel.addListener( listener );
    }

    public void removeVelocityChangedListener( VelocityChangedListener listener ) {
        velocityChangedEventChannel.removeListener( listener );
    }
}
