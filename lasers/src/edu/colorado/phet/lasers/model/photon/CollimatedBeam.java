/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.photon;


import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.util.EventChannel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.Random;

/**
 * Class: CollimatedBeam
 * Package: edu.colorado.phet.lasers.model.photon
 * Author: Another Guy
 * Date: Mar 21, 2003
 * <p/>
 * A CollimatedBeam is a collection of photons that all have identical
 * velocities. The beam has a height, and the photons are randomly distributed
 * across that height.
 */
public class CollimatedBeam extends Particle implements PhotonSource {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    private static Random gaussianGenerator = new Random();
    private static Random angleGenerator = new Random();

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------
    private double nextTimeToProducePhoton = 0;
    private double wavelength;
    private Rectangle2D bounds;
    private Vector2D velocity;
    // The rate at which the beam produces photons
    private double timeSinceLastPhotonProduced = 0;
    // Used to deterimine when photons should be produced
    private double photonsPerSecond;
    // Maximum photon rate
    private double maxPhotonsPerSecond;
    // Is the collimated beam currently generating photons?
    private boolean isEnabled;
    // Angle at which the beam fans out, in radians
    private double fanout = 0;


    /**
     * @param wavelength
     * @param origin
     * @param height
     * @param width
     * @param direction
     * @param maxPhotonsPerSecond
     * @param fanout              spread of beam, in degrees
     */
    public CollimatedBeam( double wavelength, Point2D origin, double height, double width,
                           Vector2D direction, double maxPhotonsPerSecond, double fanout ) {
        this.fanout = Math.toRadians( fanout );
        this.wavelength = wavelength;
        this.maxPhotonsPerSecond = maxPhotonsPerSecond;
        this.bounds = new Rectangle2D.Double( origin.getX(), origin.getY(), width, height );
        this.setPosition( origin );
        this.velocity = new Vector2D.Double( direction ).normalize().scale( Photon.SPEED );
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    /**
     * @return fanout in degrees
     */
    public double getFanout() {
        return Math.toDegrees( fanout );
    }

    /**
     * @param fanout in degrees
     */
    public void setFanout( double fanout ) {
        this.fanout = Math.toRadians( fanout );
    }

    public void setBounds( Rectangle2D rect ) {
        this.bounds = rect;
        this.setPosition( new Point2D.Double( rect.getX(), rect.getY() ) );
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public void setDirection( Vector2D.Double direction ) {
        this.velocity = new Vector2D.Double( direction ).normalize().scale( Photon.SPEED );
    }

    public double getAngle() {
        return velocity.getAngle();
    }

    public double getHeight() {
        return bounds.getHeight();
    }

    public void setHeight( double height ) {
        this.bounds.setRect( bounds.getX(), bounds.getY(), bounds.getWidth(), height );
    }

    public double getWidth() {
        return bounds.getHeight();
    }

    public void setWidth( double width ) {
        this.bounds.setRect( bounds.getX(), bounds.getY(), width, bounds.getHeight() );
    }

    public double getPhotonsPerSecond() {
        return photonsPerSecond;
    }

    public void setPhotonsPerSecond( double photonsPerSecond ) {
        // The following if statement prevents the system from sending out a big
        // wave of photons if it has been set at a rate of 0 for awhile.
        if( this.photonsPerSecond == 0 ) {
            timeSinceLastPhotonProduced = 0;
        }
        this.photonsPerSecond = photonsPerSecond;
        nextTimeToProducePhoton = getNextTimeToProducePhoton();
        rateChangeListenerProxy.rateChangeOccurred( new RateChangeEvent( this ) );
    }

    public void setWavelength( double wavelength ) {
        this.wavelength = wavelength;
        wavelengthChangeListenerProxy.wavelengthChanged( new WavelengthChangeEvent( this ) );
    }

    public double getWavelength() {
        return wavelength;
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Produce photons
        if( isEnabled() ) {
            timeSinceLastPhotonProduced += dt;
            if( nextTimeToProducePhoton < timeSinceLastPhotonProduced ) {
                timeSinceLastPhotonProduced = 0;
                // Set the photon's velocity to a randomized angle
                double angle = angleGenerator.nextDouble() * ( fanout / 2 ) * ( angleGenerator.nextBoolean() ? 1 : -1 );
                Vector2D photonVelocity = new Vector2D.Double( velocity ).rotate( angle );
                final Photon newPhoton = Photon.create( this.getWavelength(),
                                                        new Point2D.Double( genPositionX(), genPositionY() ),
                                                        photonVelocity );
                photonEmittedListenerProxy.photonEmittedEventOccurred( new PhotonEmittedEvent( this, newPhoton ) );
                nextTimeToProducePhoton = getNextTimeToProducePhoton();
            }
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled( boolean enabled ) {
        isEnabled = enabled;
        timeSinceLastPhotonProduced = 0;
    }

    private double genPositionY() {
        double yDelta = 0;
        // Things are different if we're firing horizontally
        if( velocity.getX() > velocity.getY() ) {
            yDelta = Math.random() * bounds.getHeight();
        }
        return this.getBounds().getMinY() + yDelta;
    }

    private double genPositionX() {
        double xDelta = 0;
        // Things are different if we're firing vertically
        if( velocity.getY() > velocity.getX() ) {
            xDelta = Math.random() * bounds.getWidth();
        }
        return this.getBounds().getMinX() + xDelta;
    }

    private double getNextTimeToProducePhoton() {
        double temp = ( gaussianGenerator.nextGaussian() + 1.0 );
        temp = 1;
        return temp / ( photonsPerSecond / 1000 );
    }

    public double getMaxPhotonsPerSecond() {
        return this.maxPhotonsPerSecond;
    }

    public void setMaxPhotonsPerSecond( int maxPhotonsPerSecond ) {
        this.maxPhotonsPerSecond = maxPhotonsPerSecond;
    }

    //---------------------------------------------------------------------
    // Event Handling
    //---------------------------------------------------------------------

    private EventChannel rateChangeEventChannel = new EventChannel( RateChangeListener.class );
    private RateChangeListener rateChangeListenerProxy = (RateChangeListener)rateChangeEventChannel.getListenerProxy();

    private EventChannel wavelengthChangeEventChannel = new EventChannel( WavelengthChangeListener.class );
    private WavelengthChangeListener wavelengthChangeListenerProxy = (WavelengthChangeListener)wavelengthChangeEventChannel.getListenerProxy();

    private EventChannel photonEmittedEventChannel = new EventChannel( PhotonEmittedListener.class );
    private PhotonEmittedListener photonEmittedListenerProxy = (PhotonEmittedListener)photonEmittedEventChannel.getListenerProxy();

    public void addRateChangeListener( RateChangeListener rateChangeListener ) {
        rateChangeEventChannel.addListener( rateChangeListener );
    }

    public void addWavelengthChangeListener( WavelengthChangeListener wavelengthChangeListener ) {
        wavelengthChangeEventChannel.addListener( wavelengthChangeListener );
    }

    public void addPhotonEmittedListener( PhotonEmittedListener photonEmittedListener ) {
        photonEmittedEventChannel.addListener( photonEmittedListener );
    }

    public void removeListener( EventListener listener ) {
        rateChangeEventChannel.removeListener( listener );
    }
}

