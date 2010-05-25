/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Random;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;

/**
 * This class represents the main class for modeling the axon.  It acts as the
 * central location where the interaction between the membrane, the particles
 * (i.e. ions), and the gates is all governed.
 *
 * @author John Blanco
 */
public class AxonModel implements IParticleCapture {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final Random RAND = new Random();
	
	// The following constants define the boundaries for the motion of the
	// particles.  These boundaries are intended to be outside the view port,
	// so that it is not apparent to the user that they exist.  We may at some
	// point want to make these bounds dynamic and set by the view so that the
	// user never encounters a situation where these can be seen.
	private static final double MODEL_HEIGHT = 130; // In nanometers.
	private static final double MODEL_WIDTH = 180; // In nanometers.
	private static final Rectangle2D PARTICLE_BOUNDS = new Rectangle2D.Double(-MODEL_WIDTH / 2, -MODEL_HEIGHT / 2,
			MODEL_WIDTH, MODEL_HEIGHT);
	
	// Numbers of the various types of channels that are present on the
	// membrane.
	private static final int NUM_GATED_SODIUM_CHANNELS = 20;
	private static final int NUM_GATED_POTASSIUM_CHANNELS = 20;
	private static final int NUM_SODIUM_LEAK_CHANNELS = 3;
	private static final int NUM_POTASSIUM_LEAK_CHANNELS = 7;
	
	// Numbers of "bulk" ions in and out of the cell when visible.
	private static final int NUM_SODIUM_IONS_OUTSIDE_CELL = 600;
	private static final int NUM_SODIUM_IONS_INSIDE_CELL = 8;
	private static final int NUM_POTASSIUM_IONS_OUTSIDE_CELL = 60;
	private static final int NUM_POTASSIUM_IONS_INSIDE_CELL = 200;
	
	// Nominal concentration values.
	private static final double NOMINAL_SODIUM_EXTERIOR_CONCENTRATION = 145;     // In millimolar (mM)
	private static final double NOMINAL_SODIUM_INTERIOR_CONCENTRATION = 10;      // In millimolar (mM)
	private static final double NOMINAL_POTASSIUM_EXTERIOR_CONCENTRATION = 4;    // In millimolar (mM)
	private static final double NOMINAL_POTASSIUM_INTERIOR_CONCENTRATION = 140;  // In millimolar (mM)
	
	// Rates at which concentration changes during action potential.  These
	// values combined with the conductance at each time step are used to
	// calculate the concentration changes.
	private static final double INTERIOR_CONCENTRATION_CHANGE_RATE_SODIUM = 0.4;
	private static final double EXTERIOR_CONCENTRATION_CHANGE_RATE_SODIUM = 7;
	private static final double INTERIOR_CONCENTRATION_CHANGE_RATE_POTASSIUM = 2.0;
	private static final double EXTERIOR_CONCENTRATION_CHANGE_RATE_POTASSIUM = 0.05;
	
	// Threshold of significant difference for concentration values.
	private static final double CONCENTRATION_DIFF_THRESHOLD = 0.000001;
	
	// Rate at which the concentration moves toward the nominal value when
	// changed.  These are in mM per second of simulation time, and simulation
	// time is slower than real time.
	private static final double INTERIOR_CONCENTRATION_RECOVERY_RATE_FOR_SODIUM = 0.01;     // mM / sec
	private static final double EXTERIOR_CONCENTRATION_RECOVERY_RATE_FOR_SODIUM = 0.2;       // mM / sec
	private static final double INTERIOR_CONCENTRATION_RECOVERY_RATE_FOR_POTASSIUM = 0.2;    // mM / sec
	private static final double EXTERIOR_CONCENTRATION_RECOVERY_RATE_FOR_POTASSIUM = 0.005;  // mM / sec
	
	// Countdown used for preventing the axon from receiving stimuli that
	// are too close together.
	private static final double STIM_LOCKOUT_TIME = 0.01;  // Seconds of sim time.
	
	// Default values of opaqueness for newly created particles.
	private static final double FOREGROUND_PARTICLE_DEFAULT_OPAQUENESS = 0.20;
	private static final double BACKGROUND_PARTICLE_DEFAULT_OPAQUENESS = 0.05;
	
	// Default configuration values.
	private static final boolean DEFAULT_FOR_SHOW_ALL_IONS = true;
	private static final boolean DEFAULT_FOR_MEMBRANE_CHART_VISIBILITY = false;
	private static final boolean DEFAULT_FOR_CHARGES_SHOWN = false;
	private static final boolean DEFAULT_FOR_CONCENTRATION_READOUT_SHOWN = false;
	
