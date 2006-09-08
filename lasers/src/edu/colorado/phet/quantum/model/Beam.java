/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.model;


import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.Random;

/**
 * Beam
 * <p/>
 * A PhotonSource of photons that all have identical speeds. Their directions can
 * vary by a specified fanout angle.
 * The beam has a beamWidth, and the photons are randomly distributed across that beamWidth.
 * It's position is at the midpoint of that beamWidth.
 * <p>
 * The beam has a derived attribute called intensity. This represents the fraction of maximum energy the
 * beam can produce, based on its maximum photons per second and the minimum wavelength,
 * or highest frequency, light it can produce. Its maximum value is the maximum photons per second.
 */
public class Beam extends Particle implements PhotonSource {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------
    private static Random gaussianGenerator = new Random();
    private static Random startPositionGenerator = new Random();

    //-----------------------------------------------------------------
    // Instance data
    //-----------------------------------------------------------------
    private double nextTimeToProducePhoton = 0;
    private double wavelength;
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
    // Flag to determine if photon production is Gaussian or fixed
    private boolean isPhotonProductionGaussian = false;
    private double length;
    private double beamWidth;


    /**
     * @param wavelength
     * @param origin
     * @param length
     * @param beamWidth
     * @param direction
     * @param maxPhotonsPerSecond
     * @param fanout              spread of beam, in radians
     */
    public Beam( double wavelength, Point2D origin, double length, double beamWidth,
                 Vector2D direction, double maxPhotonsPerSecond, double fanout ) {
        this.fanout = fanout;
        this.wavelength = wavelength;
        this.maxPhotonsPerSecond = maxPhotonsPerSecond;
        this.setPosition( origin );
        this.velocity = new Vector2D.Double( direction ).normalize().scale( Photon.SPEED );
        this.length = length;
        this.beamWidth = beamWidth;
    }

    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    /**
     * @return fanout radians
     */
    public double getFanout() {
        return fanout;
    }

    /**
     * @param fanout in radians
     */
    public void setFanout( double fanout ) {
        this.fanout = fanout;
    }

    /**
     * Returns a shape that bounds the beam
     *
     * @return a Shape that bounds the beam
     */
    public Shape getBounds() {
        double alpha = getFanout() / 2;
        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( getPosition() );
        path.lineToRelative( 0, -getBeamWidth() / 2 );
        path.lineToRelative( getLength() * Math.cos( alpha ), -getLength() * Math.sin( alpha ) / 2 );
        path.lineToRelative( 0, getBeamWidth() + getLength() * Math.sin( alpha ) );
        path.lineToRelative( -getLength() * Math.cos( alpha ), -getLength() * Math.sin( alpha ) / 2 );
        path.lineTo( getPosition() );
        AffineTransform atx = AffineTransform.getRotateInstance( getDirection(), getPosition().getX(), getPosition().getY() );
        Shape shape = path.getGeneralPath().createTransformedShape( atx );
        return shape;
    }

    public void setDirection( Vector2D.Double direction ) {
        this.velocity = new Vector2D.Double( direction ).normalize().scale( Photon.SPEED );
    }

    public double getDirection() {
        return velocity.getAngle();
    }

    public void setBeamWidth( double beamWidth ) {
        this.beamWidth = beamWidth;
    }

    public double getBeamWidth() {
        return beamWidth;
    }

    public double getLength() {
        return length;
    }

