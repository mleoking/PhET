package edu.colorado.phet.statesofmatter.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

/**
 * A temporary class that will act as the base class for the model so that
 * it is easy to switch back and forth between different model implementations
 * while the model is refactored.
 * 
 * @author John Blanco
 *
 */
public abstract class AbstractMultipleParticleModel {
	
	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	// Minimum container height fraction.
    public static final double MIN_CONTAINER_HEIGHT_FRACTION = 0.1;

    // TODO: JPB TBD - These constants are here as a result of the first attempt
    // to integrate Paul Beale's IDL implementation of the Verlet algorithm.
    // Eventually some or all of them will be moved.
    public static final int     DEFAULT_MOLECULE = StatesOfMatterConstants.NEON;
    protected static final double DISTANCE_BETWEEN_PARTICLES_IN_CRYSTAL = 0.3;  // In particle diameters.
    protected static final double DISTANCE_BETWEEN_DIATOMIC_PAIRS = 0.8;  // In particle diameters.
    protected static final double TIME_STEP = 0.020;  // Time per simulation clock tick, in seconds.
    protected static final double INITIAL_TEMPERATURE = 0.2;
    public static final double  MAX_TEMPERATURE = 50.0;
    public static final double  MIN_TEMPERATURE = 0.0001;
    protected static final double WALL_DISTANCE_THRESHOLD = 1.122462048309373017;
    protected static final double PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD = 6.25;
    protected static final double INITIAL_GRAVITATIONAL_ACCEL = 0.045;
    public static final double  MAX_GRAVITATIONAL_ACCEL = 0.4;
    protected static final double MAX_TEMPERATURE_CHANGE_PER_ADJUSTMENT = 0.025;
    protected static final int    TICKS_PER_TEMP_ADJUSTEMENT = 10;
    protected static final int    MAX_NUM_ATOMS = 500;
    protected static final double MIN_INJECTED_MOLECULE_VELOCITY = 0.5;
    protected static final double MAX_INJECTED_MOLECULE_VELOCITY = 2.0;
    protected static final double MAX_INJECTED_MOLECULE_ANGLE = Math.PI * 0.8;
    protected static final double INJECTION_POINT_HORIZ_PROPORTION = 0.95;
    protected static final double INJECTION_POINT_VERT_PROPORTION = 0.5;
    protected static final int    MAX_PLACEMENT_ATTEMPTS = 500; // For random placement when creating gas or liquid.
    protected static final double SAFE_INTER_MOLECULE_DISTANCE = 2.0;
    protected static final double PRESSURE_DECAY_CALCULATION_WEIGHTING = 0.999;
    protected static final int    VERLET_CALCULATIONS_PER_CLOCK_TICK = 8;

    // Constants used for setting the phase directly.
    public static final int PHASE_SOLID = 1;
    public static final int PHASE_LIQUID = 2;
    public static final int PHASE_GAS = 3;
    protected static final double SOLID_TEMPERATURE = 0.15;
    protected static final double LIQUID_TEMPERATURE = 0.42;
    protected static final double GAS_TEMPERATURE = 1.0;
    protected static final double MIN_INITIAL_INTER_PARTICLE_DISTANCE = 1.2;
    protected static final double MIN_INITIAL_PARTICLE_TO_WALL_DISTANCE = 2.5;
    
    // Possible thermostat settings.
    public static final int NO_THERMOSTAT = 0;
    public static final int ISOKINETIC_THERMOSTAT = 1;
    public static final int ANDERSEN_THERMOSTAT = 2;
    public static final int ADAPTIVE_THERMOSTAT = 3;   // Adaptive uses isokinetic when temperature is changing and
                                                       // Andersen when temperature is stable.  This is done because
                                                       // it provides the most visually appealing behavior.
    
    // Parameters to control rates of change of the container size.
    protected static final double MAX_PER_TICK_CONTAINER_SHRINKAGE = 50;
    protected static final double MAX_PER_TICK_CONTAINER_EXPANSION = 200;
    
    // Countdown value used when recalculating temperature when the
    // container size is changing.
    protected static final int CONTAINER_SIZE_CHANGE_RESET_COUNT = 25;
    
    // Range for deciding if the temperature is near the current set point.
    // The units are internal model units.
    protected static final double TEMPERATURE_CLOSENESS_RANGE = 0.15;

