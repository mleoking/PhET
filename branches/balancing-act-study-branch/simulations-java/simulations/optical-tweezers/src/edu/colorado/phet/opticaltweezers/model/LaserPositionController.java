// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * LaserPositionController controls the position of the laser in order to 
 * maintain constant trap force on the bead.  In the actual experiment, a 
 * feedback loop tells the equipment how much to move the laser in order 
 * to keep the trap force constant. We "Hollywood" this by moving the laser
 * proportionally to the change in the DNA strand's extension length.
 * <p>
 * Relationships:
 * <ul>
 * <li>moves the laser
 * <li>uses the change in the DNA strand's extension length to determine 
 * how much to move the laser
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LaserPositionController extends OTObservable implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_ENABLED = "enabled";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Laser _laser;
    private DNAStrand _dnaStrand;
    private boolean _enabled;
    private double _previousExtension; // nm
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param laser
     * @param dnaStrand
     */
    public LaserPositionController( Laser laser, DNAStrand dnaStrand ) {
        super();
        _laser = laser;
        _dnaStrand = dnaStrand;
        _enabled = false;
        _previousExtension = 0; // this will be set by setEnabled
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Enables and disables this object.
     * When enabled, the laser's position is controlled.
     * 
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            _previousExtension = _dnaStrand.getExtension(); // so that the laser doesn't move wildly
            notifyObservers( PROPERTY_ENABLED );
        }
    }
    
    /**
     * Is this object enabled?
     * 
     * @return
     */
    public boolean isEnabled() {
        return _enabled;
    }
    
    //----------------------------------------------------------------------------
    // Model for keeping laser trap force constant
    //----------------------------------------------------------------------------
    
    /*
     * If we're keeping the trap force constant, then move the laser
     * proportionally to the change in the strand's extension length.
     */
    private void moveLaser() {
        final double extension = _dnaStrand.getExtension();
        final double deltaX = extension - _previousExtension;
        _previousExtension = extension;
        Point2D laserPosition = _laser.getPositionReference();
        _laser.setPosition( laserPosition.getX() + deltaX, laserPosition.getY() );
    }

    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Each time the clock ticks, move the laser if this position control
     * is enabled and the laser has non-zero power.
     */
    public void stepInTime( double dt ) {
        if ( _enabled && _laser.isRunning() && _laser.getPower() > 0) {
            moveLaser();
        }
    }
}