    public void setLength( double length ) {
        this.length = length;
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

    /**
     *
     * @param intensity
     * @param maxWavelength
     */
    public void setIntensity( double intensity, double maxWavelength ) {
//        double wavelengthRange = maxWavelength - minWavelength;
//        double middleWavelength = wavelengthRange / 2 + minWavelength;
//        double photonRate = intensity * ( 1 + ( ( getWavelength() - middleWavelength ) / wavelengthRange ));
//        setPhotonsPerSecond( photonRate );
//
        setPhotonsPerSecond( intensity );         
//        setPhotonsPerSecond( intensity * intensityToRateFactor( maxWavelength ));
    }

    public double getIntensity( double maxWavelength ) {
//        double wavelengthRange = maxWavelength - minWavelength;
//        double middleWavelength = wavelengthRange / 2 + minWavelength;
//        double intensity = photonsPerSecond / ( 1 + ( ( getWavelength() - middleWavelength ) / wavelengthRange ) );
//        return intensity;

        if( true ) return photonsPerSecond;
        return photonsPerSecond * rateToIntensityFactor( maxWavelength );
    }

    private double rateToIntensityFactor(double minWavelength ) {
        return 1 / intensityToRateFactor( minWavelength );
    }

    private double intensityToRateFactor( double maxWavelength ) {
//        double wavelengthRange = maxWavelength - minWavelength;
//        double middleWavelength = wavelengthRange / 2 + minWavelength;
//        double intensityFactor = 1 + ( ( getWavelength() - middleWavelength ) / wavelengthRange );
        double intensityFactor = getWavelength() / maxWavelength;
        return intensityFactor;
    }

    public double getMaxPhotonsPerSecond() {
        return this.maxPhotonsPerSecond;
    }

    public double getMaxIntensity( double minWavelength, double maxWavelength ) {
        return getMaxPhotonsPerSecond();
//        return maxPhotonsPerSecond * rateToIntensityFactor( minWavelength, maxWavelength ) ;
    }

    public void setMaxPhotonsPerSecond( int maxPhotonsPerSecond ) {
        this.maxPhotonsPerSecond = maxPhotonsPerSecond;
    }

    public void setWavelength( double wavelength ) {
        this.wavelength = wavelength;
        wavelengthChangeListenerProxy.wavelengthChanged( new WavelengthChangeEvent( this ) );
    }

    public double getWavelength() {
        return wavelength;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled( boolean enabled ) {
        isEnabled = enabled;
        timeSinceLastPhotonProduced = 0;
    }

    private Point2D genPosition() {
        double r = startPositionGenerator.nextDouble();
        double inset = 10;  // inset from the edges of the "beam" that photons are emitted
        double d = r * ( ( getBeamWidth() - inset ) / 2 ) * ( startPositionGenerator.nextBoolean() ? 1 : -1 );
        double dx = d * Math.sin( getDirection() );
        double dy = -d * Math.cos( getDirection() );
        return new Point2D.Double( getPosition().getX() + dx, getPosition().getY() + dy );
    }

    //----------------------------------------------------------------
    // Time-dependent behavior
    //----------------------------------------------------------------

    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Produce photons
        if( isEnabled() ) {
            timeSinceLastPhotonProduced += dt;
            if( nextTimeToProducePhoton < timeSinceLastPhotonProduced ) {

                int nPhotons = (int)( timeSinceLastPhotonProduced * getPhotonsPerSecond() / 1E3 );
                for( int i = 0; i < nPhotons; i++ ) {
                    // Set the photon's velocity to a fanout angle proportional to its distance from the
                    // center of the beam
                    Point2D photonLoc = genPosition();
                    double angle = ( photonLoc.distance( getPosition() ) / getBeamWidth() / 2 ) * getFanout();
                    double alpha = getDirection()
                                   - Math.atan2( photonLoc.getY() - getPosition().getY(),
                                                 photonLoc.getX() - getPosition().getX() );
                    if( alpha > 0 ) {
                        angle *= -1;
                    }
                    Vector2D photonVelocity = new Vector2D.Double( velocity ).rotate( angle );
                    final Photon newPhoton = Photon.create( this.getWavelength(),
                                                            photonLoc,
                                                            photonVelocity );
                    photonEmittedListenerProxy.photonEmitted( new PhotonEmittedEvent( this, newPhoton ) );
                }
                nextTimeToProducePhoton = getNextTimeToProducePhoton();
                timeSinceLastPhotonProduced = 0;
            }
        }
    }

    private double getNextTimeToProducePhoton() {
        double temp = isPhotonProductionGaussian ? ( gaussianGenerator.nextGaussian() + 1.0 )
                      : 1;
        return temp / ( photonsPerSecond / 1000 );
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

