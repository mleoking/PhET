/**
 * Class: CollimatedBeam
 * Package: edu.colorado.phet.lasers.physics.photon
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.physics.photon;


import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.lasers.physics.LaserModel;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.LinkedList;

/**
 * A CollimatedBeam is a collection of photons that all have identical
 * velocities. The beam has a height, and the photons are randomly distributed
 * across that height.
 */
public class CollimatedBeam extends Particle {

    private int wavelength;
    private Point2D origin;
    private double height;
    private double width;
    private Vector2D velocity;
    private ArrayList photons = new ArrayList();
    // The rate at which the beam produces photons
    private float timeSinceLastPhotonProduced = 0;
    // Used to deterimine when photons should be produced
    private float photonsPerSecond = 30;
    // Is the collimated beam currently generating photons?
    private boolean isActive;
    private LaserModel model;
    private LinkedList listeners = new LinkedList();

    public interface Listener {
        void photonCreated( CollimatedBeam beam, Photon photon );
    }

    /**
     *
     * @param wavelength
     * @param origin
     * @param height
     * @param width
     */
    public CollimatedBeam( LaserModel model, int wavelength, Point2D origin, double height, double width, Vector2D direction ) {
        this.model = model;
        this.wavelength = wavelength;
        this.origin = origin;
        this.height = height;
        this.width = width;
        this.velocity = new Vector2D.Double( direction ).normalize().scale( Photon.s_speed );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public Point2D getOrigin() {
        return origin;
    }

    public void setOrigin( Point2D origin ) {
        this.origin = origin;
    }

    /**
     *
     * @return
     */
    public double getHeight() {
        return height;
    }

    /**
     *
     * @param height
     */
    public void setHeight( double height ) {
        this.height = height;
    }

    /**
     *
     * @return
     */
    public double getWidth() {
        return width;
    }

    /**
     *
     * @param width
     */
    public void setWidth( float width ) {
        this.width = width;
    }

    /**
     *
     * @return
     */
    public float getPhotonsPerSecond() {
        return photonsPerSecond;
    }

    /**
     *
     * @param photonsPerSecond
     */
    public void setPhotonsPerSecond( float photonsPerSecond ) {

        // The following if statement prevents the system from sending out a big
        // wave of photons if it has been set at a rate of 0 for awhile.
        if( this.photonsPerSecond == 0 ) {
            timeSinceLastPhotonProduced = 0;
        }
        this.photonsPerSecond = photonsPerSecond;
        nextTimeToProducePhoton = getNextTimeToProducePhoton();
    }

    /**
     *
     * @return
     */
    public int getWavelength() {
        return wavelength;
    }

    /**
     *
     */
    public void addPhoton() {
        Photon newPhoton = Photon.create( this );
        newPhoton.setPosition( genPositionX(), genPositionY() + newPhoton.getRadius() );
        newPhoton.setVelocity( new Vector2D.Double( velocity ) );
        newPhoton.setWavelength( this.wavelength );
        model.addModelElement( newPhoton );
//        new AddParticleCmd( newPhoton ).doIt();
        photons.add( newPhoton );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.photonCreated( this, newPhoton );
        }
    }

    /**
     *
     * @param photon
     */
    public void removePhoton( Photon photon ) {
        photons.remove( photon );
    }

    /**
     *
     */
    private float nextTimeToProducePhoton = 0;

    public void stepInTime( float dt ) {

        super.stepInTime( dt );

        // Produce photons
        if( isActive() ) {
            timeSinceLastPhotonProduced += dt;
            int numPhotons = (int)( photonsPerSecond * timeSinceLastPhotonProduced );
//            for( int i = 0; i < numPhotons; i++ ) {
            if( nextTimeToProducePhoton < timeSinceLastPhotonProduced ) {
                timeSinceLastPhotonProduced = 0;
                this.addPhoton();
                nextTimeToProducePhoton = getNextTimeToProducePhoton();
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     *
     * @param active
     */
    public void setActive( boolean active ) {
        isActive = active;
        timeSinceLastPhotonProduced = 0;
    }

    /**
     *
     * @return
     */
    private double genPositionY() {
        double yDelta = velocity.getX() != 0 ? (float)Math.random() * height : 0;
        return this.getPosition().getY() + yDelta;
    }

    /**
     *
     * @return
     */
    private double genPositionX() {
        double xDelta = velocity.getY() != 0 ? (float)Math.random() * width : 0;
        return this.getPosition().getX() + xDelta;
    }

    private Random gaussianGenerator = new Random();
    private float getNextTimeToProducePhoton() {
        double temp = ( gaussianGenerator.nextGaussian() + 1.0 );
        return (float)temp / photonsPerSecond;
    }

}
