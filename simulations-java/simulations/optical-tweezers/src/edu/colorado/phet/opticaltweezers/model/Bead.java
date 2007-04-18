/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.opticaltweezers.util.Vector2D;

/**
 * Bead is the model of a glass bead, the dialectric particle in this experiement.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Bead extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_DIAMETER = "diameter";
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    private static final boolean MOTION_DEBUG_OUTPUT = false;
    
    // Brownian motion scaling factor, bigger values cause bigger motion
    private static final double BROWNIAN_MOTION_SCALE = 2;
    
    private static final boolean MOTION_TRAP_FORCE_COMPONENT_ENABLED = false;
    private static final boolean MOTION_FLUID_COMPONENT_ENABLED = false;
    private static final boolean MOTION_BROWNIAN_COMPONENT_ENABLED = true;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _diameter; // nm
    private final double _density; // kg/nm^3
    private Fluid _fluid;
    private Laser _laser;
    private Random _stepAngleRandom;
    private boolean _motionEnabled;
    private Vector2D _velocity; // nm/sec
    private Vector2D _acceleration; // nm/sec^2
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param position nm
     * @param orientation radians
     * @param diameter nm
     * @param density g/nm^3
     * @param fluid
     * @param laser
     */
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
        _velocity = new Vector2D();
        _acceleration = new Vector2D();
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the diameter.
     * 
     * @param diameter diameter (nm)
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
     * @return diameter (nm)
     */
    public double getDiameter() {
        return _diameter;
    }
    
    /**
     * Gets the mass.
     * 
     * @return mass (g)
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
     * @param motionEnabled true or false
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
    
    // Conversions used in bead motion algorithm
    private static final double NANOMETERS_PER_METER = 1E9;
    private static final double GRAMS_PER_KILOGRAM = 1E3;
    private static final double PICONEWTONS_PER_NANONEWTON = 1E3;
    
    private void moveBead( final double dt ) {
        
        // algorithm works only for horizontal fluid flow
        assert( _fluid.getOrientation() == 0 || _fluid.getOrientation() == Math.PI );
        
        // Top and bottom edges of microscope slide (nm), bead treated as a point
        final double yTopOfSlide = _fluid.getMinY() + ( getDiameter() / 2 ); // nm
        final double yBottomOfSlide = _fluid.getMaxY() - ( getDiameter() / 2 ); // nm
        
        // Drag coefficient (kg/sec)
        final double fluidViscosity = _fluid.getViscosity(); // Pa*s = kg/(m*sec)
        final double beadRadius = ( getDiameter() / 2 ) / NANOMETERS_PER_METER; // m
        final double gamma = 6 * Math.PI * fluidViscosity * beadRadius; // kg/sec
        
        // Trap force (nN)
        Vector2D trapForce = _laser.getTrapForce( getX(), getY() ); // pN
        double Fx = trapForce.getX() / PICONEWTONS_PER_NANONEWTON; // nN
        double Fy = trapForce.getY() / PICONEWTONS_PER_NANONEWTON; // nN
        if ( !MOTION_TRAP_FORCE_COMPONENT_ENABLED ) {
            Fx = Fy = 0;
        }
        
        // Acceleration (nm/sec^2)
        final double mass = getMass() / GRAMS_PER_KILOGRAM; // kg
        double fluidSpeed = _fluid.getSpeed(); // nm/sec
        if ( !MOTION_FLUID_COMPONENT_ENABLED ) {
            fluidSpeed = 0;
        }
        double ax = ( Fx / mass ) - ( gamma * _velocity.getX() / mass ) - ( gamma * fluidSpeed / mass ); // nm/sec^2
        double ay = ( Fy / mass ) - ( gamma * _velocity.getY() / mass ); // nm/sec^2
        _acceleration.setXY( ax, ay );
        
        // Brownian motion components (nm)
        double dxBrownian = 0;
        double dyBrownian = 0;
        if ( MOTION_BROWNIAN_COMPONENT_ENABLED ) {

            final double fluidTemperature = _fluid.getTemperature(); // Kelvin
            final double stepLength = BROWNIAN_MOTION_SCALE * Math.sqrt( fluidTemperature ) * Math.sqrt( dt ); // nm
            double stepAngle = 0; // radians
            if ( getY() <= yTopOfSlide ) {
                // bounce off top edge of microscope slide at an angle between 45 and 135 degrees
                stepAngle = ( Math.PI / 4 ) + ( _stepAngleRandom.nextDouble() * Math.PI / 2 );
            }
            else if ( getY() >= yBottomOfSlide ) {
                // bounce off bottom edge of microscope slide at an angle between -45 and -135 degrees
                stepAngle = ( Math.PI + ( Math.PI / 4 ) ) + ( _stepAngleRandom.nextDouble() * Math.PI / 2 );
            }
            else {
                // no collision with the edges of the microscope slide, any random angle will do
                stepAngle = _stepAngleRandom.nextDouble() * ( 2 * Math.PI );
            }
            // covert from Polar to Cartesian coordinates
            dxBrownian = stepLength * Math.cos( stepAngle ); // nm
            dyBrownian = stepLength * Math.sin( stepAngle ); // nm
        }

        // Combine all motion components
        final double xNew = getX() + ( _velocity.getX() * dt ) + ( 0.5 * _acceleration.getX() * dt * dt ) + dxBrownian;
        double yNew = getY() + ( _velocity.getY() * dt ) + ( 0.5 * _acceleration.getY() * dt * dt ) + dyBrownian;

        
        // New velocity (nm/sec)
        Vector2D oldVelocity = new Vector2D( _velocity ); //XXX
        double vx = _velocity.getX() + ( _acceleration.getX() * dt );
        double vy = _velocity.getY() + ( _acceleration.getY() * dt );
        _velocity.setXY( vx, vy );
        
        // Collision detection
        if ( yNew < yTopOfSlide ) {
            // collide with top edge of microscope slide
            yNew = yTopOfSlide;
        }
        else if ( yNew > yBottomOfSlide ) {
            // collide with bottom edge of microscope slide
            yNew = yBottomOfSlide;
        }
        
        Point2D oldPosition = getPosition(); //XXX
        setPosition( xNew, yNew );
        
        if ( MOTION_DEBUG_OUTPUT ) {
            System.out.println( "old position = " + oldPosition + " nm" );
            System.out.println( "new position = " + getPositionRef() + " nm" );
            System.out.println( "old velocity = " + oldVelocity + " nm/sec" );
            System.out.println( "new velocity = " + _velocity + " nm/sec" );
            System.out.println( "acceleration = " + _acceleration + " nm/sec^2" );
            System.out.println( "dt = " + dt );
            System.out.println( "mass =" + mass + " kg" );
            System.out.println( "radius = " + beadRadius + " m" );
            System.out.println( "fluid viscosity = " + fluidViscosity + " Pa*sec" );
            System.out.println( "drag coefficient = " + gamma + " kg/(m*s)" );
            System.out.println( "fluid speed = " + fluidSpeed + " nm/sec" );
            System.out.println( "trap Fx = " + Fx + " nN" );
            System.out.println( "trap Fy = " + Fy + " nN" );
            System.out.println();
        }
    }
}
