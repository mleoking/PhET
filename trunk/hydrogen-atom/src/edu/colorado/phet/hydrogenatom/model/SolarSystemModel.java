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

/**
 * SolarSystemModel models the hydrogen atom as a classical solar system.
 * <p>
 * Physical representation:
 * Proton at the center, electron spirals towards the proton.
 * (Our spiral is clockwise to be consistent with all other orbits in this sim.)
 * The electron starts at a fixed distance and random angle from the proton.
 * The radius of the spiral decreases linearly and the electron accelerates 
 * as the electron moves closer to the proton. 
 * The final state shows the electron on top of the proton.
 * In this final state, the atom is considered "destroyed".
 * <p>
 * Collision behavior:
 * The spiraling behavior should occur fast enough so that the atom is
 * destroyed before any photons or alpha particles reach it.  Therefore,
 * there are no collisions.
 * <p>
 * Absorption behavior:
 * Atom is destroyed, so it does not absorb photons or alpha particles.
 * <p>
 * Emission behavior:
 * Atom is destroyed, so it does not emit photons or alpha particles.
 */
public class SolarSystemModel extends AbstractHydrogenAtom {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ELECTRON_POSITION = "electronPosition";
    public static final String PROPERTY_DESTROYED = "destroyed";
    
    /* initial distance between electron and proton */
    private static final double ELECTRON_DISTANCE = 100;
    private static final double ELECTRON_ACCELERATION = 1.1;
    private static final double ELECTRON_ANGLE_DELTA = Math.toRadians( 10 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _electronAngle; // in radians
    private Point2D _electronPosition;
    private boolean _destroyed;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * @param position
     */
    public SolarSystemModel( Point2D position ) {
        super( position, 0 /* orientation */ );
        
        _electronAngle = Math.random() * Math.toRadians( 360 );
        _electronPosition = new Point2D.Double();
        _destroyed = false;
        
        double x = getX() + ( ELECTRON_DISTANCE * Math.cos( _electronAngle ) );
        double y = getY() + ( ELECTRON_DISTANCE * Math.sin( _electronAngle ) );
        _electronPosition.setLocation( x, y );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Has the atom been destroyed?
     * The atom is destroyed when the electron has completed its spiral into the proton.
     * @return true or false
     */
    public boolean isDestroyed() {
        return _destroyed;
    }
    
    /**
     * Gets the absolute electron position.
     * @return Point2D
     */
    public Point2D getElectronPosition() {
        return _electronPosition;
    }
    
    /**
     * Gets the electron's position relative to the local coordinate system of the atom.
     * The proton is at the origin (0,0) of the atom's local coordinate system.
     * @return Point2D
     */
    public Point2D getRelativeElectronPosition() {
        Point2D atomPosition = getPosition();
        double x = _electronPosition.getX() - atomPosition.getX();
        double y = _electronPosition.getY() - atomPosition.getY();
        return new Point2D.Double( x, y );
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        if ( !_destroyed ) {
            _electronAngle += ELECTRON_ANGLE_DELTA;
            double x = getX() + ( ELECTRON_DISTANCE * Math.cos( _electronAngle ) );
            double y = getY() + ( ELECTRON_DISTANCE * Math.sin( _electronAngle ) );
            _electronPosition.setLocation( x, y );
            notifyObservers( PROPERTY_ELECTRON_POSITION );
        }
    }
}
