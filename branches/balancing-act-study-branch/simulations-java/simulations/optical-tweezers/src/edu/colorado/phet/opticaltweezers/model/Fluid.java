// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.opticaltweezers.model;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.opticaltweezers.util.OTVector2D;

/**
 * Fluid is the model of fluid that contains the glass bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Fluid extends OTObservable implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final double WATER_VISCOSITY = 1E-3; // Pa*sec
    
    public static final String PROPERTY_SPEED = "speed";
    public static final String PROPERTY_VISCOSITY = "viscosity";
    public static final String PROPERTY_TEMPERATURE = "temperature";
    public static final String PROPERTY_ENABLED = "enabled";
    public static final String PROPERTY_APT_CONCENTRATION = "aptConcentration";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final DoubleRange _speedRange; // nm/sec
    private final DoubleRange _viscosityRange; // Pa*sec
    private final DoubleRange _temperatureRange; // Kelvin
    private final DoubleRange _atpConcentrationRange; // arbitrary units
    
    private double _speed; // nm/sec
    private final double _direction; // radians
    private double _viscosity; // Pa*sec
    private double _temperature; // Kelvin
    private double _atpConcentration; // arbitrary units
    
    private boolean _enabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param speedRange speed of the fluid stream (nm/sec)
     * @param viscosityRange (Pa*sec)
     * @param temperatureRange (Kelvin)
     * @param atpConcentrationRange (arbitrary units)
     */
    public Fluid( DoubleRange speedRange, double direction, DoubleRange viscosityRange, DoubleRange temperatureRange, DoubleRange atpConcentrationRange ) {
        
        _speedRange = speedRange;
        _viscosityRange = viscosityRange;
        _temperatureRange = temperatureRange;
        _atpConcentrationRange = atpConcentrationRange;
        
        _speed = _speedRange.getDefault();
        _direction = direction;
        _viscosity = _viscosityRange.getDefault();
        _temperature = _temperatureRange.getDefault();
        _atpConcentration = _atpConcentrationRange.getDefault();
        
        _enabled = true;
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the direction of the fluid flow.
     * 
     * @return direction (radians)
     */
    public double getDirection() {
        return _direction;
    }
    
    /**
     * Get the fluid velocity, a vector describing speed (nm/sec) and direction (radians).
     * 
     * @return Vector2D
     */
    public OTVector2D getVelocity() {
        return getVelocity( null );
    }
    
    /**
     * Gets the fluid velocity, putting it in a provided vector.
     * If a vector is not provides, one will be allocated.
     * This method is provided for clients who need to reduce allocation of Vector2D objects.
     * 
     * @param velocityVector
     * @return Vector2D
     */
    public OTVector2D getVelocity( OTVector2D velocityVector ) {
        OTVector2D v = velocityVector;
        if ( v == null ) {
            v = new OTVector2D.Cartesian();
        }
        v.setMagnitudeAngle( getSpeed(), _direction );
        return v;
    }
    
    /**
     * Gets the fluid speed.
     * 
     * @return speed (nm/sec)
     */
    public double getSpeed() {
        return _speed;
    }
    
    /**
     * Sets the fluid speed. 
     * This is a scalar quantity because our model only supports left-to-right fluid flow.
     * 
     * @param speed speed (nm/sec)
     */
    public void setSpeed( double speed ) {
        if ( speed < _speedRange.getMin() || speed > _speedRange.getMax() ) {
            throw new IllegalArgumentException( "speed out of range: " + speed );
        }
        _speed = speed;
        notifyObservers( PROPERTY_SPEED );
    }
    
    /**
     * Gets the fluid speed range.
     * 
     * @return DoubleRange (nm/sec)
     */
    public DoubleRange getSpeedRange() {
        return _speedRange;
    }
    
    /**
     * Gets the fluid viscosity.
     * 
     * @return viscosity (Pa*sec)
     */
    public double getViscosity() {
        return _viscosity;
    }
    
    /**
     * Sets the fluid viscosity.
     * 
     * @param viscosity viscosity (Pa*sec)
     */
    public void setViscosity( double viscosity ) {
        if ( viscosity < _viscosityRange.getMin() || viscosity > _viscosityRange.getMax() ) {
            throw new IllegalArgumentException( "viscosity out of range: " + viscosity );
        }
        if ( viscosity != _viscosity ) {
            _viscosity = viscosity;
            notifyObservers( PROPERTY_VISCOSITY );
        }
    }
    
    /**
     * Gets the fluid viscosity range.
     * 
     * @return DoubleRange (Pa*sec)
     */
    public DoubleRange getViscosityRange() {
        return _viscosityRange;
    }
    
    /**
     * Gets the fluid temperature.
     * 
     * @return temperature (Kelvin)
     */
    public double getTemperature() {
        return _temperature;
    }

    /**
     * Sets the fluid temperature.
     * 
     * @param temperature temperature (Kelvin)
     */
    public void setTemperature( double temperature ) {
        if ( temperature < _temperatureRange.getMin() || temperature > _temperatureRange.getMax() ) {
            throw new IllegalArgumentException( "temperature out of range: " + temperature );
        }
        if ( temperature != _temperature ) {
            _temperature = temperature;
            notifyObservers( PROPERTY_TEMPERATURE );
        }
    }
    
    /**
     * Gets the fluid temperature range.
     * 
     * @return DoubleRange (Kelvin)
     */
    public DoubleRange getTemperatureRange() {
        return _temperatureRange;
    }
    
    /**
     * Gets the ATP concentration.
     * ATP is adenosine 5'-triphosphate, a multifunctional nucleotide that is
     * the "universal energy currency" of all known living organisms.
     * See http://en.wikipedia.org/wiki/Adenosine_triphosphate
     * 
     * @return APT concentration (arbitrary units)
     */
    public double getATPConcentration() {
        return _atpConcentration;
    }
    
    /**
     * Sets the ATP concentration.
     * 
     * @param aptConcentration APT concentration (arbitrary units)
     */
    public void setATPConcentration( double aptConcentration ) {
        if ( aptConcentration < _atpConcentrationRange.getMin() || aptConcentration > _atpConcentrationRange.getMax() ) {
            throw new IllegalArgumentException( "aptConcentration out of range: " + aptConcentration );
        }
        if ( aptConcentration != _atpConcentration ) {
            _atpConcentration = aptConcentration;
            notifyObservers( PROPERTY_APT_CONCENTRATION );
        }
    }
    
    /**
     * Gets the ATP concentration range.
     * 
     * @return DoubleRange (arbitrary units)
     */
    public DoubleRange getATPConcentrationRange() {
        return _atpConcentrationRange;
    }

    public void setEnabled( boolean enabled ) {
        if ( enabled != _enabled ) {
            _enabled = enabled;
            notifyObservers( PROPERTY_ENABLED );
        }
    }
    
    public boolean isEnabled() {
        return _enabled;
    }
    
    //----------------------------------------------------------------------------
    // Drag force model
    //----------------------------------------------------------------------------

    /**
     * Gets the drag force acting on a bead that is moving at a specified velocity.
     * 
     * @param beadVelocity
     * @param beadDiameter
     * @return drag force (pN)
     */
    public OTVector2D getDragForce( OTVector2D beadVelocity, double beadDiameter ) {
        double mobility = getMobility( beadDiameter );
        OTVector2D velocity = getVelocity();
        double fx = ( velocity.getX() - beadVelocity.getX() ) / mobility;
        double fy = ( velocity.getY() - beadVelocity.getY() ) / mobility;
        return new OTVector2D.Cartesian( fx, fy );
    }
    
    /**
     * Gets mobility.
     * 
     * @param beadDiameter
     * @return double (nm/sec)/pN
     */
    public double getMobility( double beadDiameter ) {
        double C = getMobilityConstant( beadDiameter );
        double normalizedViscosity = getDimensionlessNormalizedViscosity();
        return ( C / normalizedViscosity ); // (nm/sec)/pN
    }
    
    /* 
     * Mike Dubson's original notes show this value as 600 um/sec for a bead with radius R=100 nm.
     * We need a constant that is in nm/sec and works for any bead radius.
     * So we multiply 600 um/sec * 1000 nm/um to get 600,000 nm/sec,
     * then multiply by 100 nm to get 60,000,000 nm^2/sec.
     * Our calculation for the mobility constant is then:
     * 
     * C = 6E7 / R = nm/sec
     */
    private double getMobilityConstant( double beadDiameter ) {
        return 6E7 / ( beadDiameter / 2 );
    }
    
    /**
     * Gets the dimensionless normalized viscosity, where the viscosity of water is 1.
     * 
     * @return double (dimensionless)
     */
    public double getDimensionlessNormalizedViscosity() {
        return _viscosity / WATER_VISCOSITY;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        // do nothing
    }
}