    // Constant used to limit how close the atoms are allowed to get to one
    // another so that we don't end up getting crazy big forces.
    protected static final double MIN_DISTANCE_SQUARED = 0.7225;

    // Parameters used for "hollywooding" of the water crystal.
    protected static final double WATER_FULLY_MELTED_TEMPERATURE = 0.3;
    protected static final double WATER_FULLY_MELTED_ELECTROSTATIC_FORCE = 1.0;
    protected static final double WATER_FULLY_FROZEN_TEMPERATURE = 0.22;
    protected static final double WATER_FULLY_FROZEN_ELECTROSTATIC_FORCE = 4.0;
    
    // Parameters that control the increasing of gravity as the temperature
    // approaches zero.  This is done to counteract the tendency of the
    // thermostat to slow falling molecules noticably at low temps.  This is
    // a "hollywooding" thing.
    protected static final double TEMPERATURE_BELOW_WHICH_GRAVITY_INCREASES = 0.10;
    protected static final double LOW_TEMPERATURE_GRAVITY_INCREASE_RATE = 50;
    
    // Values used for converting from model temperature to the temperature
    // for a given particle.
    public static final double TRIPLE_POINT_MODEL_TEMPERATURE = 0.4;    // Empirically determined.
    public static final double CRITICAL_POINT_MODEL_TEMPERATURE = 0.8;  // Empirically determined.
    protected static final double NEON_TRIPLE_POINT_IN_KELVIN = 25;
    protected static final double NEON_CRITICAL_POINT_IN_KELVIN = 44;
    protected static final double ARGON_TRIPLE_POINT_IN_KELVIN = 84;
    protected static final double ARGON_CRITICAL_POINT_IN_KELVIN = 151;
    protected static final double O2_TRIPLE_POINT_IN_KELVIN = 54;
    protected static final double O2_CRITICAL_POINT_IN_KELVIN = 155;
    protected static final double WATER_TRIPLE_POINT_IN_KELVIN = 273;
    protected static final double WATER_CRITICAL_POINT_IN_KELVIN = 647;

	public AbstractMultipleParticleModel() {
		super();
	}
	
	
	
	////////////////////// Methods //////////////////////////////////////
	
    abstract public double getGravitationalAcceleration();
    abstract public void setGravitationalAcceleration( double acc ); 
    abstract public void addListener(Listener listener);
    abstract public boolean getContainerExploded();
    abstract public double getEpsilon();
    abstract public double getSigma();
    abstract public double getModelPressure();
    abstract public int getMoleculeType();
    abstract public double getModelTemperature();
    abstract public double getNormalizedContainerWidth();
    abstract public double getNormalizedContainerHeight();
    abstract public int getNumMolecules();
    abstract public double getParticleContainerHeight();
    abstract public Rectangle2D getParticleContainerRect();
    abstract public double getPressureInAtmospheres();
    abstract public double getTemperatureInKelvin();
    abstract public int getThermostatType();
    abstract public void injectMolecule();
    abstract public void setMoleculeType(int moleculeID);
    abstract public void setPhase(int state);
    abstract public void setHeatingCoolingAmount(double normalizedHeatingCoolingAmount);
    abstract public void setTargetParticleContainerHeight( double desiredContainerHeight );
    abstract public void setTemperature(double newTemperature);
    abstract public void setThermostatType( int type );


    public static interface Listener {
        
        /**
         * Inform listeners that the model has been reset.
         */
        public void resetOccurred();
        
        /**
         * Inform listeners that a new particle has been added to the model.
         */
        public void particleAdded(StatesOfMatterAtom particle);
        
        /**
         * Inform listeners that the temperature of the system has changed.
         */
        public void temperatureChanged();
        
        /**
         * Inform listeners that the pressure of the system has changed.
         */
        public void pressureChanged();

        /**
         * Inform listeners that the size of the container has changed.
         */
        public void containerSizeChanged();
        
        /**
         * Inform listeners that the type of molecule being simulated has
         * changed.
         */
        public void moleculeTypeChanged();

        /**
         * Inform listeners that the container has exploded.
         */
        public void containerExploded();

    }
    
    public static class Adapter implements Listener {
        public void resetOccurred(){}
        public void particleAdded(StatesOfMatterAtom particle){}
        public void temperatureChanged(){}
        public void pressureChanged(){}
        public void containerSizeChanged(){}
        public void moleculeTypeChanged(){}
        public void containerExploded(){}
    }



}