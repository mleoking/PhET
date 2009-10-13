/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.neuron.NeuronConstants;

/**
 * This class represents the main class for modeling the axon.  It acts as the
 * central location where the interaction between the membrane, the particles
 * (i.e. ions), and the gates is all governed.
 *
 * @author John Blanco
 */
public class AxonModel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final Random RAND = new Random();
	
	private static final int TOTAL_INITIAL_PARTICLES = 120;
	
	private static final double MAX_PARTICLE_VELOCITY = 500; // In nano meters per second.
	
	// The following constant defines how frequently particle motion is updated.
	// A value of 1 means every clock tick for every particle, 2 means every other
	// particle on each tick, etc.
	private static final int PARTICLE_UPDATE_INCREMENT = 4;
	
	// The following constants define the boundaries for the motion of the
	// particles.  These boundaries are intended to be outside the view port,
	// so that it is not apparent to the user that they exist.  We may at some
	// point want to make these bounds dynamic and set by the view so that the
	// user never encounters a situation where these can be seen.
	private static final double MODEL_HEIGHT = 130; // In nanometers.
	private static final double MODEL_WIDTH = 180; // In nanometers.
	private static final Rectangle2D PARTICLE_BOUNDS = new Rectangle2D.Double(-MODEL_WIDTH / 2, -MODEL_HEIGHT / 2,
			MODEL_WIDTH, MODEL_HEIGHT);
	
	// Center of the model.
	private static final Point2D CENTER_POS = new Point2D.Double(0, 0);
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final NeuronClock clock;
    private final AxonMembrane axonMembrane = new AxonMembrane();
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private ArrayList<AbstractMembraneChannel> channels = new ArrayList<AbstractMembraneChannel>();
    private final double crossSectionInnerRadius;
    private final double crossSectionOuterRadius;
    private int particleUpdateOffset = 0;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private ConcentrationTracker concentrationTracker = new ConcentrationTracker();

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
				handleClockTicked(clockEvent);
			}
        });
        
        // Add the particles.
        // TODO: This is probably not correct, but for now assume that
        // the concentration of Na and K is equal and that both are equally
        // distributed inside and outside of the membrane.
        /*
        int i = TOTAL_INITIAL_PARTICLES;
        Particle newParticle;
        while (true){
        	newParticle = new PotassiumIon();
        	positionParticleInsideMembrane(newParticle);
        	particles.add(newParticle);
        	concentrationTracker.updateParticleCount(ParticleType.POTASSIUM_ION, ParticlePosition.INSIDE_MEMBRANE, 1);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newParticle = new SodiumIon();
        	positionParticleInsideMembrane(newParticle);
        	particles.add(newParticle);
        	concentrationTracker.updateParticleCount(ParticleType.SODIUM_ION, ParticlePosition.INSIDE_MEMBRANE, 1);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newParticle = new PotassiumIon();
        	positionParticleOutsideMembrane(newParticle);
        	particles.add(newParticle);
        	concentrationTracker.updateParticleCount(ParticleType.POTASSIUM_ION, ParticlePosition.OUTSIDE_MEMBRANE, 1);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newParticle = new SodiumIon();
        	positionParticleOutsideMembrane(newParticle);
        	particles.add(newParticle);
        	concentrationTracker.updateParticleCount(ParticleType.SODIUM_ION, ParticlePosition.OUTSIDE_MEMBRANE, 1);
        	i--;
        	if (i == 0){
        		break;
        	}
        }
        */
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public NeuronClock getClock() {
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
    
    public ArrayList<AbstractMembraneChannel> getMembraneChannels(){
    	return new ArrayList<AbstractMembraneChannel>(channels);
    }
    
    public int getNumMembraneChannels(MembraneChannelTypes channelType){
    	
    	int numChannels = 0;
    	
    	for (AbstractMembraneChannel channel : channels){
    		if (channel.getChannelType() == channelType){
    			numChannels++;
    		}
    	}
    	
    	return numChannels;
    }

    public void setNumMembraneChannels(MembraneChannelTypes channelType, int desiredNumChannesl){
    	if (desiredNumChannesl > NeuronConstants.MAX_CHANNELS_PER_TYPE){
    		System.err.println(getClass().getName() + "- Warning: Attempt to set too many channels.");
    		assert false;
    		return;
    	}
    	
    	if (desiredNumChannesl < getNumMembraneChannels(channelType)){
    		// Need to remove one or more channels.
    		while (desiredNumChannesl < getNumMembraneChannels(channelType)){
    			removeChannel(channelType);
    		}
    	}
    	else if (desiredNumChannesl > getNumMembraneChannels(channelType)){
    		// Need to add one or more channels.
    		while (desiredNumChannesl > getNumMembraneChannels(channelType)){
    			addChannel(channelType);
    		}
    	}
    	else{
    		// Don't need to do nuthin'.
    	}
    }
    
    //----------------------------------------------------------------------------
    // Other Methods
    //----------------------------------------------------------------------------
    
    public void reset(){
    	// Remove all channels.
    	ArrayList<AbstractMembraneChannel> tempChannelList = new ArrayList<AbstractMembraneChannel>(channels);
    	for ( AbstractMembraneChannel channel : tempChannelList){
    		removeChannel(channel.getChannelType());
    	}
    	
    	// Move the particles to the appropriate initial locations by setting
    	// the target proportions.
    	setConcentration(ParticleType.SODIUM_ION, 0.5);
    	setConcentration(ParticleType.POTASSIUM_ION, 0.5);
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
    		switch (particleType){
    		case POTASSIUM_ION:
            	newParticle = new PotassiumIon();
            	break;
    		case SODIUM_ION:
            	newParticle = new SodiumIon();
            	break;
    		case PROTEIN_ION:
            	newParticle = new ProteinIon();
            	break;
    		default:
    			System.err.println("Error - Unrecognized particle type.");
    			assert false;
    		}
    		
    		if (position == ParticlePosition.INSIDE_MEMBRANE){
    			positionParticleInsideMembrane(newParticle);
    		}
    		else{
    			positionParticleOutsideMembrane(newParticle);
    		}
        	particles.add(newParticle);
        	concentrationTracker.updateParticleCount(newParticle.getType(), position, 1);
    	}
    }

    /**
     * Get the proportion of particles of a given type that are inside the
     * axon membrane.  A value of 1 indicates that all particles are inside, 0
     * means that none are inside.
     */
    public double getProportionOfParticlesInside(ParticleType particleType){
    	return concentrationTracker.getProportion(particleType, ParticlePosition.INSIDE_MEMBRANE);
    }
    
    /**
     * 
     * @return
     */
    public Rectangle2D getParticleMotionBounds(){
    	return PARTICLE_BOUNDS;
    }
    
    /**
     * Set the proportion of particles inside the axon membrane.  A value of 0
     * indicates that all particles of this type should be outside, a value of 1
     * indicates that the should all be inside, and value between...well, you
     * get the idea.
     */
    public void setConcentration(ParticleType particleType, double targetProportion){
    	
    	if (targetProportion > 1 || targetProportion < 0){
    		System.err.println(getClass().getName() + " - Error: Invalid target proportion value = " + targetProportion);
    		assert false;
    		return; 
    	}

    	int targetNumInside = (int)Math.round(targetProportion * 
    			(double)(concentrationTracker.getTotalNumParticles(particleType)));
    	
    	if (targetNumInside > concentrationTracker.getNumParticlesInPosition(particleType, 
    			ParticlePosition.INSIDE_MEMBRANE)){
    		// Move some particles from outside to inside.
    		for (Particle particle : particles){
    			if (particle.getType() == particleType && !isParticleInside(particle)){
    				// Move this guy in.
    				positionParticleInsideMembrane(particle);
    				concentrationTracker.updateParticleCount(particleType, ParticlePosition.INSIDE_MEMBRANE, 1);
    				concentrationTracker.updateParticleCount(particleType, ParticlePosition.OUTSIDE_MEMBRANE, -1);
    				if (concentrationTracker.getNumParticlesInPosition(particleType, ParticlePosition.INSIDE_MEMBRANE) == targetNumInside){
    					break;
    				}
    			}
    		}
    		notifyConcentrationGradientChanged(particleType);
    	}
    	else if (targetNumInside < concentrationTracker.getNumParticlesInPosition(particleType, ParticlePosition.INSIDE_MEMBRANE)){
    		// Move some particles from inside to outside.
    		for (Particle particle : particles){
    			if (particle.getType() == particleType && isParticleInside(particle)){
    				// Move this guy out.
    				positionParticleOutsideMembrane(particle);
    				concentrationTracker.updateParticleCount(particleType, ParticlePosition.INSIDE_MEMBRANE, -1);
    				concentrationTracker.updateParticleCount(particleType, ParticlePosition.OUTSIDE_MEMBRANE, 1);
    				if (concentrationTracker.getNumParticlesInPosition(particleType, ParticlePosition.INSIDE_MEMBRANE) == targetNumInside){
    					break;
    				}
    			}
    		}
    		notifyConcentrationGradientChanged(particleType);
    	}
    }
    
    private void handleClockTicked(ClockEvent clockEvent){
    	
    	for (int i = particleUpdateOffset; i < particles.size(); i += PARTICLE_UPDATE_INCREMENT){
    		updateParticleVelocity(particles.get(i));
    	}
    	
    	particleUpdateOffset = (particleUpdateOffset + 1) % PARTICLE_UPDATE_INCREMENT;
    	
    	for (Particle particle : particles){
    		particle.stepInTime(clockEvent.getSimulationTimeChange());
    	}
    	
    	for (AbstractMembraneChannel channel : channels){
    		channel.stepInTime(clockEvent.getSimulationTimeChange());
    		ArrayList<Particle> particlesTakenByChannel = channel.checkTakeControlParticles(particles);
    		if (particlesTakenByChannel != null){
    			for (Particle particle : particlesTakenByChannel){
    				particles.remove(particle);
    				if (particle.getPositionReference().distance(CENTER_POS) > crossSectionOuterRadius){
    					// This particle was outside and, now that it has been
    					// captured by a channel, is considered to be inside.
    					concentrationTracker.updateParticleCount(particle.getType(), ParticlePosition.OUTSIDE_MEMBRANE, -1);
    					concentrationTracker.updateParticleCount(particle.getType(), ParticlePosition.INSIDE_MEMBRANE, 1);
    					notifyConcentrationGradientChanged(particle.getType());
    				}
    			}
    		}
    		ArrayList<Particle> particlesReleasedByChannel = channel.checkReleaseControlParticles(particles);
    		if (particlesReleasedByChannel != null){
    			for (Particle particle : particlesReleasedByChannel){
    				particles.add(particle);
    				if (particle.getPositionReference().distance(CENTER_POS) > crossSectionOuterRadius){
    					// This particle was inside a channel and was released
    					// outside the membrane.
    					concentrationTracker.updateParticleCount(particle.getType(), ParticlePosition.OUTSIDE_MEMBRANE, 1);
    					concentrationTracker.updateParticleCount(particle.getType(), ParticlePosition.INSIDE_MEMBRANE, -1);
    					notifyConcentrationGradientChanged(particle.getType());
    				}
    			}
    		}
    	}
    }
    
	public void addListener(Listener listener){
		listeners.add(listener);
	}
	
	public void removeListener(Listener listener){
		listeners.remove(listener);
	}
	
	private void notifyChannelAdded(AbstractMembraneChannel channel){
		for (Listener listener : listeners){
			listener.channelAdded(channel);
		}
	}
	
	private void notifyChannelRemoved(AbstractMembraneChannel channel){
		for (Listener listener : listeners){
			listener.channelRemoved(channel);
		}
	}
	
	private void notifyConcentrationGradientChanged(ParticleType particleType){
		for (Listener listener : listeners){
			listener.concentrationRatioChanged(particleType);
		}
	}
	
    private void updateParticleVelocity(Particle particle){
    	
    	// Convert the position to polar coordinates.
    	double r = Math.sqrt(particle.getX() * particle.getX() + particle.getY() * particle.getY());
    	double theta = Math.atan2(particle.getY(), particle.getX());
    	
    	// Determine the current angle of travel.
    	double previousAngleOfTravel = Math.atan2( particle.getPositionReference().y, particle.getPositionReference().x );
    	
    	double angle = 0;
    	double velocity;

    	// Generate the new angle of travel for the particle.
    	if (r < axonMembrane.getCrossSectionDiameter() / 2){
    		// Particle is inside the membrane.
    		if (crossSectionInnerRadius - r <= particle.getDiameter()){
    			// This particle is near the membrane wall, so should be repelled.
    	    	angle = theta + Math.PI + ((RAND.nextDouble() - 0.5) * Math.PI / 2);
    		}
    		else{
    			// Particle should just do a random walk.
				angle = Math.PI * 2 * RAND.nextDouble();
    		}
    	}
    	else{
    		// Particle is outside the membrane.
    		if (r - crossSectionOuterRadius <= particle.getDiameter()){
    			// This particle is near the membrane wall, so should be repelled.
				angle = Math.PI * RAND.nextDouble() - Math.PI / 2 + theta;
    		}
    		else if (!PARTICLE_BOUNDS.contains(particle.getPositionReference())){
    			// Particle is moving out of bounds, so move it back towards
    			// the center.
    	    	angle = theta + Math.PI + ((RAND.nextDouble() - 0.5) * Math.PI / 2);
    	    	angle = theta + Math.PI;
    		}
    		else{
    			// Particle should just do a random walk.
				angle = Math.PI * 2 * RAND.nextDouble();
    		}
    	}
    	
    	// Generate the new overall velocity.
		velocity = MAX_PARTICLE_VELOCITY * RAND.nextDouble();
		
		// Set the particle's new velocity. 
    	particle.setVelocity(velocity * Math.cos(angle), velocity * Math.sin(angle));
    }
    
    private void addChannel(MembraneChannelTypes channelType){
    	AbstractMembraneChannel membraneChannel = null;
    	
    	if (getNumMembraneChannels(channelType) > NeuronConstants.MAX_CHANNELS_PER_TYPE){
    		System.err.println(getClass().getName() + " - Warning: Ignoring attempt to add more than max allowed channels.");
    		assert false;
    		return;
    	}
    	
    	switch (channelType){
    	case SODIUM_LEAKAGE_CHANNEL:
    		membraneChannel = new SodiumLeakageChannel();
    		break;
    		
    	case POTASSIUM_LEAKAGE_CHANNEL:
    		membraneChannel = new PotassiumLeakageChannel();
    		break;
    	}
    	
    	// Find a position for the new channel.
    	double angleOffset = channelType == MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL ? 0 : 
    		Math.PI / NeuronConstants.MAX_CHANNELS_PER_TYPE;
    	/*
    	double angle = 0;
    	double radius = axonMembrane.getCrossSectionDiameter() / 2;
    	Point2D newLocation = new Point2D.Double();
    	boolean foundOpenSpot = false;
    	for (int i = 0; i < NeuronConstants.MAX_CHANNELS_PER_TYPE && !foundOpenSpot; i++){
    		angle = i * 2 * Math.PI / NeuronConstants.MAX_CHANNELS_PER_TYPE + angleOffset + (i % 2) * Math.PI;
    		newLocation = new Point2D.Double(radius * Math.cos(angle), radius * Math.sin(angle));
    		// Make sure this position isn't already taken.
    		foundOpenSpot = true;
    		for (AbstractMembraneChannel channel : channels){
    			if (channel.getCenterLocation().distance(newLocation) < 1){
    				foundOpenSpot = false;
    				break;
    			}
    		}
    	}
    	*/
    	int numChannelsOfThisType = getNumMembraneChannels(channelType);
    	double angle = (double)(numChannelsOfThisType % 2) * Math.PI + 
    		(double)(numChannelsOfThisType / 2) * 2 * Math.PI / NeuronConstants.MAX_CHANNELS_PER_TYPE + angleOffset;
    	double radius = axonMembrane.getCrossSectionDiameter() / 2;
		Point2D newLocation = new Point2D.Double(radius * Math.cos(angle), radius * Math.sin(angle));
    	
    	// Position the channel on the membrane.
    	membraneChannel.setRotationalAngle(angle);
    	membraneChannel.setCenterLocation(newLocation);

    	// Add the channel and let everyone know it exists.
    	channels.add(membraneChannel);
    	notifyChannelAdded(membraneChannel);
    }
    
    private void removeChannel(MembraneChannelTypes channelType){
    	// Make sure there is at least one to remove.
    	if (getNumMembraneChannels(channelType) < 1 ){
    		System.err.println(getClass().getName() + ": Error - No channel of this type to remove, type = " + channelType);
    		return;
    	}
    	
    	AbstractMembraneChannel channelToRemove = null;
    	// Work backwards through the array so that the most recently added
    	// channel is removed first.  This just looks better visually and
    	// makes the positioning of channels work better.
    	for (int i = channels.size() - 1; i >= 0; i--){
    		if (channels.get(i).getChannelType() == channelType){
    			channelToRemove = channels.get(i);
    			break;
    		}
    	}
    	
    	if (channelToRemove != null){
    		ArrayList<Particle> releasedParticle = channelToRemove.forceReleaseAllParticles(particles);
    		// Since particles in a channel are considered to be inside the
    		// membrane, force any particle that was in the channel when it was
    		// removed to go safely into the interior.
    		if (releasedParticle != null){
    			for (Particle particle : releasedParticle){
    				particles.add(particle);
    				positionParticleInsideMembrane(particle);
    			}
    		}
    		// Remove the channel and send any notifications.
    		channels.remove(channelToRemove);
    		channelToRemove.remove();
    		notifyChannelRemoved(channelToRemove);
    	}
    }
    
    /**
     * Place a particle at a random location inside the axon membrane.
     */
    private void positionParticleInsideMembrane(Particle particle){
    	// Choose any angle.
    	double angle = RAND.nextDouble() * Math.PI * 2;
    	
    	// Choose a distance.  This is calculated so that there is a higher
    	// tendency to be towards the outside, otherwise things look too
    	// concentrated in the middle.
    	double skewedRand = -Math.pow(RAND.nextDouble() - 1, 2) + 1;
    	double distance = skewedRand * crossSectionInnerRadius - particle.getDiameter();
    	
    	/*
    	 * TODO: The code below was used prior to 10/9/2009, which is when it
    	 * was decided that the atoms should be evenly distributed within the
    	 * membrane, not tending towards the outside.  Keep it until we're
    	 * sure we don't need it, then blow it away.
    	double multiplier = 0;
    	if (RAND.nextDouble() < 0.8){
    		multiplier = 1 - (RAND.nextDouble() * 0.25);
    	}
    	else{
    		multiplier = RAND.nextDouble();
    	}
    	double distance = multiplier * (crossSectionInnerRadius - atom.getDiameter());
    	*/
    	particle.setPosition(distance * Math.cos(angle), distance * Math.sin(angle));
    }

    /**
     * Place a particle at a random location outside the axon membrane.
     */
    private void positionParticleOutsideMembrane(Particle particle){
    	double maxDistance = CENTER_POS.distance(new Point2D.Double(getParticleMotionBounds().getMinX(), 
    			getParticleMotionBounds().getMinY()));
		double angle = RAND.nextDouble() * Math.PI * 2;
		Point2D location = new Point2D.Double();
    	while (true){
    		double distance = RAND.nextDouble() * (maxDistance - crossSectionOuterRadius) + crossSectionOuterRadius +
    			particle.getDiameter();
    		location.setLocation(distance * Math.cos(angle), distance * Math.sin(angle));
    		if (getParticleMotionBounds().contains(location)){
    			// This works.
    			break;
    		}
    	}
    	particle.setPosition(location);
    }
    
    /**
     * Determine whether the given particle is considered to be inside or outside
     * of the axon.  IMPORTANT NOTE - If an particle is in a channel, it is
     * considered to be inside the membrane.
     * 
     * @param particle
     * @return
     */
    private boolean isParticleInside(Particle particle){

    	boolean inside = false;
    	
		for (AbstractMembraneChannel channel : channels){
			if (channel.getOwnedParticles().contains(particle)){
				inside = true;
				break;
			}
		}
		
		if (!inside){
			inside = particle.getPositionReference().distance(CENTER_POS) < crossSectionOuterRadius;
		}
    	
    	return inside;
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
    
    public interface Listener{
    	/**
    	 * Notification that a channel was added.
    	 * 
    	 * @param channel - Channel that was added.
    	 */
    	public void channelAdded(AbstractMembraneChannel channel);
    	
    	/**
    	 * Notification that a channel was removed.
    	 * 
    	 * @param channel - Channel that was removed.
    	 */
    	public void channelRemoved(AbstractMembraneChannel channel);
    	
    	/**
    	 * Notification that the concentration gradient for the given particle
    	 * type had changed.
    	 * 
    	 * @param particleType - Particle for which the concentration gradient has
    	 * changed.
    	 */
    	public void concentrationRatioChanged(ParticleType particleType);
    }
    
    public static class Adapter implements Listener{
		public void channelAdded(AbstractMembraneChannel channel) {}
		public void channelRemoved(AbstractMembraneChannel channel) {}
		public void concentrationRatioChanged(ParticleType particleType) {}
    }
}
