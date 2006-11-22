/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieBrightnessNode;
import edu.colorado.phet.hydrogenatom.view.particle.PhotonNode;

/**
 * DeBroglieModel is the deBroglie model of a hydrogen atom.
 * It is identical to the Bohr model, but has a different visual representation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieModel extends BohrModel {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_VIEW = "view";
    
    public static DeBroglieView DEFAULT_VIEW = DeBroglieView.BRIGHTNESS_MAGNITUDE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DeBroglieView _view;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeBroglieModel( Point2D position ) {
        super( position );
        _view = DEFAULT_VIEW;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the type of view.
     * This really shouldn't be in the model, but in our current architecture,
     * the only way to pass information to view components is via the model.
     * 
     * @param view
     */
    public void setView( DeBroglieView view ) {
        if ( view != _view ) {
            _view = view;
            notifyObservers( PROPERTY_VIEW );
        }
    }
    
    /**
     * Gets the type of view.
     * @return DeBroglieView
     */
    public DeBroglieView getView() {
        return _view;
    }
    
    /**
     * Gets the amplitude of a standing wave at some angle,
     * in some specified state of the electron.
     * 
     * @param angle
     * @param state
     * @return -1 <= amplitude <= 1
     */
    public double getAmplitude( double angle, int state ) {
        assert( state >= getGroundState() && state <= getGroundState() + getNumberOfStates() - 1 );
        double electronAngle = getElectronAngle();
        double amplitude = Math.sin( state * angle ) * Math.sin( electronAngle );
        assert( amplitude >= -1 && amplitude <= 1 );
        return amplitude;
    }
    
    /**
     * Gets the amplitude of a standing wave at some angle,
     * in the electron's current state.
     * 
     * @param angle
     * @return
     */
    public double getAmplitude( double angle ) {
        return getAmplitude( angle, getElectronState() );
    }
    
    //----------------------------------------------------------------------------
    // BohrModel overrides
    //----------------------------------------------------------------------------
    
    /**
     * Determines whether a photon collides with this atom.
     * In this case, the photon collides with the atom if it
     * hits the ring used to represent the standing wave.
     * 
     * @param photon
     * @return true or false
     */
    protected boolean collides( Photon photon ) {
        
        // position of photon relative to atom's center
        double x = photon.getX() - getX();
        double y = photon.getY() - getY();
        
        // distance of photon and electron from atom's center
        double photonRadius = Math.sqrt( ( x * x ) + ( y * y ) );
        double orbitRadius = getElectronOrbitRadius();
        
        // how close the photon's center must be to a point on the electron's orbit
        double closeness = COLLISION_CLOSENESS + ( DeBroglieBrightnessNode.RING_WIDTH / 2 );
        
        boolean collides = ( Math.abs( photonRadius - orbitRadius ) <= closeness );
        return collides;
    }
    
    /**
     * Calculates the new electron angle for some time step.
     * In this case, the change in angle (and thus the oscillation frequency)
     * is the same of all states of the electron.
     * 
     * @param dt
     * @return double
     */
    protected double calculateNewElectronAngle( double dt ) {
        double deltaAngle = dt * getElectronAngleDelta();
        return getElectronAngle() - deltaAngle; // clockwise
    }
}
