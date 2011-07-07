// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.hydrogenatom.event.PhotonListener;
import edu.colorado.phet.hydrogenatom.view.particle.ElectronNode;
import edu.colorado.phet.hydrogenatom.view.particle.PhotonNode;

/**
 * AbstractHydrogenAtom is the base class for all hydrogen atom models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractHydrogenAtom extends FixedObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    /* how close a photon and electron (treated as points) must be for them to collide */
    public static double COLLISION_CLOSENESS = ( PhotonNode.DIAMETER / 2 ) + ( ElectronNode.DIAMETER / 2 );
    
    public static final String PROPERTY_ELECTRON_STATE = "electronState";
    public static final String PROPERTY_ELECTRON_OFFSET = "electronOffset";
    public static final String PROPERTY_ATOM_DESTROYED = "atomDestroyed";
    public static final String PROPERTY_ATOM_IONIZED = "atomIonized";
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    /* Ground state */
    protected static final int GROUND_STATE = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ArrayList<PhotonListener> _photonListeners;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param position
     * @param orientation
     */
    public AbstractHydrogenAtom( Point2D position, double orientation ) {
        super( position, orientation );
        _photonListeners = new ArrayList<PhotonListener>();
    }

    public void cleanup() {
        removeAllPhotonListeners();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the ground state.
     * The notion of "ground state" does not apply to all
     * hydrogen atom models, but it is convenient to have it here.
     * 
     * @return int
     */
    public static int getGroundState() {
        return GROUND_STATE;
    }
    
    /**
     * Gets the number of electron states that the model supports.
     * The default is zero, since some models have no notion of "state".
     * @return int
     */
    public static int getNumberOfStates() {
        return 0;
    }
    
    /**
     * Gets the transition wavelengths for a specified state.
     * The default implementation returns null.
     * The notion of "transition wavelength" does not apply to all
     * hydrogen atom models, but it is convenient to have it here.
     * 
     * @param state
     * @return double[]
     */
    public double[] getTransitionWavelengths( int state ) {
        return null;
    }
    
    //----------------------------------------------------------------------------
    // Particle motion
    //----------------------------------------------------------------------------

    /**
     * Moves a photon.
     * In the default implementation, the atom has no influence on the photon's movement.
     * 
     * @param photon
     * @param dt
     */
    public void movePhoton( Photon photon, double dt ) {
        double speed = photon.getSpeed();
        double distance = speed * dt;
        double direction = photon.getOrientation();
        double dx = Math.cos( direction ) * distance;
        double dy = Math.sin( direction ) * distance;
        double x = photon.getX() + dx;
        double y = photon.getY() + dy;
        photon.setPosition( x, y );
    }

    /**
     * Moves an alpha particle.
     * In the default implementation, the atom has no influence on the alpha particle's movement.
     * 
     * @param alphaParticle
     * @param dt
     */
    public void moveAlphaParticle( AlphaParticle alphaParticle, double dt ) {
        double speed = alphaParticle.getSpeed();
        double distance = speed * dt;
        double direction = alphaParticle.getOrientation();
        double dx = Math.cos( direction ) * distance;
        double dy = Math.sin( direction ) * distance;
        double x = alphaParticle.getX() + dx;
        double y = alphaParticle.getY() + dy;
        alphaParticle.setPosition( x, y );
    }
    
    /**
     * Determines if two points collide.
     * Any distance between the points that is <= threshold
     * is considered a collision.
     * 
     * @param p1
     * @param p2
     * @param threshold
     * @return true or false
     */
    protected static boolean pointsCollide( Point2D p1, Point2D p2, double threshold ) {
        return p1.distance( p2 ) <= threshold;
    }

    //----------------------------------------------------------------------------
    // ModelElement default implementation
    //----------------------------------------------------------------------------

    /**
     * Called when time has advanced by some delta.
     * The default implementation does nothing.
     */
    public void stepInTime( double dt ) {}
    
    //----------------------------------------------------------------------------
    // PhotonListener
    //----------------------------------------------------------------------------

    public void addPhotonListener( PhotonListener listener ) {
        _photonListeners.add( listener );
    }

    public void removePhotonListener( PhotonListener listener ) {
        _photonListeners.remove( listener );
    }

    public void removeAllPhotonListeners() {
        _photonListeners.clear();
    }

    // Fires when a photon is absorbed.
    protected void firePhotonAbsorbedEvent( Photon photon ) {
        for ( PhotonListener listener : new ArrayList<PhotonListener>( _photonListeners ) ) {
            listener.photonAbsorbed( photon );
        }
    }

    // Fires when a photon is emitted.
    protected void firePhotonEmittedEvent( Photon photon ) {
        for ( PhotonListener listener : new ArrayList<PhotonListener>( _photonListeners ) ) {
            listener.photonEmitted( photon );
        }
    }
}
