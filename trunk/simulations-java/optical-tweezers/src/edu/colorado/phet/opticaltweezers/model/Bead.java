/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.model.ModelElement;

/**
 * Bead is the model of a glass bead, the dialectric particle in this experiement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Bead extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_DIAMETER = "diameter";
    
    // Brownian motion scaling factor, bigger values cause bigger motion
    public static final double BROWNIAN_MOTION_SCALE = 1;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _diameter; // nm
    private final double _density; // g/nm^3
    private Fluid _fluid;
    private Laser _laser;
    private Random _stepAngleRandom;
    private boolean _motionEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Bead( Point2D position, double orientation, double diameter, double density, Fluid fluid, Laser laser ) {
        super( position, orientation, 0 /* speed */ );
        if ( diameter <= 0 ) {
            throw new IllegalArgumentException( "diameter must be > 0: " + diameter );
        }
        if ( density <= 0 ) {
            throw new IllegalArgumentException( "density must be > 0: " + density );   
        }
        _diameter = diameter;
        _density = density;
        _fluid = fluid;
        _laser = laser;
        _stepAngleRandom = new Random();
        _motionEnabled = true;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the diameter.
     * 
     * @param diameter diameter in nm
     */
    public void setDiameter( double diameter ) {
        if ( diameter <= 0 ) {
            throw new IllegalArgumentException( "diameter must be > 0" );
        }
        if ( diameter != _diameter ) {
            _diameter = diameter;
            notifyObservers( PROPERTY_DIAMETER );
        }
    }
    
    /**
     * Gets the diameter.
     * 
     * @return diameter in nm
     */
    public double getDiameter() {
        return _diameter;
    }
    
    /**
     * Gets the mass.
     * 
     * @return mass, in grams (g)
     */
    public double getMass() {
        double radius = ( _diameter / 2 );
        double volume = ( 4. / 3. ) * Math.PI * ( radius * radius * radius );
        return volume * _density;
    }
    
    /**
     * Enables and disables motion of the model, used when something else 
     * is determining the bead's position (eg, when the user is dragging the bead).
     * 
     * @param motionEnabled
     */
    public void setMotionEnabled( boolean motionEnabled ) {
        _motionEnabled = motionEnabled;    
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        if ( _motionEnabled ) {
            moveBead( dt );
        }
    }
    
    //----------------------------------------------------------------------------
    // Motion model
    //----------------------------------------------------------------------------
    
    private void moveBead( double dt ) {
        
        // Top and bottom edges of microscope slide
        final double minY = _fluid.getMinY() + ( getDiameter() / 2 );
        final double maxY = _fluid.getMaxY() - ( getDiameter() / 2 );
        
        // Brownian motion components
        double dxBrownian = 0;
        double dyBrownian = 0;
        {
            final double fluidTemperature = _fluid.getTemperature();
            final double stepLength = BROWNIAN_MOTION_SCALE * Math.sqrt( fluidTemperature ) * Math.sqrt( dt );
            double stepAngle = 0;
            if ( getY() <= minY ) {
                // bounce off top edge of microscope slide at an angle between 45 and 135 degrees
                stepAngle = ( Math.PI / 4 ) + ( _stepAngleRandom.nextDouble() * Math.PI / 2 );
            }
            else if ( getY() >= maxY ) {
                // bounce off bottom edge of microscope slide at an angle between -45 and -135 degrees
                stepAngle = ( Math.PI + ( Math.PI / 4 ) ) + ( _stepAngleRandom.nextDouble() * Math.PI / 2 );
            }
            else {
                // no collision with the edges of the microscope slide, any random angle will do
                stepAngle = _stepAngleRandom.nextDouble() * ( 2 * Math.PI );
            }
            // covert from Polar to Cartesian coordinates
            dxBrownian = stepLength * Math.cos( stepAngle );
            dyBrownian = stepLength * Math.sin( stepAngle );
        }
        
        // Combine all motion components
        final double newX = getX() + dxBrownian;
        double newY = getY() + dyBrownian;
        
        // Collision detection
        if ( newY < minY ) {
            // collide with top edge of microscope slide
            newY = minY;
        }
        else if ( newY > maxY ) {
            // collide with bottom edge of microscope slide
            newY = maxY;
        }
        
        setPosition( newX, newY );
    }
}