	// Value that controls how much of a change of the membrane potential must
	// occur before a notification is sent out.
	private static final double MEMBRANE_POTENTIAL_CHANGE_THRESHOLD = 0.005;
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final ConstantDtClock clock;
    private final AxonMembrane axonMembrane = new AxonMembrane();
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private ArrayList<MembraneChannel> membraneChannels = new ArrayList<MembraneChannel>();
    private final double crossSectionInnerRadius;
    private final double crossSectionOuterRadius;
    private EventListenerList listeners = new EventListenerList();
    private ConcentrationTracker concentrationTracker = new ConcentrationTracker();
    private IHodgkinHuxleyModel hodgkinHuxleyModel = new ModifiedHodgkinHuxleyModel();
    private boolean potentialChartVisible = DEFAULT_FOR_MEMBRANE_CHART_VISIBILITY;
    private boolean allIonsSimulated = DEFAULT_FOR_SHOW_ALL_IONS; // Controls whether all ions, or just those near membrane, are simulated.
    private boolean chargesShown = DEFAULT_FOR_CHARGES_SHOWN; // Controls whether charges are depicted.
    private boolean concentrationReadoutVisible = DEFAULT_FOR_CONCENTRATION_READOUT_SHOWN; // Controls whether concentration readings are depicted.
    private double stimLockoutCountdownTime;
    private double previousMembranePotential = 0;
    private double sodiumInteriorConcentration = NOMINAL_SODIUM_INTERIOR_CONCENTRATION;
	private double sodiumExteriorConcentration = NOMINAL_SODIUM_EXTERIOR_CONCENTRATION;
    private double potassiumInteriorConcentration = NOMINAL_POTASSIUM_INTERIOR_CONCENTRATION;
    private double potassiumExteriorConcentration = NOMINAL_POTASSIUM_EXTERIOR_CONCENTRATION;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AxonModel( NeuronClock clock ) {
    	
        this.clock = clock;
        
        crossSectionInnerRadius = (axonMembrane.getCrossSectionDiameter() - axonMembrane.getMembraneThickness()) / 2; 
        crossSectionOuterRadius = (axonMembrane.getCrossSectionDiameter() + axonMembrane.getMembraneThickness()) / 2;
        
        clock.addClockListener(new ClockAdapter(){
			@Override
			public void clockTicked(ClockEvent clockEvent) {
				stepInTime( clockEvent.getSimulationTimeChange() );
			}
        });
        
        // Listen to the membrane for events that indicate that a traveling
        // action potential has arrived at the location of the transverse
        // cross section.
        axonMembrane.addListener(new AxonMembrane.Adapter() {
			public void travelingActionPotentialReachedCrossSection() {
				// The action potential has arrived, so stimulate the model
				// the simulates the action potential voltages and current
				// flows.
				hodgkinHuxleyModel.stimulate();
			}
		});
        
        addInitialChannels();
        
        // Note: It is expected that the model will be reset once it has been
        // created, and this will set the initial state, including adding the
        // particles to the model.
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public ConstantDtClock getClock() {
        return clock;
    }    
    
    public ArrayList<Particle> getParticles(){
    	return particles;
    }
    
    public AxonMembrane getAxonMembrane(){
    	return axonMembrane;
    }
    
    public Shape getBodyShape(){
    	return new GeneralPath();
    }
    
    public ArrayList<MembraneChannel> getMembraneChannels(){
    	return new ArrayList<MembraneChannel>(membraneChannels);
    }
    
    /* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotential#getMembranePotential()
	 */
    public double getMembranePotential(){
    	return hodgkinHuxleyModel.getMembraneVoltage();
    }
    
    /**
     * Get a reference to the first Hodgkins-Huxley model.  This is used
     * primarily for debugging purposes.
     */
    public IHodgkinHuxleyModel getHodgkinHuxleyModel(){
    	return hodgkinHuxleyModel;
    }

    public double getSodiumInteriorConcentration() {
		return sodiumInteriorConcentration;
	}

	public double getSodiumExteriorConcentration() {
		return sodiumExteriorConcentration;
	}

	public double getPotassiumInteriorConcentration() {
		return potassiumInteriorConcentration;
	}

	public double getPotassiumExteriorConcentration() {
		return potassiumExteriorConcentration;
	}

    public boolean isPotentialChartVisible(){
    	return potentialChartVisible;
    }
    
    public void setPotentialChartVisible(boolean isVisible){
    	if (potentialChartVisible != isVisible){
    		potentialChartVisible = isVisible;
    		notifyPotentialChartVisibilityChanged();
    	}
    }
    
    public boolean isConcentrationReadoutVisible(){
    	return concentrationReadoutVisible;
    }
    
    public void setConcentrationReadoutVisible(boolean isVisible){
    	if (concentrationReadoutVisible != isVisible){
    		concentrationReadoutVisible = isVisible;
    		notifyConcentrationReadoutVisibilityChanged();
    	}
    }
    
    /**
     * For consistency with convention, "is" is used instead of "are".  I know
     * the grammar is bad.  Get over it.
     * 
     * @return
     */
    public boolean isChargesShown(){
    	return chargesShown;
    }
    
    public void setChargesShown(boolean chargesShown){
    	if (this.chargesShown != chargesShown){
    		this.chargesShown = chargesShown;
    		notifyChargesShownChanged();
    	}
    }
    
    /**
     * Return a value indicating whether simulation of all ions is currently
     * turned on in the simulation.  And yes, it would be more grammatically
     * correct to set "areAllIonsSimulated", but we are sticking with the
     * convention for boolean variables.  So get over it.
     */
    public boolean isAllIonsSimulated(){
    	return allIonsSimulated;
    }
    
    /**
     * Set the boolean value that indicates whether all ions are shown in the
     * simulation, or just those that are moving across the membrane.
     * 
     * @param allIonsSimulated
     */
    public void setAllIonsSimulated(boolean allIonsSimulated){
    	// This can only be changed when the stimlus initiation is not locked
    	// out.  Otherwise, particles would come and go during an action
    	// potential, which would be hard to handle and potentially confusing.
    	if (!isStimulusInitiationLockedOut()){
    		if (this.allIonsSimulated != allIonsSimulated){
    			this.allIonsSimulated = allIonsSimulated; 
    			notifyAllIonsSimulatedChanged();
    			if (this.allIonsSimulated){
    				// Add the bulk particles.
    				addInitialBulkParticles();
    			}
    			else{
    				// Remove all particles.
    				removeAllParticles();
    			}
    		}
    	}
    }
    
    
    
    //----------------------------------------------------------------------------
    // Other Methods
    //----------------------------------------------------------------------------
    
    public void reset(){
    	
    	// Reset the axon membrane.
    	axonMembrane.reset();
    	
    	// Remove all existing particles.
    	removeAllParticles();
    	allIonsSimulated = false;
    	
    	// Reset all membrane channels.
    	for (MembraneChannel membraneChannel : membraneChannels){
    		membraneChannel.reset();
    	}
    	
    	// Reset the HH model.
    	hodgkinHuxleyModel.reset();
    	
    	// Reset the concentration readout values.
    	boolean concentrationChanged = false;
    	if (sodiumExteriorConcentration != NOMINAL_SODIUM_EXTERIOR_CONCENTRATION){
    		sodiumExteriorConcentration = NOMINAL_SODIUM_EXTERIOR_CONCENTRATION;
    		concentrationChanged = true;
    	}
    	if (sodiumInteriorConcentration != NOMINAL_SODIUM_INTERIOR_CONCENTRATION){
    		sodiumInteriorConcentration = NOMINAL_SODIUM_INTERIOR_CONCENTRATION;
    		concentrationChanged = true;
    	}
    	if (potassiumExteriorConcentration != NOMINAL_POTASSIUM_EXTERIOR_CONCENTRATION){
    		potassiumExteriorConcentration = NOMINAL_POTASSIUM_EXTERIOR_CONCENTRATION;
    		concentrationChanged = true;
    	}
    	if (potassiumInteriorConcentration != NOMINAL_POTASSIUM_INTERIOR_CONCENTRATION){
    		potassiumInteriorConcentration = NOMINAL_POTASSIUM_INTERIOR_CONCENTRATION;
    		concentrationChanged = true;
    	}
    	if (concentrationChanged){
    		notifyConcentrationChanged();
    	}
    	
    	// Reset the stimulation lockout time.
    	boolean wasLockedOut = isStimulusInitiationLockedOut();
    	stimLockoutCountdownTime = 0;
    	if (wasLockedOut){
    		notifyStimulusLockoutStateChanged();
    	}
    	
    	// Set the membrane chart to its initial state.
    	setPotentialChartVisible(DEFAULT_FOR_MEMBRANE_CHART_VISIBILITY);
    	
    	// Set the concentration readout visibility to its initial state.
    	setConcentrationReadoutVisible(DEFAULT_FOR_CONCENTRATION_READOUT_SHOWN);
    	
    	// Set the visibility of the charge symbols to its initial state.
    	setChargesShown(DEFAULT_FOR_CHARGES_SHOWN);
    	
    	// Set the boolean that controls whether all ions are simulated to its
    	// original state.
    	setAllIonsSimulated(DEFAULT_FOR_SHOW_ALL_IONS);
    }
    
    /**
     * Add the specified particles to the model.
     * 
     * @param particleType
     * @param position
     */
    public void addParticles(ParticleType particleType, ParticlePosition position, int numberToAdd){
    	Particle newParticle = null;
    	for (int i = 0; i < numberToAdd; i++){
    		newParticle = createParticle(particleType);
    		
    		if (position == ParticlePosition.INSIDE_MEMBRANE){
    			positionParticleInsideMembrane(newParticle);
    		}
    		else{
    			positionParticleOutsideMembrane(newParticle);
    		}
    		
    		// Set the opaqueness.
    		if (RAND.nextBoolean()){
    			newParticle.setOpaqueness(FOREGROUND_PARTICLE_DEFAULT_OPAQUENESS);
    		}
    		else {
    			newParticle.setOpaqueness(BACKGROUND_PARTICLE_DEFAULT_OPAQUENESS);
    		}
    		
        	concentrationTracker.updateParticleCount(newParticle.getType(), position, 1);
    	}
    }

    /**
     * Add the specified particles to the given capture zone.
     * 
     * @param particleType
     * @param captureZone
     * @param numberToAdd
     */
    public void addParticles(ParticleType particleType, CaptureZone captureZone, int numberToAdd){
    	Particle newParticle = null;
    	for (int i = 0; i < numberToAdd; i++){
    		newParticle = createParticle(particleType);
    		
    		newParticle.setOpaqueness(FOREGROUND_PARTICLE_DEFAULT_OPAQUENESS);
    		Point2D position = captureZone.getSuggestedNewParticleLocation();
    		newParticle.setPosition(position);
    	}
    }
    
    /**
     * 
     * @return
     */
    public Rectangle2D getParticleMotionBounds(){
    	return PARTICLE_BOUNDS;
    }
    
    public void initiateStimulusPulse(){
    	if (!isStimulusInitiationLockedOut()){
    		axonMembrane.initiateTravelingActionPotential();
    		notifyStimulusPulseInitiated();
    		stimLockoutCountdownTime = STIM_LOCKOUT_TIME;
    		notifyStimulusLockoutStateChanged();
    	}
    }
    
    /**
     * Get a boolean value that indicates whether the initiation of a new
     * stimulus (i.e. action potential) is currently locked out.  This is done
     * to prevent the situation where multiple action potentials are moving
     * down the membrane at the same time.
     * 
     * @return
     */
    public boolean isStimulusInitiationLockedOut(){
    	return (stimLockoutCountdownTime > 0);
    }
    
    /**
     * Create a particle of the specified type in the specified capture zone.
     * In general, this method will be used when a particle is or may soon be
     * needed to travel through a membrane channel.
     *  
     * @param particleType
     * @param captureZone
     * @return
     */
    public Particle createParticle(ParticleType particleType, CaptureZone captureZone){
    	
    	final Particle newParticle = Particle.createParticle(particleType);
    	particles.add(newParticle);
    	if (captureZone != null){
    		Point2D location = captureZone.getSuggestedNewParticleLocation();
    		newParticle.setPosition(location);
    	}
    	
    	// Listen to the particle for notification of its removal.
    	newParticle.addListener(new Particle.Adapter(){
    		public void removedFromModel() {
    			particles.remove(newParticle);
    		}
    	});
    	
    	// Send notification that this particle has come into existence.
    	notifyParticleAdded(newParticle);
    	
		return newParticle;
    }
    
    /**
     * Create a particle of the specified type and add it to the model.
     *  
     * @param particleType
     * @return
     */
    public Particle createParticle(ParticleType particleType){
    	
    	return createParticle(particleType, null);
    }
    
    /**
     * Starts a particle of the specified type moving through the
     * specified channel.  If one or more particles of the needed type exist
     * within the capture zone for this channel, one will be chosen and set to
     * move through, and another will be created to essentially take its place
     * (though the newly created one will probably be in a slightly different
     * place for better visual effect).  If none of the needed particles
     * exist, two will be created, and one will move through the channel and
     * the other will just hang out in the zone.
     * 
     * Note that it is not guaranteed that the particle will make it through
     * the channel, since it is possible that the channel could close before
     * the particle goes through it.
     * 
     * @param particleType
     * @param channel
     * @return
     */
    public void requestParticleThroughChannel(ParticleType particleType, MembraneChannel channel, double maxVelocity, MembraneCrossingDirection direction){

    	CaptureZone captureZone;
    	
    	if (direction == MembraneCrossingDirection.IN_TO_OUT){
    		captureZone = channel.getInteriorCaptureZone();
    	}
    	else{
    		captureZone = channel.getExteriorCaptureZone();
    	}
    		
    	// Scan the capture zone for particles of the desired type.
    	CaptureZoneScanResult czsr = scanCaptureZoneForFreeParticles(captureZone, particleType);
    	Particle particleToCapture = czsr.getClosestFreeParticle();
    	
    	if (czsr.getNumParticlesInZone() == 0){
    		// No particles available in the zone, so create a new one.
    		Particle newParticle = createParticle(particleType, captureZone);
   			particleToCapture = newParticle;
    	}
    	else{
    		// We found a particle to capture, but we should replace it with
    		// another in the same zone.
    		Particle replacementParticle = createParticle(particleType, captureZone);
    		replacementParticle.setMotionStrategy(new SlowBrownianMotionStrategy(replacementParticle.getPositionReference()));
    		replacementParticle.setFadeStrategy(new TimedFadeInStrategy(0.0005, FOREGROUND_PARTICLE_DEFAULT_OPAQUENESS));
    	}
    	
    	// Make the particle to capture fade in.
		particleToCapture.setFadeStrategy(new TimedFadeInStrategy(0.0005));
		
    	// Set a motion strategy that will cause this particle to move across
    	// the membrane.
    	channel.moveParticleThroughNeuronMembrane(particleToCapture, maxVelocity);
    }
    
    private void stepInTime(double dt){
    
    	// Update the value of the membrane potential by stepping the
    	// Hodgkins-Huxley model.
    	hodgkinHuxleyModel.stepInTime( dt );
    	if (Math.abs(previousMembranePotential - hodgkinHuxleyModel.getMembraneVoltage()) > MEMBRANE_POTENTIAL_CHANGE_THRESHOLD){
    		previousMembranePotential = hodgkinHuxleyModel.getMembraneVoltage();
    		notifyMembranePotentialChanged();
    	}
    	
    	// Step the membrane in time.
    	axonMembrane.stepInTime( dt );
    	
    	// Update the stimulus lockout timer.
    	if (stimLockoutCountdownTime >= 0){
    		stimLockoutCountdownTime -= dt;
    		if (stimLockoutCountdownTime <= 0){
    			notifyStimulusLockoutStateChanged();
    		}
    	}
    	
    	// Step the channels.
    	for (MembraneChannel channel : membraneChannels){
    		channel.stepInTime( dt );
    	}
    	
    	// Step the particles.  Since particles may remove themselves as a
    	// result of being stepped, we need to copy the list in order to avoid
    	// concurrent modification exceptions.
    	ArrayList<Particle> particlesCopy = new ArrayList<Particle>(particles);
    	for (Particle particle : particlesCopy){
    		particle.stepInTime( dt );
    	}
    	
    	// Adjust the overall potassium and sodium concentration levels based
    	// parameters of the HH model.  This is done solely to provide values
    	// that can be displayed to the user, and are not used for anything
    	// else in the model.
    	boolean concentrationChanged = false;
    	double potassiumConductance = hodgkinHuxleyModel.get_n4();
    	if (potassiumConductance != 0){
    		// Potassium is moving out of the cell as part of the process of
    		// an action potential, so adjust the interior and exterior
    		// concentration values.
    		potassiumExteriorConcentration += potassiumConductance * dt * EXTERIOR_CONCENTRATION_CHANGE_RATE_POTASSIUM;
    		potassiumInteriorConcentration -= potassiumConductance * dt * INTERIOR_CONCENTRATION_CHANGE_RATE_POTASSIUM;
    		concentrationChanged = true;
    	}
    	else{
    		if (potassiumExteriorConcentration != NOMINAL_POTASSIUM_EXTERIOR_CONCENTRATION){
    			double difference = Math.abs(potassiumExteriorConcentration - NOMINAL_POTASSIUM_EXTERIOR_CONCENTRATION);
    			if (difference < CONCENTRATION_DIFF_THRESHOLD){
    				// Close enough to consider it fully restored.
    				potassiumExteriorConcentration = NOMINAL_POTASSIUM_EXTERIOR_CONCENTRATION;
    			}
    			else{
    				// Move closer to the nominal value.
    				potassiumExteriorConcentration -= difference * 1000 * dt; 
    			}
    			concentrationChanged = true;
    		}
    		if (potassiumInteriorConcentration != NOMINAL_POTASSIUM_INTERIOR_CONCENTRATION){
    			double difference = Math.abs(potassiumInteriorConcentration - NOMINAL_POTASSIUM_INTERIOR_CONCENTRATION);
    			if (difference < CONCENTRATION_DIFF_THRESHOLD){
    				// Close enough to consider it fully restored.
    				potassiumInteriorConcentration = NOMINAL_POTASSIUM_INTERIOR_CONCENTRATION;
    			}
    			else{
    				// Move closer to the nominal value.
    				potassiumInteriorConcentration += difference * 1000 * dt; 
    			}
    			concentrationChanged = true;
    		}
    	}
    	double sodiumConductance = hodgkinHuxleyModel.get_m3h();
    	if (hodgkinHuxleyModel.get_m3h() != 0){
    		// Sodium is moving in to the cell as part of the process of an
    		// action potential, so adjust the interior and exterior
    		// concentration values.
    		sodiumExteriorConcentration -= sodiumConductance * dt * EXTERIOR_CONCENTRATION_CHANGE_RATE_SODIUM;
    		sodiumInteriorConcentration += sodiumConductance * dt * INTERIOR_CONCENTRATION_CHANGE_RATE_SODIUM;
    		concentrationChanged = true;
    	}
    	else{
    		// Move back towards the nominal concentrations.
    		if (sodiumExteriorConcentration != NOMINAL_SODIUM_EXTERIOR_CONCENTRATION){
    			sodiumExteriorConcentration += EXTERIOR_CONCENTRATION_RECOVERY_RATE_FOR_SODIUM * dt;
    			if (sodiumExteriorConcentration > NOMINAL_SODIUM_EXTERIOR_CONCENTRATION){
    				sodiumExteriorConcentration = NOMINAL_SODIUM_EXTERIOR_CONCENTRATION;
    			}
    			concentrationChanged = true;
    		}
    		if (sodiumInteriorConcentration != NOMINAL_SODIUM_INTERIOR_CONCENTRATION){
    			sodiumInteriorConcentration -= INTERIOR_CONCENTRATION_RECOVERY_RATE_FOR_SODIUM * dt;
    			if (sodiumInteriorConcentration < NOMINAL_SODIUM_INTERIOR_CONCENTRATION){
    				sodiumInteriorConcentration = NOMINAL_SODIUM_INTERIOR_CONCENTRATION;
    			}
    			concentrationChanged = true;
    		}
    	}
    	if (concentrationChanged){
    		notifyConcentrationChanged();
    	}
    }
    
    /**
     * Scan the supplied capture zone for particles of the specified type.
     * 
     * @param zone
     * @param particleType
     * @return
     */
    private CaptureZoneScanResult scanCaptureZoneForFreeParticles(CaptureZone zone, ParticleType particleType){
    	Particle closestFreeParticle = null;
    	double distanceOfClosestParticle = Double.POSITIVE_INFINITY;
    	int totalNumberOfParticles = 0;
    	Point2D captureZoneOrigin = zone.getOriginPoint();
    	
    	for (Particle particle : particles){
    		if ((particle.getType() == particleType) && (particle.isAvailableForCapture()) && (zone.isPointInZone(particle.getPositionReference()))) {
    			totalNumberOfParticles++;
    			if (closestFreeParticle == null){
    				closestFreeParticle = particle;
    				distanceOfClosestParticle = captureZoneOrigin.distance(closestFreeParticle.getPositionReference());
    			}
    			else if (captureZoneOrigin.distance(closestFreeParticle.getPosition()) < distanceOfClosestParticle){
    				closestFreeParticle = particle;
    				distanceOfClosestParticle = captureZoneOrigin.distance(closestFreeParticle.getPositionReference());
    			}
    		}
    	}
    	
    	return new CaptureZoneScanResult(closestFreeParticle, totalNumberOfParticles);
    }
    
	public void addListener(Listener listener){
		listeners.add(Listener.class, listener);
	}
	
	public void removeListener(Listener listener){
		listeners.remove(Listener.class, listener);
	}
	
	private void notifyChannelAdded(MembraneChannel channel){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.channelAdded(channel);
		}
	}
	
