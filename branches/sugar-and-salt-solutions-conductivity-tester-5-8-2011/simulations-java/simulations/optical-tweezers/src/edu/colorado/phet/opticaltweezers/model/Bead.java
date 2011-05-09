// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.opticaltweezers.util.OTVector2D;

/**
 * Bead is the model of a glass bead, the dialectric particle in this experiement.
 * The bead exhibits Brownian motion, and is under the influence of an optical trap force,
 * a fluid drag force, and (if attached to a DNA strand) a DNA force. Motion models
 * are provided for fluid and vacuum.
 * <p>
 * Relationships:
 * <ul>
 * <li>uses fluid enabled state to determine whether we're in a fluid or vacuum
 * <li>uses fluid drag force, mobility, normalized viscosity and temperature in bead-in-fluid motion algorithm
 * <li>uses laser to calculate trap force and electric field 
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Bead extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Public class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_BROWNIAN_MOTION_ENABLED = "brownianMotionEnabled";
    
    // Developer controls
    public static final String PROPERTY_DT_SUBDIVISION_THRESHOLD = "dtSubdivisionThreshold";
    public static final String PROPERTY_NUMBER_OF_DT_SUBDIVISIONS = "numberOfDtSubdivisions";
    public static final String PROPERTY_BROWNIAN_MOTION_SCALE = "brownianMotionScale";
    public static final String PROPERTY_VERLET_ACCELERATION_SCALE = "verletAccelerationScale";
    public static final String PROPERTY_VERLET_DT_SUBDIVISION_THRESHOLD = "verletDtSubdivisionThreshold";
    public static final String PROPERTY_VERLET_NUMBER_OF_DT_SUBDIVISIONS = "verletNumberOfDtSubdivisions";
    public static final String PROPERTY_VACUUM_FAST_THRESHOLD = "vacuumFastThreshold";
    public static final String PROPERTY_VACUUM_FAST_DT = "vacuumFastDt";
    public static final String PROPERTY_VACUUM_FAST_POWER = "vacuumFastPower";
    
    //----------------------------------------------------------------------------
    // Private class data
    //----------------------------------------------------------------------------
    
    // Debugging output for the motion algorithms
    private static final boolean DEBUG_MOTION_IN_FLUID = false;
    private static final boolean DEBUG_MOTION_IN_VACUUM = false;
    
    // units conversions
    private static final double PM_PER_NM = 1E3; // picometers per nanometer
    private static final double G_PER_KG = 1E3; // grams per kilogram
    
    private static final OTVector2D ZERO_VECTOR = new OTVector2D.Cartesian( 0, 0 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final double _diameter; // nm
    private final double _density; // kg/nm^3
    private final Fluid _fluid;
    private final MicroscopeSlide _microscopeSlide;
    private final Laser _laser;
    private final Random _stepAngleRandom;
    private boolean _motionEnabled;
    private boolean _brownianMotionEnabled;
    private final OTVector2D _velocity; // nm/sec
    private DNAStrand _dnaStrand;
    
    // Developer controls
    private double _brownianMotionScale;
    private final DoubleRange _brownianMotionScaleRange;
    private double _dtSubdivisionThreshold;
    private final DoubleRange _dtSubdivisionThresholdRange;
    private int _numberOfDtSubdivisions;
    private final IntegerRange _numberOfDtSubdivisionsRange;
    private double _verletDtSubdivisionThreshold;
    private final DoubleRange _verletDtSubdivisionThresholdRange;
    private int _verletNumberOfDtSubdivisions;
    private final IntegerRange _verletNumberOfDtSubdivisionsRange;
    private double _verletAccelerationScale;
    private final DoubleRange _verletAccelerationScaleRange;
    private double _vacuumFastThreshold;
    private final DoubleRange _vacuumFastThresholdRange;
    private double _vacuumFastDt;
    private final DoubleRange _vacuumFastDtRange;
    private double _vacuumFastPower;
    private final DoubleRange _vacuumFastPowerRange;
    
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
     * @param microscopeSlide
     * @param laser
     * @param brownianMotionScaleRange
     * @param dtSubdivisionThresholdRange
     * @param numberOfDtSubdivisionsRange
     * @param verletDtSubdivisionThresholdRange
     * @param verletNumberOfDtSubdivisionsRange
     * @param verletAccelerationScaleRange
     * @param vacuumFastThresholdRange
     * @param vacuumFastDtRange
     * @param vacuumFastPowerRange
     */
    public Bead( Point2D position, 
            double orientation, 
            double diameter, 
            double density,
            Fluid fluid,
            MicroscopeSlide microscopeSlide,
            Laser laser,
            DoubleRange brownianMotionScaleRange,
            DoubleRange dtSubdivisionThresholdRange,
            IntegerRange numberOfDtSubdivisionsRange,
            DoubleRange verletDtSubdivisionThresholdRange,
            IntegerRange verletNumberOfDtSubdivisionsRange,
            DoubleRange verletAccelerationScaleRange,
            DoubleRange vacuumFastThresholdRange,
            DoubleRange vacuumFastDtRange,
            DoubleRange vacuumFastPowerRange ) {
        
        super( position, orientation, 0 /* speed */ );
        
        if ( diameter <= 0 ) {
            throw new IllegalArgumentException( "diameter must be > 0: " + diameter );
        }
        if ( density <= 0 ) {
            throw new IllegalArgumentException( "density must be > 0: " + density );   
        }
        
        _diameter = diameter;
        _density = density;
        
        _microscopeSlide = microscopeSlide;
        _fluid = fluid;
        
        _laser = laser;
        
        _stepAngleRandom = new Random();
        _motionEnabled = true;
        _brownianMotionEnabled = true;
        _velocity = new OTVector2D.Cartesian();
        
        _brownianMotionScaleRange = brownianMotionScaleRange;
        _dtSubdivisionThresholdRange = dtSubdivisionThresholdRange;
        _numberOfDtSubdivisionsRange = numberOfDtSubdivisionsRange;
        _verletDtSubdivisionThresholdRange = verletDtSubdivisionThresholdRange;
        _verletNumberOfDtSubdivisionsRange = verletNumberOfDtSubdivisionsRange;
        _verletAccelerationScaleRange = verletAccelerationScaleRange;
        _vacuumFastThresholdRange = vacuumFastThresholdRange;
        _vacuumFastDtRange = vacuumFastDtRange;
        _vacuumFastPowerRange = vacuumFastPowerRange;
        
        _brownianMotionScale = brownianMotionScaleRange.getDefault();
        _dtSubdivisionThreshold = dtSubdivisionThresholdRange.getDefault();
        _numberOfDtSubdivisions = numberOfDtSubdivisionsRange.getDefault();
        _verletDtSubdivisionThreshold = verletDtSubdivisionThresholdRange.getDefault();
        _verletNumberOfDtSubdivisions = verletNumberOfDtSubdivisionsRange.getDefault();
        _verletAccelerationScale = verletAccelerationScaleRange.getDefault();
        _vacuumFastThreshold = vacuumFastThresholdRange.getDefault();
        _vacuumFastDt = vacuumFastDtRange.getDefault();
        _vacuumFastPower = vacuumFastPowerRange.getDefault();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
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
        if ( motionEnabled != _motionEnabled ) {
            _motionEnabled = motionEnabled;
            _velocity.setXY( 0, 0 );
        }
    }
    
    public void setBrownianMotionEnabled( boolean enabled ) {
        if ( enabled != _brownianMotionEnabled ) {
            _brownianMotionEnabled = enabled;
            notifyObservers( PROPERTY_BROWNIAN_MOTION_ENABLED );
        }
    }
    
    public boolean isBrownianMotionEnabled() {
        return _brownianMotionEnabled;
    }
    
    /**
     * Gets the drag force acting on the bead at its current velocity.
     * 
     * @return Vector2D
     */
    public OTVector2D getDragForce() {
        OTVector2D dragForce = ZERO_VECTOR;
        if ( _fluid.isEnabled() ) {
            // bead is in fluid
            dragForce = _fluid.getDragForce( _velocity, _diameter );
        }
        return dragForce;
    }
    
    /**
     * Gets the optical trap force acting on the bead at its current location.
     * 
     * @return Vector2D
     */
    public OTVector2D getTrapForce() {
        OTVector2D trapForce = ZERO_VECTOR;
        if ( _laser != null ) {
            trapForce = _laser.getTrapForce( getX(), getY() );
        }
        return trapForce;
    }
    
    /**
     * Gets the potential energy of the bead.
     * 
     * @return potential energy (mJ)
     */
    public double getPotentialEnergy() {
        double potentialEnergy = 0;
        if ( _laser != null ) {
            potentialEnergy = _laser.getPotentialEnergy( getX(), getY() );
        }
        return potentialEnergy;
    }
    
    /**
     * Attaches the bead to the head of a DNA strand.
     * 
     * @param dnaStrand
     */
    public void attachTo( DNAStrand dnaStrand ) {
        _dnaStrand = dnaStrand;
    }
    
    /**
     * Gets the DNA strand that the bead is attached to.
     * 
     * @return DNAStrand, null if bead is not attached to a DNA strand
     */
    public DNAStrand getDNAStrand() {
        return _dnaStrand;
    }
    
    /**
     * Gets the x-component of the electric field at the bead's position.
     * 
     * @return
     */
    public double getElectricFieldX() {
        double electricFieldX = 0;
        if ( _laser != null ) {
            double xOffset = _position.getX() - _laser.getPositionReference().getX();
            double yOffset = _position.getY() - _laser.getPositionReference().getY();
            electricFieldX = _laser.getElectricFieldX( xOffset, yOffset );
        }
        return electricFieldX;
    }
    
    //----------------------------------------------------------------------------
    // Developer controls
    //----------------------------------------------------------------------------
    
    /**
     * Sets the scaling factor used to calculate Brownian motion.
     * Bigger values cause bigger motion.
     * 
     * @param brownianMotionScale
     */
    public void setBrownianMotionScale( double brownianMotionScale ) {
        if ( !_brownianMotionScaleRange.contains( brownianMotionScale ) ) {
            throw new IllegalArgumentException( "brownianMotionScale out of range: " + brownianMotionScale );
        }
        if ( brownianMotionScale != _brownianMotionScale ) {
            _brownianMotionScale = brownianMotionScale;
            notifyObservers( PROPERTY_BROWNIAN_MOTION_SCALE );
        }
    }
    
    public double getBrownianMotionScale() {
        return _brownianMotionScale;
    }
    
    public DoubleRange getBrownianMotionScaleRange() {
        return _brownianMotionScaleRange;
    }
    
    /**
     * Sets the dt subdivision threshold for the bead-in-fluid motion algorithm.
     * Clock steps above this value will be subdivided as specified by setVerletNumberOfDtSubdivisions.
     * 
     * @param verletDtSubdivisionThreshold
     */
    public void setDtSubdivisionThreshold( double dtSubdivisionThreshold ) {
        if ( !_dtSubdivisionThresholdRange.contains( dtSubdivisionThreshold ) ) {
            throw new IllegalArgumentException( "dtSubdivisionThreshold out of range: " + dtSubdivisionThreshold );
        }
        if ( dtSubdivisionThreshold != _dtSubdivisionThreshold ) {
            _dtSubdivisionThreshold = dtSubdivisionThreshold;
            notifyObservers( PROPERTY_DT_SUBDIVISION_THRESHOLD );
        }
    }
    
    public double getDtSubdivisionThreshold() {
        return _dtSubdivisionThreshold;
    }
    
    public DoubleRange getDtSubdivisionThresholdRange() {
        return _dtSubdivisionThresholdRange;
    }
    
    /**
     * Sets the number of dt subdivisions for the bead-in-fluid motion algorithm.
     * This determines how many times the motion algorithm is run each time the clock ticks.
     * 
     * @param numberOfDtSubdivisions
     */
    public void setNumberOfDtSubdivisions( int numberOfDtSubdivisions ) {
        if ( !_numberOfDtSubdivisionsRange.contains( numberOfDtSubdivisions ) ) {
            throw new IllegalArgumentException( "numberOfSubdivisions out of range: " + numberOfDtSubdivisions );
        }
        if ( numberOfDtSubdivisions != _numberOfDtSubdivisions ) {
            _numberOfDtSubdivisions = numberOfDtSubdivisions;
            notifyObservers( PROPERTY_NUMBER_OF_DT_SUBDIVISIONS );
        }
    }
    
    public int getNumberOfDtSubdivisions() {
        return _numberOfDtSubdivisions;
    }
    
    public IntegerRange getNumberOfDtSubdivisionsRange() {
        return _numberOfDtSubdivisionsRange;
    }
    
    /**
     * Sets the acceleration scaling factor used in the Verlet (vacuum) motion algorithm.
     * 
     * @param verletAccelerationScale
     */
    public void setVerletAccelerationScale( double verletAccelerationScale ) {
        if ( !_verletAccelerationScaleRange.contains( verletAccelerationScale ) ) {
            throw new IllegalArgumentException( "verletAccelerationScale out of range: " + verletAccelerationScale );
        }
        if ( verletAccelerationScale != _verletAccelerationScale ) {
            _verletAccelerationScale = verletAccelerationScale;
            notifyObservers( PROPERTY_VERLET_ACCELERATION_SCALE );
        }
    }
    
    public double getVerletAccelerationScale() {
        return _verletAccelerationScale;
    }
    
    public DoubleRange getVerletAccelerationScaleRange() {
        return _verletAccelerationScaleRange;
    }
    
    /**
     * Sets the dt subdivision threshold for Verlet (vacuum) motion algorithm.
     * Clock steps above this value will be subdivided as specified by setVerletNumberOfDtSubdivisions.
     * 
     * @param verletDtSubdivisionThreshold
     */
    public void setVerletDtSubdivisionThreshold( double verletDtSubdivisionThreshold ) {
        if ( !_verletDtSubdivisionThresholdRange.contains( verletDtSubdivisionThreshold ) ) {
            throw new IllegalArgumentException( "verletDtSubdivisionThreshold out of range: " + verletDtSubdivisionThreshold );
        }
        if ( verletDtSubdivisionThreshold != _verletDtSubdivisionThreshold ) {
            _verletDtSubdivisionThreshold = verletDtSubdivisionThreshold;
            notifyObservers( PROPERTY_VERLET_DT_SUBDIVISION_THRESHOLD );
        }
    }
    
    public double getVerletDtSubdivisionThreshold() {
        return _verletDtSubdivisionThreshold;
    }
    
    public DoubleRange getVerletDtSubdivisionThresholdRange() {
        return _verletDtSubdivisionThresholdRange;
    }
    
    /**
     * Sets the number of dt subdivisions for the Verlet (vacuum) motion algorithm.
     * This determines how many times the motion algorithm is run each time the clock ticks.
     * 
     * @param verletNumberOfDtSubdivisions
     */
    public void setVerletNumberOfDtSubdivisions( int verletNumberOfDtSubdivisions ) {
        if ( !_verletNumberOfDtSubdivisionsRange.contains( verletNumberOfDtSubdivisions ) ) {
            throw new IllegalArgumentException( "verletNumberOfDtSubdivisions out of range: " + verletNumberOfDtSubdivisions );
        }
        if ( verletNumberOfDtSubdivisions != _verletNumberOfDtSubdivisions ) {
            _verletNumberOfDtSubdivisions = verletNumberOfDtSubdivisions;
            notifyObservers( PROPERTY_VERLET_NUMBER_OF_DT_SUBDIVISIONS );
        }
    }
    
    public int getVerletNumberOfDtSubdivisions() {
        return _verletNumberOfDtSubdivisions;
    }
    
    public IntegerRange getVerletNumberOfDtSubdivisionsRange() {
        return _verletNumberOfDtSubdivisionsRange;
    }
    
    /**
     * Sets the threshold about which bead in a vacuum motion will be faked to look "really fast".
     * The threshold is dt*power.
     * 
     * @param vacuumFastThreshold
     */
    public void setVacuumFastThreshold( double vacuumFastThreshold ) {
        if ( !_vacuumFastThresholdRange.contains( vacuumFastThreshold  ) ) {
            throw new IllegalArgumentException( "vacuumFastThreshold out of range: " + vacuumFastThreshold );
        }
        if ( _vacuumFastThreshold != vacuumFastThreshold ) {
            _vacuumFastThreshold = vacuumFastThreshold;
            notifyObservers( PROPERTY_VACUUM_FAST_THRESHOLD );
        }
    }
    
    public double getVacuumFastThreshold() {
        return _vacuumFastThreshold;
    }
    
    public DoubleRange getVacuumFastThresholdRange() {
        return _vacuumFastThresholdRange;
    }
    
    /**
     * Sets the dt used for faking "really fast" bead motion in a vacuum.
     * 
     * @param vacuumFastDt
     */
    public void setVacuumFastDt( double vacuumFastDt ) {
        if ( !_vacuumFastDtRange.contains( vacuumFastDt  ) ) {
            throw new IllegalArgumentException( "vacuumFastDt out of range: " + vacuumFastDt );
        }
        if ( _vacuumFastDt != vacuumFastDt ) {
            _vacuumFastDt = vacuumFastDt;
            notifyObservers( PROPERTY_VACUUM_FAST_DT );
        }
    }
    
    public double getVacuumFastDt() {
        return _vacuumFastDt;
    }
    
    public DoubleRange getVacuumFastDtRange() {
        return _vacuumFastDtRange;
    }
    
    /**
     * Sets the laser power used for faking "really fast" bead motion in a vacuum.
     * 
     * @param vacuumFastPower laser power (mW)
     */
    public void setVacuumFastPower( double vacuumFastPower ) {
        if ( !_vacuumFastPowerRange.contains( vacuumFastPower  ) ) {
            throw new IllegalArgumentException( "vacuumFastPower out of range: " + vacuumFastPower );
        }
        if ( _vacuumFastPower != vacuumFastPower ) {
            _vacuumFastPower = vacuumFastPower;
            notifyObservers( PROPERTY_VACUUM_FAST_POWER );
        }
    }
    
    public double getVacuumFastPower() {
        return _vacuumFastPower;
    }
    
    public DoubleRange getVacuumFastPowerRange() {
        return _vacuumFastPowerRange;
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
    // Motion model
    //----------------------------------------------------------------------------
    
    private void move( double clockDt ) {
        if ( _fluid.isEnabled() ) {
            // bead is in fluid
            moveInFluid( clockDt );
        }
        else {
            // bead is in a vacuum
            moveInVacuum( clockDt );
        }
    }
    
    /*
     * Bead motion in a vacuum, Verlet algorithm.
     * The only force acting on the bead is the optical trap force.
     * 
     * Units:
     *     time - sec
     *     force - pN = 1E-12 * (kg*m)/sec^2
     *     distance - nm
     *     velocity - nm/sec
     *     acceleration - nm/sec^2
     *     mass - kg
     * 
     * @param clockDt
     */
    private void moveInVacuum( double clockDt ) {
        
        // Top and bottom edges of microscope slide, bead treated as a point
        final double yTopOfSlide = _microscopeSlide.getCenterMinY() + ( getDiameter() / 2 ); // nm
        final double yBottomOfSlide = _microscopeSlide.getCenterMaxY() - ( getDiameter() / 2 ); // nm

        final double mass = getMass() / G_PER_KG; // kg
        
        // current values for positon & velocity
        double xOld = getX();
        double yOld = getY();
        double vxOld = _velocity.getX();
        double vyOld = _velocity.getY();
        
        /*
         * FAKING MOTION IN A VACUUM:
         * Above a certain threshold, we want the oscillation to just look "really fast".
         * This eliminates oscillations that appear slower at certain points due to 
         * how far the bead moves per time step.  The threshold is based on clock dt 
         * and laser power.  When the threshold is exceeded, we use some values 
         * for dt and power that we know will make the motion look "really fast".
         * Note that this is also done in the main loop below, when calculating trap force.
         */
        boolean fakeMotion = false;
        OTVector2D trapForce = ZERO_VECTOR;
        if ( _laser != null ) {
            fakeMotion = ( _laser.isRunning() && ( _laser.getPower() * clockDt >= _vacuumFastThreshold ) );
            if ( fakeMotion ) {
                clockDt = _vacuumFastDt;
                trapForce = _laser.getTrapForce( xOld, yOld, _vacuumFastPower );
            }
            else {
                trapForce = _laser.getTrapForce( xOld, yOld ); // pN = 1E-12 * N = 1E-12 * (kg*m)/sec^2
            }
        }
        
        // current acceleration
        double axOld = _verletAccelerationScale * ( 1 / PM_PER_NM ) * trapForce.getX() / mass; // nm/sec^2
        double ayOld = _verletAccelerationScale * ( 1 / PM_PER_NM ) * trapForce.getY() / mass;
        
        // next values for position, velocity and acceleration
        double xNew = 0, yNew = 0;
        double vxNew = 0, vyNew = 0;
        double axNew = 0, ayNew = 0;
        
        // Subdivide the clock step into N equals pieces
        double dt = clockDt;
        int loops = 1;
        if ( clockDt > ( 1.001 * _verletDtSubdivisionThreshold ) ) {
            dt = clockDt / _verletNumberOfDtSubdivisions;
            loops = _verletNumberOfDtSubdivisions;
        }
        
        // Run the motion algorithm for subdivided clock step
        for ( int i = 0; i < loops; i++ ) {
            
            // new position
            xNew = xOld + ( vxOld * dt ) + ( 0.5 * axOld * dt * dt );
            yNew = yOld + ( vyOld * dt ) + ( 0.5 * ayOld * dt * dt );
            
            // simple collision detection
            if ( yNew < yTopOfSlide ) {
                // collide with top edge of microscope slide
                yNew = yTopOfSlide;
            }
            else if ( yNew > yBottomOfSlide ) {
                // collide with bottom edge of microscope slide
                yNew = yBottomOfSlide;
            }
            
            // trap force
            trapForce = ZERO_VECTOR;
            if ( _laser != null ) {
                if ( fakeMotion ) {
                    // See comment above about faking motion in a vacuum
                    trapForce = _laser.getTrapForce( xNew, yNew, _vacuumFastPower );
                }
                else {
                    trapForce = _laser.getTrapForce( xNew, yNew ); // pN = 1E-12 * N = 1E-12 * (kg*m)/sec^2
                }
            }
            
            // new acceleration
            axNew = _verletAccelerationScale * ( 1 / PM_PER_NM ) * trapForce.getX() / mass; // nm/sec^2
            ayNew = _verletAccelerationScale * ( 1 / PM_PER_NM ) * trapForce.getY() / mass;
            
            // new velocity
            vxNew = vxOld + ( 0.5 * ( axNew + axOld ) * dt );
            vyNew = vyOld + ( 0.5 * ( ayNew + ayOld ) * dt );
            
            if ( DEBUG_MOTION_IN_VACUUM ) {
                System.out.println( "fakeMotion = " + fakeMotion );
                System.out.println( "dt = " + dt );
                System.out.println( "mass = " + mass + " kg" );
                System.out.println( "old position = " + new Point2D.Double( xOld, yOld ) + " nm" );
                System.out.println( "new position = " + new Point2D.Double( xNew, yNew ) + " nm" );
                System.out.println( "new trap force = " + trapForce + " pN" );
                System.out.println( "old acceleration = [" + axOld + "," + ayOld + "] nm/sec^2" );
                System.out.println( "new acceleration = [" + axNew + "," + ayNew + "] nm/sec^2" );
                System.out.println( "old velocity = [" + vxOld + "," + vyOld + "] nm/sec" );
                System.out.println( "new velocity = [" + vxNew + "," + vyNew + "] nm/sec" );
                System.out.println();
            }
            
            xOld = xNew;
            yOld = yNew;
            vxOld = vxNew;
            vyOld = vyNew;
            axOld = axNew;
            ayOld = ayNew;
        }
        
        // Set new values
        _velocity.setXY( vxNew, vyNew ); // nm/sec
        setPosition( xNew, yNew ); // nm
    }
    
    /*
     * Bead motion in a fluid.
     * Forces acting on the bead include Brownian force, optical trap force,
     * fluid drag force and (if the bead is attached to a DNA strand) DNA force.
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
    private void moveInFluid( final double clockDt ) {
        
        // Top and bottom edges of microscope slide, bead treated as a point
        final double yTopOfSlide = _microscopeSlide.getCenterMinY() + ( getDiameter() / 2 ); // nm
        final double yBottomOfSlide = _microscopeSlide.getCenterMaxY() - ( getDiameter() / 2 ); // nm
        
        // Mobility
        final double normalizedViscosity = _fluid.getDimensionlessNormalizedViscosity(); // unitless
        final double mobility = _fluid.getMobility( _diameter ); // (nm/sec)/pN
        final OTVector2D fluidVelocity = _fluid.getVelocity(); // nm/sec
        if ( fluidVelocity.getY() != 0 ) {
            throw new IllegalStateException( "bead motion algorithm requires horizontal fluid flow" );
        }
        
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
        
        // Subdivide the clock step into N equals pieces
        double dt = clockDt;
        int loops = 1;
        if ( clockDt > ( 1.001 * _dtSubdivisionThreshold ) ) {
            dt = clockDt / _numberOfDtSubdivisions;
            loops = _numberOfDtSubdivisions;
        }
        
        // assume these are all zero
        OTVector2D trapForce = ZERO_VECTOR;
        OTVector2D dnaForce = ZERO_VECTOR;
        OTVector2D brownianDisplacement = ZERO_VECTOR;
        
        // Run the motion algorithm for subdivided clock step
        for ( int i = 0; i < loops; i++ ) {

            // Trap force (pN)
            if ( _laser != null ) {
                trapForce = _laser.getTrapForce( xOld, yOld );
            }

            // DNA force (pN)
            if ( _dnaStrand != null ) {
                dnaForce = _dnaStrand.getForce( xOld, yOld );
            }
                
            // Brownian displacement (nm)
            if ( _brownianMotionEnabled ) {
                brownianDisplacement = computeBrownianDisplacement( dt );
            }

            // New position (nm)
            xNew = xOld + ( vxOld * dt ) + brownianDisplacement.getX();
            yNew = yOld + ( vyOld * dt ) + brownianDisplacement.getY();

            /*
             * Collision detection.
             * This is very simplified because Brownian force is the only force
             * that moves the bead toward the edges of the microscope slide.
             * Trap force and DNA force pull the bead towards the center,
             * while fluid flow moves the bead from left to right.
             */
            if ( yNew < yTopOfSlide ) {
                // collide with top edge of microscope slide
                yNew = yTopOfSlide;
            }
            else if ( yNew > yBottomOfSlide ) {
                // collide with bottom edge of microscope slide
                yNew = yBottomOfSlide;
            }
            
            // New velocity
            vxNew = ( mobility * trapForce.getX() ) + ( mobility * dnaForce.getX() ) + fluidVelocity.getX(); // nm/sec
            vyNew = ( mobility * trapForce.getY() ) + ( mobility * dnaForce.getY() ) + fluidVelocity.getY(); // nm/sec

            if ( DEBUG_MOTION_IN_FLUID ) {
                System.out.println( "old position = " + new Point2D.Double( xOld, yOld ) + " nm" );
                System.out.println( "new position = " + new Point2D.Double( xNew, yNew ) + " nm" );
                System.out.println( "old velocity = [" + vxOld + "," + vyOld + "] nm/sec" );
                System.out.println( "new velocity = [" + vxNew + "," + vyNew + "] nm/sec" );
                System.out.println( "dt = " + dt );
                System.out.println( "normalized viscosity = " + normalizedViscosity );
                System.out.println( "mobility = " + mobility + " (nm/sec)/pN" );
                System.out.println( "fluid velocity = " + fluidVelocity + " nm/sec" );
                System.out.println( "trap force = " + trapForce + " pN" );
                System.out.println( "DNA force = " + dnaForce + " pN" );
                System.out.println( "Brownian displacement = " + brownianDisplacement + " nm" );
                System.out.println();
            }
            
            xOld = xNew;
            yOld = yNew;
            vxOld = vxNew;
            vyOld = vyNew;
        }
        
        // Set new values
        _velocity.setXY( vxNew, vyNew ); // nm/sec
        setPosition( xNew, yNew ); // nm
    }
    

    /*
     * Computes a random Brownian displacement.
     * 
     * @return displacement vector (nm)
     */
    private OTVector2D computeBrownianDisplacement( final double dt ) {
        
        OTVector2D displacement = null;
        if ( _fluid.isEnabled() ) {
            // bead is in fluid
            final double normalizedViscosity = _fluid.getDimensionlessNormalizedViscosity(); // unitless
            final double fluidTemperature = _fluid.getTemperature(); // Kelvin

            final double stepLength = _brownianMotionScale * ( 2200 / Math.sqrt( normalizedViscosity ) ) * Math.sqrt( fluidTemperature / 300 ) * Math.sqrt( dt ); // nm
            double stepAngle = _stepAngleRandom.nextDouble() * ( 2 * Math.PI ); // radians

            displacement = new OTVector2D.Polar( stepLength, stepAngle );
        }
        else {
            // bead is in a vacuum
            displacement = ZERO_VECTOR;
        }
        return displacement;
    }
}
