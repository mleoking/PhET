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
import edu.colorado.phet.hydrogenatom.view.atom.DeBroglieHeight3DNode;

/**
 * DeBroglieModel is the deBroglie model of a hydrogen atom.
 * It is identical to the Bohr model, but has different visual representations.
 * The different visual representations mean that it also has different
 * methods of handling collision detection and determining electron position.
 * <p>
 * NOTE: The algorithms for collision detection and calculation
 * of electron position differ greatly for 2D and 3D views. 
 * Therefore this model needs to know something about the view in order
 * to make things look right in 3D.  So this model cannot be shown in
 * both 2D and 3D views simultaneously.  There are undubtedly ways to
 * do this, but this simulation does not require multiple simultaneous
 * views of the model.
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
    // Private class data
    //----------------------------------------------------------------------------
    
    // DO NOT CHANGE !!!
    private static final double ORBIT_Y_SCALE = DeBroglieHeight3DNode.ORBIT_Y_SCALE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DeBroglieView _view; // see setView javadoc
    
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
     * Uses different algorithms depending on whether the view is 2D or 3D.
     */
    protected boolean collides( Photon photon ) {
        if ( _view == DeBroglieView.HEIGHT_3D ) {
            return collides3D( photon );
        }
        else {
            return collides2D( photon );
        }
    }
    
    /*
     * Determines whether a photon collides with this atom in the 3D view.
     * In this case, the photon collides with the atom if it
     * hits the ellipse that is the 2D projection of the electron's 3D orbit.
     * 
     * @param photon
     * @return true or false
     */
    private boolean collides3D( Photon photon ) {
       
        // position of photon relative to atom's center
        double x = photon.getX() - getX();
        double y = photon.getY() - getY();
        double angle = Math.atan( y / x );
        
        // position on orbit at corresponding angle
        double orbitRadius = getElectronOrbitRadius();
        double orbitX = orbitRadius * Math.cos( angle );
        double orbitY = ORBIT_Y_SCALE * orbitRadius * Math.sin( angle );
        
        // distance from electron to closest point on orbit
        double distance = Point2D.distance( x, y, orbitX, orbitY );
        
        // how close the photon's center must be to a point on the electron's orbit
        double closeness = COLLISION_CLOSENESS + ( DeBroglieBrightnessNode.RING_WIDTH / 2 );
        
        boolean collides = ( distance <= closeness );
        return collides;
    }
    
    /*
     * Determines whether a photon collides with this atom in one of the 2D views.
     * In all 2D views (including "radial distance"), the photon collides with
     * the atom if it hits the ring used to represent the standing wave in one
     * of the brightness views.
     * 
     * @param photon
     * @return true or false
     */
    private boolean collides2D( Photon photon ) {
        
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
     * For deBroglie, the change in angle (and thus the oscillation frequency)
     * is the same for all states of the electron.
     * 
     * @param dt
     * @return double
     */
    protected double calculateNewElectronAngle( double dt ) {
        double deltaAngle = dt * getElectronAngleDelta();
        return getElectronAngle() - deltaAngle; // clockwise
    }
    
    /**
     * Gets the electron's position in world coordinates.
     * This is the electron's offset adjusted by the atom's position.
     * If we're using a 3D view, adjust the y coordinate to account 
     * for the 3D perspective.
     */
    protected Point2D getElectronPosition() {
        Point2D p = super.getElectronPosition();
        if ( _view == DeBroglieView.HEIGHT_3D ) {
            double x = p.getX();
            double y = getY() + ( ( p.getY() - getY() ) * ORBIT_Y_SCALE ); // adjust distance from atom's center
            p.setLocation( x, y );
        }
        return p;
    }
}
