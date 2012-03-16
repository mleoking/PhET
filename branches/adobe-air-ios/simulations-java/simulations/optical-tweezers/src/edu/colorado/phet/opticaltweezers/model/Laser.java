// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock.ConstantDtClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock.ConstantDtClockEvent;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.opticaltweezers.util.OTVector2D;

/**
 * Laser is the model of the laser.
 * Position indicates where the center of the trap is located.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Laser extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_POWER = "power";
    public static final String PROPERTY_RUNNING = "running";

    // Developer controls
    public static final String PROPERTY_TRAP_FORCE_RATIO = "trapForceRatio";
    public static final String PROPERTY_ELECTRIC_FIELD_SCALE = "electricFieldScale";
    public static final String PROPERTY_ELECTRIC_FIELD = "electricField";
    
    // fudge factor for potential energy model
    private static final double ALPHA = 1; // nm^2/sec
    
    private static final double SPEED_OF_LIGHT = 3E17; // nm/sec
    
    // dt used to make the e-field look like it's moving really fast
    private static final double ELECTRIC_FIELD_FAST_DT = 4E-5;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _running;
    private final double _diameterAtObjective; // nm
    private final double _diameterAtWaist; // nm
    private final double _distanceFromObjectiveToWaist;
    private final double _distanceFromObjectiveToControlPanel; // nm
    private final double _wavelength; // nm
    private final double _visibleWavelength; // nm
    private double _power; // mW
    private final DoubleRange _powerRange; // mW
    private double _electricFieldTime; // accumulated time, used to compute time-based e-field
    private OTClock _clock;
    
    // Developer controls
    private double _trapForceRatio; // determines ratio of x & y trap force components
    private DoubleRange _trapForceRatioRange;
    private double _electricFieldScale; // used to scale the e-field
    private DoubleRange _electricFieldScaleRange;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param position position of the laser's objective (nm)
     * @param orientation orientation of the beam (radians)
     * @param diameterAtObjective diameter of the beam at the objective (nm)
     * @param diameterAtWaist diameter at the waist of the beam (nm)
     * @param distanceFromObjectiveToWaist distance from the objective to the waist (nm)
     * @param distanceFromObjectiveToControlPanel distance from objective to control panel (nm)
     * @param wavelength wavelength (nm)
     * @param visibleWavelength wavelength (nm) to use in views, since actual wavelength is likely IR
     * @param power power (mW)
     * @param trapForceRatioRange
     * @param electricFieldScaleRange
     * @param clock
     */
    public Laser( Point2D position, 
            double orientation, 
            double diameterAtObjective, 
            double diameterAtWaist, 
            double distanceFromObjectiveToWaist, 
            double distanceFromObjectiveToControlPanel,
            double wavelength, 
            double visibleWavelength, 
            DoubleRange powerRange, 
            DoubleRange trapForceRatioRange, 
            DoubleRange electricFieldScaleRange,
            OTClock clock ) {
        
        super( position, orientation, 0 /* speed */ );
        
        _running = false;
        _diameterAtObjective = diameterAtObjective;
        _diameterAtWaist = diameterAtWaist;
        _distanceFromObjectiveToWaist = distanceFromObjectiveToWaist;
        _distanceFromObjectiveToControlPanel = distanceFromObjectiveToControlPanel;
        _wavelength = wavelength;
        _visibleWavelength = visibleWavelength;
        _powerRange = new DoubleRange( powerRange );
        _power = _powerRange.getDefault();
        _trapForceRatioRange = trapForceRatioRange;
        _trapForceRatio = _trapForceRatioRange.getDefault();
        _electricFieldScaleRange = electricFieldScaleRange;
        _electricFieldScale = _electricFieldScaleRange.getDefault();
        _electricFieldTime = 0;
        
        _clock = clock;
        _clock.addConstantDtClockListener( new ConstantDtClockAdapter() {
            // When dt changes, reset the accumulated electric field time.
            public void dtChanged( ConstantDtClockEvent event ) {
                resetElectricFieldTime();
            }
        });
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Turns the laser on and off.
     * 
     * @param running true or false
     */
    public void setRunning( boolean running ) {
        if ( running != _running ) {
            _running = running;
            notifyObservers( PROPERTY_RUNNING );
        }
    }
    
    /**
     * Is the laser running?
     * 
     * @return true or false
     */
    public boolean isRunning() {
        return _running;
    }
    
    /**
     * Gets the laser's diameter at the microscope objective.
     * 
     * @return nm
     */
    public double getDiameterAtObjective() {
        return _diameterAtObjective;
    }
    
    /**
     * Gets the laser's diameter at the trap waist.
     * 
     * @return nm
     */
    public double getDiameterAtWaist() {
        return _diameterAtWaist;
    }

    /**
     * Gets the distance from the microscope objective to the trap waist.
     * 
     * @return nm
     */
    public double getDistanceFromObjectiveToWaist() {
        return _distanceFromObjectiveToWaist;
    }
    
    /**
     * Gets the distance from the microscope objective to the control panel.
     * 
     * @return nm
     */
    public double getDistanceFromObjectiveToControlPanel() {
        return _distanceFromObjectiveToControlPanel;
    }
    
    /**
     * Gets the laser's wavelength.
     * 
     * @return nm
     */
    public double getWavelength() {
        return _wavelength;
    }
    
    /**
     * Gets the wavelength that used to display the laser in the view.
     * The laser's actual wavelength is likely not in the visible spectrum,
     * so this wavelength should be a visible wavelength.
     * 
     * @return nm
     */
    public double getVisibleWavelength() {
        return _visibleWavelength;
    }
    
    /**
     * Gets the laser's power.
     * Careful! The laser's power setting can be non-zero while the laser is not running.
     * 
     * @return mW
     */
    public double getPower() {
        return _power;
    }
    
    /**
     * Sets the laser's power.
     * 
     * @param power power (mW)
     */
    public void setPower( final double power ) {
        if ( power < _powerRange.getMin() || power > _powerRange.getMax() ) {
            throw new IllegalArgumentException( "power out of range: " + power );
        }
        if ( power != _power ) {
            _power = power;
            notifyObservers( PROPERTY_POWER );
        }
    }
    
    /**
     * Gets the laser's power range.
     * 
     * @return DoubleRange (mW)
     */
    public DoubleRange getPowerRange() {
        return _powerRange;
    }
    
    /**
     * Sets the trap force ratio.
     * This value is used in the calculation of Fy, the y-component of the trap force,
     * and determines the ratio of Fy:Fx.
     * 
     * @param trapForceRatio
     */
    public void setTrapForceRatio( final double trapForceRatio ) {
        if ( !_trapForceRatioRange.contains( trapForceRatio  ) ) {
            throw new IllegalArgumentException( "trapForceRation out of range: " + trapForceRatio );
        }
        if ( trapForceRatio != _trapForceRatio ) {
            _trapForceRatio = trapForceRatio;
            notifyObservers( PROPERTY_TRAP_FORCE_RATIO );
        }
    }
    
    /**
     * Gets the trap force ratio.
     * See setTrapForceRatio.
     * 
     * @return
     */
    public double getTrapForceRatio() {
        return _trapForceRatio;
    }
    
    /**
     * Gets the range of the trap force ratio.
     * See setTrapForceRatio.
     * 
     * @return
     */
    public DoubleRange getTrapForceRatioRange() {
        return _trapForceRatioRange;
    }
    
    public void setElectricFieldScale( final double electricFieldScale ) {
        if ( !_electricFieldScaleRange.contains( electricFieldScale ) ) {
            throw new IllegalArgumentException( "electricFieldScale out of range: " + electricFieldScale );
        }
        if ( electricFieldScale != _electricFieldScale ) {
            _electricFieldScale = electricFieldScale;
            notifyObservers( PROPERTY_ELECTRIC_FIELD_SCALE );
        }
    }
    public double getElectricFieldScale() {
        return _electricFieldScale;
    }
    
    public DoubleRange getElectricFieldScaleRange() {
        return _electricFieldScaleRange;
    }
    
    //----------------------------------------------------------------------------
    // Radius (shape) model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the beam radius at a specified distance from the center of the waist.
     * The center of the waist is at (0,0).  If we draw a line through the center
     * of the waist, in the direction that the beam is pointing, then z is a 
     * distance on this line.
     * 
     * @param yOffset vertical distance from the center of the waist (nm)
     * @return radius at y (nm)
     */
    public double getRadius( final double yOffset ) {
        final double yOffsetAbs = Math.abs( yOffset );
        final double r0 = _diameterAtWaist / 2;
        final double rMax = _diameterAtObjective / 2;
        final double zr = ( Math.PI * r0 * r0 ) / _wavelength;
        final double A = ( _distanceFromObjectiveToWaist / zr ) / Math.sqrt( ( ( rMax / r0 ) * ( rMax / r0 ) ) - 1 );
        final double t1 = yOffsetAbs / ( A * zr );
        return r0 * Math.sqrt( 1 + ( t1 * t1 ) );
    }
    
    /**
     * Is a specified point inside the laser beam's shape?
     * 
     * @param x coordinate relative to global model origin (nm)
     * @param y coordinate relative to global model origin (nm)
     * @return true or false
     */
    public boolean contains( final double x, final double y ) {
        assert( getOrientation() == Math.toRadians( -90 ) ); // laser beam must point up
        boolean b = false;
        final double xOffset = x - getX();
        final double yOffset = y - getY();
        // Is y on the out-going side of the objective?
        if ( yOffset <= _distanceFromObjectiveToWaist ) {
            final double radius = getRadius( yOffset );
            if ( radius >= Math.abs( xOffset ) ) {
                b = true;
            }
        }
        return b;
    }
    
    //----------------------------------------------------------------------------
    // Intensity model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the intensity at a point.
     * 
     * @param x coordinate relative to global model origin (nm)
     * @param y coordinate relative to global model origin (nm)
     * @return intensity (mW/nm^2)
     */
    public double getIntensity( final double x, final double y ) {
        final double power = ( _running ) ? _power : 0;
        return getIntensity( x, y, power );
    }

    /**
     * Gets the intensity at a point for a specific power.
     * 
     * @param x coordinate relative to global model origin (nm)
     * @param y coordinate relative to global model origin (nm)
     * @param power power (mW)
     * @return intensity (mW/nm^2)
     */
    public double getIntensity( final double x, final double y, final double power ) {
        final double xOffset = x - getX();
        final double yOffset = y - getY();
        final double radius = getRadius( yOffset );
        return getIntensityOnRadius( xOffset, radius, power ); 
    }
    
    /**
     * Gets the intensity for a specified location, radius and power.
     * 
     * @param xOffset horizontal distance from the center of the waist (nm)
     * @param radius beam radius (nm)
     * @param power power (mW)
     * @return intensity (mW/nm^2)
     */
    public static double getIntensityOnRadius( final double xOffset, final double radius, final double power ) {
        if ( radius <= 0 ) {
            throw new IllegalArgumentException( "radius must be > 0: " + radius );
        }
        if ( power < 0 ) {
            throw new IllegalArgumentException( "power must be >= 0 : " + power );
        }
        final double t1 = power / ( Math.PI * ( ( radius * radius ) / 2 ) );
        final double t2 = Math.exp( ( -2 * xOffset * xOffset ) / ( radius * radius ) );
        return t1 * t2;
    }
    
    /**
     * Gets the maximum intensity of the laser, at the center of the trap with max power.
     * 
     * @return intensity (mW/nm^2)
     */
    public double getMaxIntensity() {
        return getIntensity( getX(), getY(), _powerRange.getMax() );
    }
    
    //----------------------------------------------------------------------------
    // Trap Force model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the trap force at a point.
     * 
     * @param x coordinate relative to global model origin (nm)
     * @param y coordinate relative to global model origin (nm) 
     * @param trap force (pN)
     */
    public OTVector2D getTrapForce( final double x, final double y ) {
        final double power = ( _running ) ? _power : 0;
        return getTrapForce( x, y, power );
    }
    
    /**
     * Gets the trap force at a point for a specific power setting.
     * 
     * @param x coordinate relative to global model origin (nm)
     * @param y coordinate relative to global model origin (nm) 
     * @param power power (mW)
     * @param trap force (pN)
     */
    public OTVector2D getTrapForce( final double x, final double y, final double power ) {
        final double xOffset = x - getX();
        final double yOffset = y - getY();
        return getTrapForceAtOffset( xOffset, yOffset, power );
    }
    
    /**
     * Gets the maximum trap force.
     * 
     * @return trap force (pN)
     */
    public OTVector2D getMaxTrapForce() {
        double xOffset = _diameterAtWaist / 4; // halfway between center and edge of waist
        double yOffset = 0;
        double maxPower = _powerRange.getMax();
        return getTrapForceAtOffset( xOffset, yOffset, maxPower );
    }
    
    /*
     * Gets the trap force for at a specific offset from the center of the 
     * trap, and a specific power value.
     * 
     * @param xOffset horizontal distance from the center of the trap  (nm)
     * @param yOffset vertical distance from the center of the trap (nm)
     * @param power a power value (mW)
     * @return trap force (pN)
     */
    private OTVector2D getTrapForceAtOffset( final double xOffset, final double yOffset, final double power ) {
        if ( power < 0 ) {
            throw new IllegalArgumentException( "power must be >= 0 : " + power );
        }
        
        final double K = 582771.6; // provided by Tom Perkins
        final double ry = getRadius( yOffset ); // radius at yOffset, nm
        final double intensity = getIntensityOnRadius( xOffset, ry, power ); // intensity at radius, mW/nm^2
        
        // x component
        double fx = -1 * K * ( xOffset / ( ry * ry ) ) * intensity;

        // y component
        double fy = -1 * K * ( yOffset / ( ry * ry ) ) * intensity * _trapForceRatio * ( 1 - ( ( 2 * xOffset * xOffset ) / ( ry * ry ) ) );
        
        // In "slow" clock mode, the trap force should be shrinking and growing,
        // so that it's zero when the E-field is zero, and a maximum when the E-field is strongest.
        if ( _clock.getDt() <= _clock.getSlowRange().getMax() ) {
            final double electricFieldComponent = getElectricFieldComponent( yOffset );
            final double scale = Math.sqrt( 2 ) * Math.abs( electricFieldComponent );
            fx *= scale;
            fy *= scale;
        }

        return new OTVector2D.Cartesian( fx, fy );
    }
    
    //----------------------------------------------------------------------------
    // Potential Energy model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the potential energy at a point.
     * 
     * @param x coordinate relative to global model origin (nm)
     * @param y coordinate relative to global model origin (nm) 
     * @return potential energy (mJ)
     */
    public double getPotentialEnergy( final double x, final double y ) {
        final double power = ( _running ) ? _power : 0;
        return getPotentialEnergy( x, y, power );
    }
    
    /**
     * Gets the potential energy at a point for a hypothetical power value.
     * 
     * @param x
     * @param y
     * @param power
     * @return potential energy (mJ)
     */
    private double getPotentialEnergy( final double x, final double y, final double power ) {
        return -1 * ALPHA * getIntensity( x, y, power );
    }
    
    /**
     * Gets the minimum potential energy for the laser, at the center of the trap with max power..
     * 
     * @return minimum potential energy (mJ)
     */
    public double getMinPotentialEnergy() {
        return getPotentialEnergy( getX(), getY(), _powerRange.getMax() );
    }
    
    /**
     * Gets the maximum potential energy for the laser.
     * The maximum potential energy will approach zero.
     * 
     * @return 0 (mJ)
     */
    public double getMaxPotentialEnergy() {
        return 0;
    }
    
    //----------------------------------------------------------------------------
    // E-field model (x component only)
    //----------------------------------------------------------------------------
    
    /**
     * Gets the x component of the electric field at a point relative to the laser's position.
     * 
     * @param offset
     * @return x component of the electric field
     */
    public double getElectricFieldX( Point2D offset ) {
        return getElectricFieldX( offset.getX(), offset.getY() );
    }
    
    public double getElectricFieldX( double xOffset, double yOffset ) {
        final double intensity = getIntensity( getX() + xOffset, getY() + yOffset );
        final double e0 = getInitialElectricFieldX( intensity );
        final double ex = e0 * getElectricFieldComponent( yOffset );
        return ex;
    }
    
    /*
     * Gets the main component of the electric field at some vertical offset from the laser.
     * 
     * @param yOffset
     */
    private double getElectricFieldComponent( double yOffset ) {
        return Math.sin( ( ( 2 * Math.PI ) / _wavelength ) * ( yOffset + ( SPEED_OF_LIGHT * _electricFieldTime ) ) );
    }
    
    /**
     * Gets the x component of the maximum electric field, 
     * which is E0 at the center of the trap when the laser's power is at maximum intensity.
     * 
     * @return
     */
    public double getMaxElectricFieldX() {
        final double maxIntensity = getMaxIntensity();
        return getInitialElectricFieldX( maxIntensity );
    }
    
    /*
     * Gets the initial (t=0) x component of the electric field.
     */
    private double getInitialElectricFieldX( double intensity ) {
        return _electricFieldScale * intensity;
    }
    
    /*
     * Resets the electric field time by keeping only the portion of the time
     * that represents an unfinished cycle in its periodic motion.
     * This prevents the e-field from looking jerky at dt is changed.
     */
    private void resetElectricFieldTime() {
        final double period = _wavelength / SPEED_OF_LIGHT;
        final int numberOfCompletePeriods = (int) ( _electricFieldTime / period );
        if ( numberOfCompletePeriods > 0 ) {
            _electricFieldScale -= ( numberOfCompletePeriods * period );
        }
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        if ( _running ) {
            
            final double tPrevious = _electricFieldTime;
            
            /*
             * The E-field model only applies when the clock dt is in the "slow" range.
             * When dt is in the "fast" range, run the model at a fast speed, so
             * that we get a field that looks crazy.
             */
            if ( dt <= _clock.getSlowRange().getMax() ) {
                _electricFieldTime += dt;
            }
            else {
                _electricFieldTime += ELECTRIC_FIELD_FAST_DT;
            }
            
            /*
             * WORKAROUND for machine epsilon problem.
             * 
             * If _electricFieldTime gets sufficiently large, then adding a very small dt will
             * not change the value of _electricFieldTime. This will cause the electric field 
             * display to appear to "freeze", since the value of _electricFieldTime is not changing.
             * 
             * The workaround is to set _electricFieldTime to zero if we see that it hasn't changed.
             * Since our constructor adds a ConstantDtClockListener that sets _electricFieldTime=0
             * whenever the clock's dt changes, this should happen only after running the simulation
             * for a very very long time, and it's unlikely that anyone will every observe the 
             * jump in the electric field display when this happens.
             * 
             * See http://en.wikipedia.org/wiki/Machine_epsilon or Google "machine epsilon"
             * for further information on this general problem.
             * 
             * See also MathUtil.getMachineEpsilonDouble for code that computes machine epsilon.
             */
            if ( _electricFieldTime == tPrevious ) {
                System.out.println( "Laser.stepInTime: resetting t, t got big enough that t+dt=t: t=" + _electricFieldTime + " dt=" + dt );//XXX
                _electricFieldTime = 0;
            }
            
            notifyObservers( PROPERTY_ELECTRIC_FIELD );
        }
    }
}
