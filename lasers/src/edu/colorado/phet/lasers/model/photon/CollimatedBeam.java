/**
 * Class: CollimatedBeam
 * Package: edu.colorado.phet.lasers.model.photon
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.photon;


import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.lasers.EventRegistry;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Random;

/**
 * A CollimatedBeam is a collection of photons that all have identical
 * velocities. The beam has a height, and the photons are randomly distributed
 * across that height.
 */
public class CollimatedBeam extends Particle {

    private static Random gaussianGenerator = new Random();

    private double nextTimeToProducePhoton = 0;
    private double wavelength;
    private Rectangle2D bounds;
    private Vector2D velocity;
    // The rate at which the beam produces photons
    private double timeSinceLastPhotonProduced = 0;
    // Used to deterimine when photons should be produced
    private double photonsPerSecond;
    // Is the collimated beam currently generating photons?
    private boolean isEnabled;


    public CollimatedBeam( double wavelength, Point2D origin, double height, double width, Vector2D direction ) {
        this.wavelength = wavelength;
        this.bounds = new Rectangle2D.Double( origin.getX(), origin.getY(), width, height );
        this.setPosition( origin );
        this.velocity = new Vector2D.Double( direction ).normalize().scale( Photon.s_speed );
    }

    ////////////////////////////////////////////////////////////////////////
    // Defined events and listeners
    private EventRegistry eventRegistry = new EventRegistry();

    public void addListener( EventListener listener ) {
        eventRegistry.addListener( listener );
    }

    public void removeListener( EventListener listener ) {
        eventRegistry.removeListener( listener );
    }

    public class RateChangeEvent extends EventObject {
        public RateChangeEvent() {
            super( CollimatedBeam.this );
        }

        public double getRate() {
            return CollimatedBeam.this.getPhotonsPerSecond();
        }
    }

    public interface RateChangeListener extends EventListener {
        public void rateChangeOccurred( RateChangeEvent event );
    }

    public class WavelengthChangeEvent extends EventObject {
        public WavelengthChangeEvent() {
            super( CollimatedBeam.this );
        }

        public double getWavelength() {
            return CollimatedBeam.this.getWavelength();
        }
    }

    public interface WavelengthChangeListener extends EventListener {
        public void wavelengthChangeOccurred( WavelengthChangeEvent event );
    }


    public void setBounds( Rectangle2D rect ) {
        this.bounds = rect;
        this.setPosition( new Point2D.Double( rect.getX(), rect.getY() ) );
    }

    public void setDirection( Vector2D.Double direction ) {
        this.velocity = new Vector2D.Double( direction ).normalize().scale( Photon.s_speed );
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
        eventRegistry.fireEvent( new RateChangeEvent() );
    }

    public void setWavelength( int wavelength ) {
        this.wavelength = wavelength;
        WavelengthChangeEvent event = new WavelengthChangeEvent();
        eventRegistry.fireEvent( event );
    }

    public double getWavelength() {
        return wavelength;
    }

    public void addPhoton() {
        final Photon newPhoton = Photon.create( this.getWavelength(),
                                                new Point2D.Double( genPositionX(), genPositionY() ),
                                                new Vector2D.Double( velocity ) );
        eventRegistry.fireEvent( new PhotonEmittedEvent( this, newPhoton ) );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Produce photons
        if( isEnabled() ) {
            timeSinceLastPhotonProduced += dt;
            if( nextTimeToProducePhoton < timeSinceLastPhotonProduced ) {
                timeSinceLastPhotonProduced = 0;
                final Photon newPhoton = Photon.create( this.getWavelength(),
                                                        new Point2D.Double( genPositionX(), genPositionY() ),
                                                        new Vector2D.Double( velocity ) );
                eventRegistry.fireEvent( new PhotonEmittedEvent( this, newPhoton ) );
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
        // Things are different if we're firing horizontally or vertically
        if( velocity.getX() != 0 ) {
            yDelta = Math.random() * bounds.getHeight();
        }
        return this.getPosition().getY() + yDelta;
    }

    private double genPositionX() {
        // Things are different if we're firing horizontally or vertically
        double xDelta = 0;
        if( velocity.getY() != 0 ) {
            xDelta = Math.random() * bounds.getWidth() - bounds.getWidth() / 2;
        }
        return this.getPosition().getX() + xDelta;
    }

    private double getNextTimeToProducePhoton() {
        double temp = ( gaussianGenerator.nextGaussian() + 1.0 );
        temp = 1;
        return temp / ( photonsPerSecond / 1000 );
    }
}

