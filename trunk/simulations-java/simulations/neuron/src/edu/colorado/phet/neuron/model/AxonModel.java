/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.model.AxonModel.ConcentrationTracker.AtomPosition;

/**
 * This class represents the main class for modeling the axon.  It acts as the
 * central location where the interaction between the membrane, the atoms
 * (i.e. ions), and the gates is all governed.
 *
 * @author John Blanco
 */
public class AxonModel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final Random RAND = new Random();
	
	private static final int TOTAL_INITIAL_ATOMS = 120;
	
	private static final double MAX_ATOM_VELOCITY = 500; // In nano meters per second.
	
	// The following constant defines how frequently atom motion is updated.
	// A value of 1 means every clock tick for every atom, 2 means every other
	// atom on each tick, etc.
	private static final int ATOM_UPDATE_INCREMENT = 4;
	
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
    private ArrayList<Particle> atoms = new ArrayList<Particle>();
    private ArrayList<AbstractMembraneChannel> channels = new ArrayList<AbstractMembraneChannel>();
    private final double crossSectionInnerRadius;
    private final double crossSectionOuterRadius;
    private int atomUpdateOffset = 0;
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
        
        // Add the atoms.
        // TODO: This is probably not correct, but for now assume that
        // the concentration of Na and K is equal and that both are equally
        // distributed inside and outside of the membrane.
        int i = TOTAL_INITIAL_ATOMS;
        Particle newAtom;
        while (true){
        	newAtom = new PotassiumIon();
        	positionAtomInsideMembrane(newAtom);
        	atoms.add(newAtom);
        	concentrationTracker.updateAtomCount(ParticleType.POTASSIUM_ION, AtomPosition.INSIDE_MEMBRANE, 1);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new SodiumIon();
        	positionAtomInsideMembrane(newAtom);
        	atoms.add(newAtom);
        	concentrationTracker.updateAtomCount(ParticleType.SODIUM_ION, AtomPosition.INSIDE_MEMBRANE, 1);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new PotassiumIon();
        	positionAtomOutsideMembrane(newAtom);
        	atoms.add(newAtom);
        	concentrationTracker.updateAtomCount(ParticleType.POTASSIUM_ION, AtomPosition.OUTSIDE_MEMBRANE, 1);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new SodiumIon();
        	positionAtomOutsideMembrane(newAtom);
        	atoms.add(newAtom);
        	concentrationTracker.updateAtomCount(ParticleType.SODIUM_ION, AtomPosition.OUTSIDE_MEMBRANE, 1);
        	i--;
        	if (i == 0){
        		break;
        	}
        }
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public NeuronClock getClock() {
        return clock;
    }    
    
    public ArrayList<Particle> getAtoms(){
    	return atoms;
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
    	
    	// Move the atoms to the appropriate initial locations by setting the
    	// target proportions.
    	setConcentration(ParticleType.SODIUM_ION, 0.5);
    	setConcentration(ParticleType.POTASSIUM_ION, 0.5);
    }

    /**
     * Get the proportion of atoms that are inside the axon membrane.  A value
     * of 1 indicates that all atoms are inside, 0 means that none are inside.
     */
    public double getProportionOfAtomsInside(ParticleType atomType){
    	return concentrationTracker.getProportion(atomType, AtomPosition.INSIDE_MEMBRANE);
    }
    
    /**
     * 
     * @return
     */
    public Rectangle2D getParticleMotionBounds(){
    	return PARTICLE_BOUNDS;
    }
    
    /**
     * Set the proportion of atoms inside the axon membrane.  A value of 0
     * indicates that all atoms of this type should be outside, a value of 1
     * indicates that the should all be inside, and value between...well, you
     * get the idea.
     */
    public void setConcentration(ParticleType atomType, double targetProportion){
    	
    	if (targetProportion > 1 || targetProportion < 0){
    		System.err.println(getClass().getName() + " - Error: Invalid target proportion value = " + targetProportion);
    		assert false;
    		return; 
    	}

    	int targetNumInside = (int)Math.round(targetProportion * (double)(concentrationTracker.getTotalNumAtoms(atomType)));
    	
    	if (targetNumInside > concentrationTracker.getNumAtomsInPosition(atomType, AtomPosition.INSIDE_MEMBRANE)){
    		// Move some atoms from outside to inside.
    		for (Particle atom : atoms){
    			if (atom.getType() == atomType && !isAtomInside(atom)){
    				// Move this guy in.
    				positionAtomInsideMembrane(atom);
    				concentrationTracker.updateAtomCount(atomType, AtomPosition.INSIDE_MEMBRANE, 1);
    				concentrationTracker.updateAtomCount(atomType, AtomPosition.OUTSIDE_MEMBRANE, -1);
    				if (concentrationTracker.getNumAtomsInPosition(atomType, AtomPosition.INSIDE_MEMBRANE) == targetNumInside){
    					break;
    				}
    			}
    		}
    		notifyConcentrationGradientChanged(atomType);
    	}
    	else if (targetNumInside < concentrationTracker.getNumAtomsInPosition(atomType, AtomPosition.INSIDE_MEMBRANE)){
    		// Move some atoms from inside to outside.
    		for (Particle atom : atoms){
    			if (atom.getType() == atomType && isAtomInside(atom)){
    				// Move this guy out.
    				positionAtomOutsideMembrane(atom);
    				concentrationTracker.updateAtomCount(atomType, AtomPosition.INSIDE_MEMBRANE, -1);
    				concentrationTracker.updateAtomCount(atomType, AtomPosition.OUTSIDE_MEMBRANE, 1);
    				if (concentrationTracker.getNumAtomsInPosition(atomType, AtomPosition.INSIDE_MEMBRANE) == targetNumInside){
    					break;
    				}
    			}
    		}
    		notifyConcentrationGradientChanged(atomType);
    	}
    }
    
    private void handleClockTicked(ClockEvent clockEvent){
    	
    	for (int i = atomUpdateOffset; i < atoms.size(); i += ATOM_UPDATE_INCREMENT){
    		updateAtomVelocity(atoms.get(i));
    	}
    	
    	atomUpdateOffset = (atomUpdateOffset + 1) % ATOM_UPDATE_INCREMENT;
    	
    	for (Particle atom : atoms){
    		atom.stepInTime(clockEvent.getSimulationTimeChange());
    	}
    	
    	for (AbstractMembraneChannel channel : channels){
    		channel.stepInTime(clockEvent.getSimulationTimeChange());
    		ArrayList<Particle> atomsTakenByChannel = channel.checkTakeControlAtoms(atoms);
    		if (atomsTakenByChannel != null){
    			for (Particle atom : atomsTakenByChannel){
    				atoms.remove(atom);
    				if (atom.getPositionReference().distance(CENTER_POS) > crossSectionOuterRadius){
    					// This atom was outside and, now that it has been
    					// captured by a channel, is considered to be inside.
    					concentrationTracker.updateAtomCount(atom.getType(), AtomPosition.OUTSIDE_MEMBRANE, -1);
    					concentrationTracker.updateAtomCount(atom.getType(), AtomPosition.INSIDE_MEMBRANE, 1);
    					notifyConcentrationGradientChanged(atom.getType());
    				}
    			}
    		}
    		ArrayList<Particle> atomsReleasedByChannel = channel.checkReleaseControlAtoms(atoms);
    		if (atomsReleasedByChannel != null){
    			for (Particle atom : atomsReleasedByChannel){
    				atoms.add(atom);
    				if (atom.getPositionReference().distance(CENTER_POS) > crossSectionOuterRadius){
    					// This atom was inside a channel and was released
    					// outside the membrane.
    					concentrationTracker.updateAtomCount(atom.getType(), AtomPosition.OUTSIDE_MEMBRANE, 1);
    					concentrationTracker.updateAtomCount(atom.getType(), AtomPosition.INSIDE_MEMBRANE, -1);
    					notifyConcentrationGradientChanged(atom.getType());
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
	
	private void notifyConcentrationGradientChanged(ParticleType atomType){
		for (Listener listener : listeners){
			listener.concentrationRatioChanged(atomType);
		}
	}
	
    private void updateAtomVelocity(Particle atom){
    	
    	// Convert the position to polar coordinates.
    	double r = Math.sqrt(atom.getX() * atom.getX() + atom.getY() * atom.getY());
    	double theta = Math.atan2(atom.getY(), atom.getX());
    	
    	// Determine the current angle of travel.
    	double previousAngleOfTravel = Math.atan2( atom.getPositionReference().y, atom.getPositionReference().x );
    	
    	double angle = 0;
    	double velocity;

    	// Generate the new angle of travel for the atom.
    	if (r < axonMembrane.getCrossSectionDiameter() / 2){
    		// Atom is inside the membrane.
    		if (crossSectionInnerRadius - r <= atom.getDiameter()){
    			// This atom is near the membrane wall, so should be repelled.
    	    	angle = theta + Math.PI + ((RAND.nextDouble() - 0.5) * Math.PI / 2);
    		}
    		else{
    			// Particle should just do a random walk.
				angle = Math.PI * 2 * RAND.nextDouble();
    		}
    	}
    	else{
    		// Atom is outside the membrane.
    		if (r - crossSectionOuterRadius <= atom.getDiameter()){
    			// This atom is near the membrane wall, so should be repelled.
				angle = Math.PI * RAND.nextDouble() - Math.PI / 2 + theta;
    		}
    		else if (!PARTICLE_BOUNDS.contains(atom.getPositionReference())){
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
		velocity = MAX_ATOM_VELOCITY * RAND.nextDouble();
		
		// Set the atom's new velocity. 
    	atom.setVelocity(velocity * Math.cos(angle), velocity * Math.sin(angle));
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
    		ArrayList<Particle> releasedAtoms = channelToRemove.forceReleaseAllAtoms(atoms);
    		// Since atoms in a channel are considered to be inside the
    		// membrane, force any atom that was in the channel when it was
    		// removed to go safely into the interior.
    		if (releasedAtoms != null){
    			for (Particle atom : releasedAtoms){
    				atoms.add(atom);
    				positionAtomInsideMembrane(atom);
    			}
    		}
    		// Remove the channel and send any notifications.
    		channels.remove(channelToRemove);
    		channelToRemove.remove();
    		notifyChannelRemoved(channelToRemove);
    	}
    }
    
    /**
     * Place an atom at a random location inside the axon membrane.
     */
    private void positionAtomInsideMembrane(Particle atom){
    	// Choose any angle.
    	double angle = RAND.nextDouble() * Math.PI * 2;
    	
    	// Choose a distance.
    	double distance = RAND.nextDouble() * crossSectionInnerRadius - atom.getDiameter();
    	
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
    	atom.setPosition(distance * Math.cos(angle), distance * Math.sin(angle));
    }

    /**
     * Place an atom at a random location outside the axon membrane.
     */
    private void positionAtomOutsideMembrane(Particle atom){
    	double maxDistance = Math.min(getParticleMotionBounds().getWidth() / 2,
    			getParticleMotionBounds().getHeight() / 2);
    	double distance = RAND.nextDouble() * (maxDistance - crossSectionOuterRadius) + crossSectionOuterRadius +
    		atom.getDiameter();
    	double angle = RAND.nextDouble() * Math.PI * 2;
    	atom.setPosition(distance * Math.cos(angle), distance * Math.sin(angle));
    }
    
    /**
     * Determine whether the given atom is considered to be inside or outside
     * of the axon.  IMPORTANT NOTE - If an atom is in a channel, it is
     * considered to be inside the membrane.
     * 
     * @param atom
     * @return
     */
    private boolean isAtomInside(Particle atom){

    	boolean inside = false;
    	
		for (AbstractMembraneChannel channel : channels){
			if (channel.getOwnedAtoms().contains(atom)){
				inside = true;
				break;
			}
		}
		
		if (!inside){
			inside = atom.getPositionReference().distance(CENTER_POS) < crossSectionOuterRadius;
		}
    	
    	return inside;
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
    
    /**
     * This is a "convenience class" that is used to track the relative
     * concentration of the different atom types.  This was created so that
     * the concentration doesn't need to be completely recalculated at every
     * time step, which would be computationally expensive.
     */
    public static class ConcentrationTracker {

    	enum AtomPosition {INSIDE_MEMBRANE, OUTSIDE_MEMBRANE};
    	
    	HashMap<ParticleType, Integer> mapAtomTypeToNumOutside = new HashMap<ParticleType, Integer>();
    	HashMap<ParticleType, Integer> mapAtomTypeToNumInside = new HashMap<ParticleType, Integer>();
    	
    	public void updateAtomCount(ParticleType atomType, AtomPosition position, int delta){
    		HashMap<ParticleType, Integer> map = position == AtomPosition.INSIDE_MEMBRANE ? mapAtomTypeToNumInside :
    			mapAtomTypeToNumOutside;
    		Integer currentCount = map.get(atomType);
    		if (currentCount == null){
    			currentCount = new Integer(0);
    		}
    		Integer newCount = new Integer(currentCount.intValue() + delta);
    		if (newCount.intValue() < 0){
    			System.err.println(getClass().getName()+ "- Error: Negative count for atoms in a position.");
    			assert false;
    			newCount = new Integer(0);
    		}
    		map.put(atomType, newCount);
    	}
    	
    	public void resetAtomCount(ParticleType atomType, AtomPosition position){
    		HashMap<ParticleType, Integer> map = position == AtomPosition.INSIDE_MEMBRANE ? mapAtomTypeToNumInside :
    			mapAtomTypeToNumOutside;
    		map.put(atomType, new Integer(0));
    	}
    	
    	public int getNumAtomsInPosition(ParticleType atomType, AtomPosition position){
    		HashMap<ParticleType, Integer> map = position == AtomPosition.INSIDE_MEMBRANE ? mapAtomTypeToNumInside :
    			mapAtomTypeToNumOutside;
    		Integer currentCount = map.get(atomType);
    		if (currentCount == null){
    			currentCount = new Integer(0);
    		}
    		return currentCount.intValue();
    	}
    	
    	public int getTotalNumAtoms(ParticleType atomType){
    		return (getNumAtomsInPosition(atomType, AtomPosition.INSIDE_MEMBRANE) + 
    				getNumAtomsInPosition(atomType, AtomPosition.OUTSIDE_MEMBRANE));
    	}
    	
    	public double getProportion(ParticleType atomType, AtomPosition position){
    		Integer insideCount = mapAtomTypeToNumInside.get(atomType);
    		if (insideCount == null){
    			insideCount = new Integer(0);
    		}
    		Integer outsideCount = mapAtomTypeToNumOutside.get(atomType);
    		if (outsideCount == null){
    			outsideCount = new Integer(0);
    		}

    		if (insideCount.intValue() == outsideCount.intValue() && insideCount.intValue() == 0){
    			return 0;
    		}
    		else if (position == AtomPosition.INSIDE_MEMBRANE){
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
    	 * Notification that the concentration gradient for the given atom
    	 * type had changed.
    	 * 
    	 * @param atomType - Atom for which the concentration gradient has
    	 * changed.
    	 */
    	public void concentrationRatioChanged(ParticleType atomType);
    }
    
    public static class Adapter implements Listener{
		public void channelAdded(AbstractMembraneChannel channel) {}
		public void channelRemoved(AbstractMembraneChannel channel) {}
		public void concentrationRatioChanged(ParticleType atomType) {}
    }
}
