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
    private static final double DEFAULT_BROWNIAN_MOTION_SCALE = 1;
    private static final double DEFAULT_DT_SUBDIVISION_THRESHOLD = 1;
    private static final int DEFAULT_NUMBER_OF_DT_SUBDIVISIONS = 1;
    
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
    
    private double _brownianMotionScale;
    private double _dtSubdivisionThreshold;
    private int _numberOfDtSubdivisions;
    
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
        
        _brownianMotionScale = DEFAULT_BROWNIAN_MOTION_SCALE;
        _dtSubdivisionThreshold = DEFAULT_DT_SUBDIVISION_THRESHOLD;
        _numberOfDtSubdivisions = DEFAULT_NUMBER_OF_DT_SUBDIVISIONS;
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
    // Developer methods for tuning bead motion algorithm
    //----------------------------------------------------------------------------
    
    /**
     * Sets the scaling factor used to calculate Brownian motion.
     * Bigger values cause bigger motion.
     * 
     * @param scale
     */
    public void setBrownianMotionScale( double scale ) {
        _brownianMotionScale = scale;
    }
    
    /**
     * Gets the scaling factor used to calculate Brownian motion.
     * 
     * @return double
     */
    public double getBrownianMotionScale() {
        return _brownianMotionScale;
    }
    
    public void setDtSubdivisionThreshold( double threshold ) {
        _dtSubdivisionThreshold = threshold;
    }
    
    public double getDtSubdivisionThreshold() {
        return _dtSubdivisionThreshold;
    }
    
    public void setNumberOfDtSubdivisions( int numberOfDtSubdivisions ) {
        _numberOfDtSubdivisions = numberOfDtSubdivisions;
    }
    
    public int getNumberOfDtSubdivisions() {
        return _numberOfDtSubdivisions;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        if ( _motionEnabled ) {
            move( dt );
        }
    }
    
    //----------------------------------------------------------------------------
    // Trap Force model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the optical trap force acting on the bead.
     */
    public Vector2D getTrapForce() {
        return _laser.getTrapForce( getX(), getY() );
    }
    
    //----------------------------------------------------------------------------
    // Drag Force model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the drag force acting on the bead.
     * 
     * @return
     */
    public Vector2D getDragForce() {
        return _fluid.getDragForce( _velocity );
    }
    
    //----------------------------------------------------------------------------
    // Motion model
    //----------------------------------------------------------------------------

    /*
     * Bead motion algorithm.
     * 
     * Units:
     *     time - sec
     *     force - pN
     *     distance - nm
     *     velocity - nm/sec
     *     temperature - Kelvin
     *     
     * Constraints:
     *     direction of fluid flow must be horizontal
     */
    private void move( double clockDt ) {
        
        // algorithm works only for horizontal fluid flow
        assert( _fluid.getOrientation() == 0 || _fluid.getOrientation() == Math.PI );
        
        // Top and bottom edges of microscope slide, bead treated as a point
        final double yTopOfSlide = _fluid.getMinY() + ( getDiameter() / 2 ); // nm
        final double yBottomOfSlide = _fluid.getMaxY() - ( getDiameter() / 2 ); // nm
        
        // Mobility
        final double normalizedViscosity = _fluid.getDimensionlessNormalizedViscosity(); // unitless
        final double mobility = _fluid.getMobility(); // (nm/sec)/pN
        final Vector2D fluidVelocity = _fluid.getVelocity(); // nm/sec
        if ( fluidVelocity.getY() != 0 ) {
            throw new IllegalStateException( "bead motion algorithm requires horizontal fluid flow" );
        }
        final double fluidTemperature = _fluid.getTemperature(); // Kelvin
        
        // Old position and velocity
        double xOld = getX(); // nm
        double yOld = getY(); // nm
        double vxOld = _velocity.getX(); // nm/sec
        double vyOld = _velocity.getY(); // nm/sec
        
        // New position and velocity
        double xNew = 0;
        double yNew = 0;
        double vxNew = 0;
        double vyNew = 0;
        
        double dt = clockDt;
        int loops = 1;
        if ( clockDt > ( _dtSubdivisionThreshold + ( 0.001 * _dtSubdivisionThreshold ) ) ) {
            dt = clockDt / _numberOfDtSubdivisions;
            loops = _numberOfDtSubdivisions;
        }
        
        for ( int i = 0; i < loops; i++ ) {

            // Trap force
            Vector2D trapForce = _laser.getTrapForce( xOld, yOld ); // pN
            final double Fx = trapForce.getX(); // pN
            final double Fy = trapForce.getY(); // pN

            // Brownian motion components
            final double stepLength = _brownianMotionScale * ( 2200 / Math.sqrt( normalizedViscosity ) ) * Math.sqrt( fluidTemperature / 300 ) * Math.sqrt( dt ); // nm
            double stepAngle = 0; // radians
            if ( yOld <= yTopOfSlide ) {
                // bounce off top edge of microscope slide at an angle between 45 and 135 degrees
                stepAngle = ( Math.PI / 4 ) + ( _stepAngleRandom.nextDouble() * Math.PI / 2 );
            }
            else if ( yOld >= yBottomOfSlide ) {
                // bounce off bottom edge of microscope slide at an angle between -45 and -135 degrees
                stepAngle = ( Math.PI + ( Math.PI / 4 ) ) + ( _stepAngleRandom.nextDouble() * Math.PI / 2 );
            }
            else {
                // no collision with the edges of the microscope slide, any random angle will do
                stepAngle = _stepAngleRandom.nextDouble() * ( 2 * Math.PI );
            }
            // covert from Polar to Cartesian coordinates
            double bx = stepLength * Math.cos( stepAngle ); // nm
            double by = stepLength * Math.sin( stepAngle ); // nm

            // New position
            xNew = xOld + ( vxOld * dt ) + bx; // nm
            yNew = yOld + ( vyOld * dt ) + by; // nm

            // Collision detection
            if ( yNew < yTopOfSlide ) {
                // collide with top edge of microscope slide
                yNew = yTopOfSlide;
            }
            else if ( yNew > yBottomOfSlide ) {
                // collide with bottom edge of microscope slide
                yNew = yBottomOfSlide;
            }

            // New velocity
            vxNew = ( mobility * Fx ) + fluidVelocity.getX(); // nm/sec
            vyNew = ( mobility * Fy ) + fluidVelocity.getY(); // nm/sec

            if ( MOTION_DEBUG_OUTPUT ) {
                System.out.println( "old position = " + new Point2D.Double( xOld, yOld ) + " nm" );
                System.out.println( "new position = " + new Point2D.Double( xNew, yNew ) + " nm" );
                System.out.println( "old velocity = [" + vxOld + "," + vyOld + "] nm/sec" );
                System.out.println( "new velocity = [" + vxNew + "," + vyNew + "] nm/sec" );
                System.out.println( "dt = " + dt );
                System.out.println( "normalized viscosity = " + normalizedViscosity );
                System.out.println( "mobility = " + mobility + " (nm/sec)/pN" );
                System.out.println( "fluid velocity = " + fluidVelocity + " nm/sec" );
                System.out.println( "trap Fx = " + Fx + " pN" );
                System.out.println( "trap Fy = " + Fy + " pN" );
                System.out.println();
            }
            
            xOld = xNew;
            yOld = yNew;
            vxOld = vxNew;
            vyOld = vyNew;
        }

        // Set new values
        _velocity.setXY( vxNew, vyNew );
        setPosition( xNew, yNew );
    }
}