	private void notifyParticleAdded(Particle particle){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.particleAdded(particle);
		}
	}
	
	private void notifyStimulusPulseInitiated(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.stimulusPulseInitiated();
		}
	}
	
	private void notifyMembranePotentialChanged(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.membranePotentialChanged();
		}
	}
	
	private void notifyConcentrationReadoutVisibilityChanged(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.concentrationReadoutVisibilityChanged();
		}
	}
	
	private void notifyPotentialChartVisibilityChanged(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.potentialChartVisibilityChanged();
		}
	}
	
	private void notifyChargesShownChanged(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.chargesShownChanged();
		}
	}
	
	private void notifyAllIonsSimulatedChanged(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.allIonsSimulatedChanged();
		}
	}
	
	private void notifyStimulusLockoutStateChanged(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.stimulationLockoutStateChanged();
		}
	}
	
	private void notifyConcentrationChanged(){
		for (Listener listener : listeners.getListeners(Listener.class)){
			listener.concentrationChanged();
		}
	}
	
	private void addInitialChannels(){
		
    	// Add the initial channels.  The pattern is intended to be such that
    	// the potassium and sodium gated channels are right next to each
    	// other, with occasional leak channels interspersed.  There should
    	// be one or more of each type of channel on the top of the membrane
    	// so when the user zooms in, they can see all types.
    	double angle  = Math.PI * 0.45;
    	double totalNumChannels = NUM_GATED_SODIUM_CHANNELS + NUM_GATED_POTASSIUM_CHANNELS + NUM_SODIUM_LEAK_CHANNELS +
    		NUM_POTASSIUM_LEAK_CHANNELS;
    	double angleIncrement = Math.PI * 2 / totalNumChannels;
    	int gatedSodiumChansAdded = 0;
    	int gatedPotassiumChansAdded = 0;
    	int sodiumLeakChansAdded = 0;
    	int potassiumLeakChansAdded = 0;
    	
    	// Add some of each type so that they are visible at the top portion
    	// of the membrane.
    	if (NUM_SODIUM_LEAK_CHANNELS > 0){
    		addChannel(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL, angle);
    		sodiumLeakChansAdded++;
    		angle += angleIncrement;
    	}
    	if (NUM_GATED_POTASSIUM_CHANNELS > 0){
    		addChannel(MembraneChannelTypes.POTASSIUM_GATED_CHANNEL, angle);
    		gatedPotassiumChansAdded++;
    		angle += angleIncrement;
    	}
    	if (NUM_GATED_SODIUM_CHANNELS > 0){
    		addChannel(MembraneChannelTypes.SODIUM_GATED_CHANNEL, angle);
    		gatedSodiumChansAdded++;
    		angle += angleIncrement;
    	}
    	if (NUM_POTASSIUM_LEAK_CHANNELS > 0){
    		addChannel(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL, angle);
    		potassiumLeakChansAdded++;
    		angle += angleIncrement;
    	}
    	
    	// Now loop through the rest of the membrane's circumference adding
    	// the various types of gates.
    	for (int i = 0; i < totalNumChannels - 4; i++){
    		// Calculate the "urgency" for each type of gate.
    		double gatedSodiumUrgency = (double)NUM_GATED_SODIUM_CHANNELS / gatedSodiumChansAdded;
    		double gatedPotassiumUrgency = (double)NUM_GATED_POTASSIUM_CHANNELS / gatedPotassiumChansAdded ;
    		double potassiumLeakUrgency = (double)NUM_POTASSIUM_LEAK_CHANNELS / potassiumLeakChansAdded;
    		double sodiumLeakUrgency = (double)NUM_SODIUM_LEAK_CHANNELS / sodiumLeakChansAdded;
    		MembraneChannelTypes channelTypeToAdd = null;
			if (gatedSodiumUrgency >= gatedPotassiumUrgency
					&& gatedSodiumUrgency >= potassiumLeakUrgency
					&& gatedSodiumUrgency >= sodiumLeakUrgency) {
				// Add a gated sodium channel.
				channelTypeToAdd = MembraneChannelTypes.SODIUM_GATED_CHANNEL;
				gatedSodiumChansAdded++;
			} else if (gatedPotassiumUrgency > gatedSodiumUrgency
					&& gatedPotassiumUrgency >= potassiumLeakUrgency
					&& gatedPotassiumUrgency >= sodiumLeakUrgency) {
				// Add a gated potassium channel.
				channelTypeToAdd = MembraneChannelTypes.POTASSIUM_GATED_CHANNEL;
				gatedPotassiumChansAdded++;
			} else if (potassiumLeakUrgency > gatedSodiumUrgency
					&& potassiumLeakUrgency > gatedPotassiumUrgency
					&& potassiumLeakUrgency >= sodiumLeakUrgency) {
				// Add a potassium leak channel.
				channelTypeToAdd = MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL;
				potassiumLeakChansAdded++;
			} else if (sodiumLeakUrgency > gatedSodiumUrgency
					&& sodiumLeakUrgency > gatedPotassiumUrgency
					&& sodiumLeakUrgency > potassiumLeakUrgency) {
				// Add a sodium leak channel.
				channelTypeToAdd = MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL;
				sodiumLeakChansAdded++;
			}
			else{
				assert false; // Should never get here, so debug if it does.
			}
    		
	    	addChannel(channelTypeToAdd, angle);
	    	angle += angleIncrement;
    	}
	}
    
	/**
	 * Add the provided channel at the specified rotational location.
	 * Locations are specified in terms of where on the circle of the membrane
	 * they are, with a value of 0 being on the far right, PI/2 on the top,
	 * PI on the far left, etc.
	 */
    private void addChannel(MembraneChannelTypes membraneChannelType, double angle){
    	
    	MembraneChannel membraneChannel = MembraneChannel.createMembraneChannel(membraneChannelType, this,
    			hodgkinHuxleyModel);
    	double radius = axonMembrane.getCrossSectionDiameter() / 2;
		Point2D newLocation = new Point2D.Double(radius * Math.cos(angle), radius * Math.sin(angle));
    	
    	// Position the channel on the membrane.
    	membraneChannel.setRotationalAngle(angle);
    	membraneChannel.setCenterLocation(newLocation);

    	// Add the channel and let everyone know it exists.
    	membraneChannels.add(membraneChannel);
    	notifyChannelAdded(membraneChannel);
    }
    
    /**
     * Place a particle at a random location inside the axon membrane.
     */
    private void positionParticleInsideMembrane(Particle particle){
    	// Choose any angle.
    	double angle = RAND.nextDouble() * Math.PI * 2;
    	
    	// Choose a distance from the cell center that is within the membrane.
    	// The multiplier value is created with the intention of weighting the
    	// positions toward the outside in order to get an even distribution
    	// per unit area.
    	double multiplier = Math.max(RAND.nextDouble(), RAND.nextDouble());
    	double distance = (crossSectionInnerRadius - particle.getDiameter()) * multiplier;
    	
    	particle.setPosition(distance * Math.cos(angle), distance * Math.sin(angle));
    }

    /**
     * Place a particle at a random location outside the axon membrane.
     */
    private void positionParticleOutsideMembrane(Particle particle){
    	// Choose any angle.
    	double angle = RAND.nextDouble() * Math.PI * 2;
    	
    	// Choose a distance from the cell center that is outside of the
    	// membrane. The multiplier value is created with the intention of
    	// weighting the positions toward the outside in order to get an even
    	// distribution per unit area.
    	double multiplier = RAND.nextDouble();
    	double distance = crossSectionOuterRadius + particle.getDiameter() * 2 + 
			multiplier * crossSectionOuterRadius * 2.2;
    	
    	particle.setPosition(distance * Math.cos(angle), distance * Math.sin(angle));
    }
    
    /**
     * Add the "bulk particles", which are particles that are inside and
     * outside of the membrane and, except in cases where they happen to end
     * up positioned close to the membrane, they generally stay where
     * initially positioned. 
     */
    private void addInitialBulkParticles(){
    	// Make a list of pre-existing particles.
    	ArrayList<Particle> preExistingParticles = new ArrayList<Particle>(particles);
    	
    	// Add the initial particles.
    	addParticles(ParticleType.SODIUM_ION, ParticlePosition.INSIDE_MEMBRANE, NUM_SODIUM_IONS_INSIDE_CELL);
    	addParticles(ParticleType.SODIUM_ION, ParticlePosition.OUTSIDE_MEMBRANE, NUM_SODIUM_IONS_OUTSIDE_CELL);
    	addParticles(ParticleType.POTASSIUM_ION, ParticlePosition.INSIDE_MEMBRANE, NUM_POTASSIUM_IONS_INSIDE_CELL);
    	addParticles(ParticleType.POTASSIUM_ION, ParticlePosition.OUTSIDE_MEMBRANE, NUM_POTASSIUM_IONS_OUTSIDE_CELL);
    	
    	// Look at each sodium gate and, if there are no ions in its capture
    	// zone, add some.
    	for (MembraneChannel membraneChannel : membraneChannels){
    		if (membraneChannel instanceof SodiumDualGatedChannel){
    			CaptureZone captureZone = membraneChannel.getExteriorCaptureZone();
    			CaptureZoneScanResult czsr = scanCaptureZoneForFreeParticles(captureZone, ParticleType.SODIUM_ION);
    			if (czsr.numParticlesInZone == 0){
    				addParticles(ParticleType.SODIUM_ION, captureZone, RAND.nextInt(2) + 1);
    			}
    		}
    	}
    	
    	// Set all new particles to exhibit simple Brownian motion.
    	for (Particle particle : particles){
    		if (!preExistingParticles.contains(particle)){
    			particle.setMotionStrategy(new SlowBrownianMotionStrategy(particle.getPositionReference()));
    		}
    	}
    }
    
    /**
     * Remove all particles (i.e. ions) from the simulation.
     */
    private void removeAllParticles(){
    	// Remove all particles.  This is done by telling each particle to
    	// send out notifications of its removal from the model.  All
    	// listeners, including this class, should remove their references in
    	// response.
    	ArrayList<Particle> particlesCopy = new ArrayList<Particle>(particles);
    	for (Particle particle : particlesCopy){
    		particle.removeFromModel();
    	}
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
    
    /**
     * This is a "convenience class" that is used to track the relative
     * concentration of the different particle types.  This was created so that
     * the concentration doesn't need to be completely recalculated at every
     * time step, which would be computationally expensive.
     */
    public static class ConcentrationTracker {

    	HashMap<ParticleType, Integer> mapParticleTypeToNumOutside = new HashMap<ParticleType, Integer>();
    	HashMap<ParticleType, Integer> mapParticleTypeToNumInside = new HashMap<ParticleType, Integer>();
    	
    	public void updateParticleCount(ParticleType particleType, ParticlePosition position, int delta){
    		HashMap<ParticleType, Integer> map = position == ParticlePosition.INSIDE_MEMBRANE ? mapParticleTypeToNumInside :
    			mapParticleTypeToNumOutside;
    		Integer currentCount = map.get(particleType);
    		if (currentCount == null){
    			currentCount = new Integer(0);
    		}
    		Integer newCount = new Integer(currentCount.intValue() + delta);
    		if (newCount.intValue() < 0){
    			System.err.println(getClass().getName()+ "- Error: Negative count for particles in a position.");
    			assert false;
    			newCount = new Integer(0);
    		}
    		map.put(particleType, newCount);
    	}
    	
    	public void resetParticleCount(ParticleType particleType, ParticlePosition position){
    		HashMap<ParticleType, Integer> map = position == ParticlePosition.INSIDE_MEMBRANE ? mapParticleTypeToNumInside :
    			mapParticleTypeToNumOutside;
    		map.put(particleType, new Integer(0));
    	}
    	
    	public int getNumParticlesInPosition(ParticleType particleType, ParticlePosition position){
    		HashMap<ParticleType, Integer> map = position == ParticlePosition.INSIDE_MEMBRANE ? mapParticleTypeToNumInside :
    			mapParticleTypeToNumOutside;
    		Integer currentCount = map.get(particleType);
    		if (currentCount == null){
    			currentCount = new Integer(0);
    		}
    		return currentCount.intValue();
    	}
    	
    	public int getTotalNumParticles(ParticleType particleType){
    		return (getNumParticlesInPosition(particleType, ParticlePosition.INSIDE_MEMBRANE) + 
    				getNumParticlesInPosition(particleType, ParticlePosition.OUTSIDE_MEMBRANE));
    	}
    	
    	public double getProportion(ParticleType particleType, ParticlePosition position){
    		Integer insideCount = mapParticleTypeToNumInside.get(particleType);
    		if (insideCount == null){
    			insideCount = new Integer(0);
    		}
    		Integer outsideCount = mapParticleTypeToNumOutside.get(particleType);
    		if (outsideCount == null){
    			outsideCount = new Integer(0);
    		}

    		if (insideCount.intValue() == outsideCount.intValue() && insideCount.intValue() == 0){
    			return 0;
    		}
    		else if (position == ParticlePosition.INSIDE_MEMBRANE){
    			return insideCount.doubleValue() / (insideCount.doubleValue() + outsideCount.doubleValue());
    		}
    		else {
    			return outsideCount.doubleValue() / (insideCount.doubleValue() + outsideCount.doubleValue());
    		}
    	}
    }

    /**
     * A class for reporting the closest particle to the origin in a capture
     * zone and the total number of particles in the zone.
     */
    public static class CaptureZoneScanResult {
    	final Particle closestFreeParticle;
    	final int numParticlesInZone;
		public CaptureZoneScanResult(Particle closestParticle,
				int numParticlesInZone) {
			super();
			this.closestFreeParticle = closestParticle;
			this.numParticlesInZone = numParticlesInZone;
		}
		protected Particle getClosestFreeParticle() {
			return closestFreeParticle;
		}
		protected int getNumParticlesInZone() {
			return numParticlesInZone;
		}
    }
    
    public interface Listener extends EventListener {
    	/**
    	 * Notification that a channel was added.
    	 * 
    	 * @param channel - Channel that was added.
    	 */
    	public void channelAdded(MembraneChannel channel);
    	
    	/**
    	 * Notification that a particle was added.
    	 * 
    	 * @param particle - Particle that was added.
    	 */
    	public void particleAdded(Particle particle);
    	
    	/**
    	 * Notification that a stimulus pulse has been initiated.
    	 */
    	public void stimulusPulseInitiated();
    	
    	/**
    	 * Notification that the membrane potential has changed.
    	 */
    	public void membranePotentialChanged();
    	
    	/**
    	 * Notification that the setting for the visibility of the membrane
    	 * potential chart has changed.
    	 */
    	public void potentialChartVisibilityChanged();
    	
    	/**
    	 * Notification that the setting for the visibility of the
    	 * concentration information has changed.
    	 */
    	public void concentrationReadoutVisibilityChanged();
    	
    	/**
    	 * Notification that the setting for whether or not the charges are
    	 * shown has changed.
    	 */
    	public void chargesShownChanged();
    	
    	/**
    	 * Notification that the setting for whether or not all ions are
    	 * included in the simulation has changed.
    	 */
    	public void allIonsSimulatedChanged();
    	
    	/**
    	 * Notification that the state of stimulation lockout, which prevents
    	 * stimuli from being initiated too close together, has changed.
    	 */
    	public void stimulationLockoutStateChanged();

    	/**
    	 * Notification that the concentration of one or more of the ions
    	 * has changed.
    	 */
    	public void concentrationChanged();
    }
    
    public static class Adapter implements Listener{
		public void channelAdded(MembraneChannel channel) {}
		public void particleAdded(Particle particle) {}
		public void stimulusPulseInitiated() {}
		public void potentialChartVisibilityChanged() {}
		public void stimulationLockoutStateChanged() {}
		public void allIonsSimulatedChanged() {}
		public void chargesShownChanged() {}
		public void membranePotentialChanged() {}
		public void concentrationChanged() {}
		public void concentrationReadoutVisibilityChanged() {}
    }
}
