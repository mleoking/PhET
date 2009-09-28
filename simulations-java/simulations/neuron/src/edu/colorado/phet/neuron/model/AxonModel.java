/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
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
	
	private static final int TOTAL_INITIAL_ATOMS = 100;
	
	private static final double MAX_ATOM_VELOCITY = 500; // In nano meters per second.
	
	// The following constant defines how frequently atom motion is updated.
	// A value of 1 means every clock tick for every atom, 2 means every other
	// atom on each tick, etc.
	private static final int ATOM_UPDATE_INCREMENT = 4;
	
	private static final Point2D CENTER_POS = new Point2D.Double(0, 0);
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final NeuronClock clock;
    private final AxonMembrane axonMembrane = new AxonMembrane();
    private ArrayList<Atom> atoms = new ArrayList<Atom>();
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
        Atom newAtom;
        while (true){
        	newAtom = new PotassiumIon();
        	positionAtomInsideMembrane(newAtom);
        	atoms.add(newAtom);
        	concentrationTracker.updateAtomCount(AtomType.POTASSIUM, AtomPosition.INSIDE_MEMBRANE, 1);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new SodiumIon();
        	positionAtomInsideMembrane(newAtom);
        	atoms.add(newAtom);
        	concentrationTracker.updateAtomCount(AtomType.SODIUM, AtomPosition.INSIDE_MEMBRANE, 1);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new PotassiumIon();
        	positionAtomOutsideMembrane(newAtom);
        	atoms.add(newAtom);
        	concentrationTracker.updateAtomCount(AtomType.POTASSIUM, AtomPosition.OUTSIDE_MEMBRANE, 1);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new SodiumIon();
        	positionAtomOutsideMembrane(newAtom);
        	atoms.add(newAtom);
        	concentrationTracker.updateAtomCount(AtomType.SODIUM, AtomPosition.OUTSIDE_MEMBRANE, 1);
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
    
    public ArrayList<Atom> getAtoms(){
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

    public void setNumMembraneChannels(MembraneChannelTypes channelType, int numChannels){
    	if (numChannels < getNumMembraneChannels(channelType)){
    		// Need to remove a channel.
    		removeChannel(channelType);
    	}
    	else if (numChannels > getNumMembraneChannels(channelType)){
    		// Need to add a channel.
    		addChannel(channelType);
    	}
    	else{
    		// Don't need to do nuthin'.
    	}
    }
    
    //----------------------------------------------------------------------------
    // Other Methods
    //----------------------------------------------------------------------------

    /**
     * Get the proportion of atoms that are inside the axon membrane.  A value
     * of 1 indicates that all atoms are inside, 0 means that none are inside.
     */
    public double getProportionOfAtomsInside(AtomType atomType){
    	return concentrationTracker.getProportion(atomType, AtomPosition.INSIDE_MEMBRANE);
    }
    
    /**
     * Set the proportion of atoms inside the axon membrane.  A value of 0
     * indicates that all atoms of this type should be outside, a value of 1
     * indicates that the should all be inside, and value between...well, you
     * get the idea.
     */
    public void setConcentration(AtomType atomType, double targetProportion){
    	
    	if (targetProportion > 1 || targetProportion < 0){
    		System.err.println(getClass().getName() + " - Error: Invalid target proportion value = " + targetProportion);
    		assert false;
    		return; 
    	}

    	int targetNumInside = (int)Math.round(targetProportion * (double)(concentrationTracker.getTotalNumAtoms(atomType)));
    	
    	if (targetNumInside > concentrationTracker.getNumAtomsInPosition(atomType, AtomPosition.INSIDE_MEMBRANE)){
    		// Move some atoms from outside to inside.
    		for (Atom atom : atoms){
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
    		for (Atom atom : atoms){
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
    	
    	for (Atom atom : atoms){
    		atom.stepInTime(clockEvent.getSimulationTimeChange());
    	}
    	
    	for (AbstractMembraneChannel channel : channels){
    		channel.stepInTime(clockEvent.getSimulationTimeChange());
    		ArrayList<Atom> atomsTakenByChannel = channel.checkTakeControlAtoms(atoms);
    		if (atomsTakenByChannel != null){
    			for (Atom atom : atomsTakenByChannel){
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
    		ArrayList<Atom> atomsReleasedByChannel = channel.checkReleaseControlAtoms(atoms);
    		if (atomsReleasedByChannel != null){
    			for (Atom atom : atomsReleasedByChannel){
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
	
	private void notifyConcentrationGradientChanged(AtomType atomType){
		for (Listener listener : listeners){
			listener.concentrationRatioChanged(atomType);
		}
	}
	
    private void updateAtomVelocity(Atom atom){
    	
    	// Convert the position to polar coordinates.
    	double r = Math.sqrt(atom.getX() * atom.getX() + atom.getY() * atom.getY());
    	double theta = Math.atan2(atom.getY(), atom.getX());
    	double angle;
    	double velocity;

    	// Generate the angle of travel for the atom.
    	if (r < axonMembrane.getCrossSectionDiameter() / 2){
    		// Atom is inside the membrane.
    		if (crossSectionInnerRadius - r <= atom.getDiameter()){
    			// This atom is near the membrane wall, so should be repelled.
    	    	angle = theta + Math.PI;
    		}
    		else if (crossSectionInnerRadius - r <= crossSectionInnerRadius / 2){
    			// This is in the "zone of attraction" where it should tend to
    			// move toward the membrane.  The following code creates a
    			// probabilistic bias to make it tend to move that way.
    			if (RAND.nextDouble() > 0.8){
    				angle = Math.PI * RAND.nextDouble() - Math.PI / 2 + theta;
    			}
    			else{
    				angle = Math.PI * 2 * RAND.nextDouble();
    			}
    		}
    		else{
    			// It's near the center, so it should just do a random walk.
   				angle = Math.PI * 2 * RAND.nextDouble();
    		}
    	}
    	else{
    		// Atom is outside the membrane.
    		if (r - crossSectionOuterRadius <= atom.getDiameter()){
    			// This atom is near the membrane wall, so should be repelled.
    	    	angle = theta;
    		}
    		else{
    			// The following code creates a probabilistic bias that causes
    			// the atom to tend to move toward the membrane.
    			if (RAND.nextBoolean()){
    				angle = Math.PI * RAND.nextDouble() + Math.PI / 2 + theta;
    			}
    			else{
    				angle = Math.PI * 2 * RAND.nextDouble();
    			}
    		}
    	}
    	
    	// Generate the new overall velocity.
		velocity = MAX_ATOM_VELOCITY * RAND.nextDouble();
		
		// Set the atom's new velocity. 
    	atom.setVelocity(velocity * Math.cos(angle), velocity * Math.sin(angle));
    }
    
    private void addChannel(MembraneChannelTypes channelType){
    	AbstractMembraneChannel membraneChannel = null;
    	
    	switch (channelType){
    	case SODIUM_LEAKAGE_CHANNEL:
    		membraneChannel = new SodiumLeakageChannel();
    		break;
    		
    	case POTASSIUM_LEAKAGE_CHANNEL:
    		membraneChannel = new PotassiumLeakageChannel();
    		break;
    	}
    	
    	// Find a position that is not too close to an existing channel.
    	double minInterChannelDistance = 10; // Nanometers, arbitrarily chosen to look okay.
    	double angle = 0;
    	Point2D newLocation = new Point2D.Double();
    	double radius = axonMembrane.getCrossSectionDiameter() / 2; 
    	boolean openLocationFound = false;
    	while (!openLocationFound){
    		angle = RAND.nextDouble() * Math.PI * 2;
    		// Convert to cartesian.
    		newLocation = new Point2D.Double(radius * Math.cos(angle), radius * Math.sin(angle));
    		openLocationFound = true;
    		for (AbstractMembraneChannel channel : channels ){
    			if (channel.getCenterLocation().distance(newLocation) < minInterChannelDistance){
    				// Too close.
    				openLocationFound = false;
    				System.out.println("Location rejected.");
    			}
    		}
    	}
    	
    	// Position the channel on the membrane.
    	membraneChannel.setRotationalAngle(angle);
    	membraneChannel.setCenterLocation(newLocation);
    	
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
    	for (int i = 0; i < channels.size(); i++){
    		if (channels.get(i).getChannelType() == channelType){
    			channelToRemove = channels.get(i);
    			break;
    		}
    	}
    	
    	if (channelToRemove != null){
    		channelToRemove.forceReleaseAllAtoms(atoms);
    		channels.remove(channelToRemove);
    		channelToRemove.remove();
    	}
    }
    
    private void getAtomCounts(AtomType atomType, IntegerWrapper numInside, IntegerWrapper numOutside){

    	numInside.setValue(0);
    	numOutside.setValue(0);
    	
    	for (AbstractMembraneChannel channel : channels){
    		// All atoms in channels are considered to be inside the axon.
    		numInside.setValue( numInside.getValue() + channel.getOwnedAtomsRef().size());
    	}
    	
    	for (Atom atom : atoms){
    		if (atom.getType() == atomType){
    			if (isAtomInside(atom)){
    				numInside.setValue(numInside.getValue() + 1);
    			}
    			else{
    				numOutside.setValue(numOutside.getValue() + 1);
    			}
    		}
    	}
    }
    
    /**
     * Place an atom at a random location inside the axon membrane.
     */
    private void positionAtomInsideMembrane(Atom atom){
    	double distance = RAND.nextDouble() * (crossSectionInnerRadius - atom.getDiameter());
    	double angle = RAND.nextDouble() * Math.PI * 2;
    	atom.setPosition(distance * Math.cos(angle), distance * Math.sin(angle));
    }

    /**
     * Place an atom at a random location outside the axon membrane.
     */
    private void positionAtomOutsideMembrane(Atom atom){
    	double maxDistance = crossSectionOuterRadius * 1.2; // Arbitrary multiplier, tweak as needed.
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
    private boolean isAtomInside(Atom atom){

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
    	
    	HashMap<AtomType, Integer> mapAtomTypeToNumOutside = new HashMap<AtomType, Integer>();
    	HashMap<AtomType, Integer> mapAtomTypeToNumInside = new HashMap<AtomType, Integer>();
    	
    	public void updateAtomCount(AtomType atomType, AtomPosition position, int delta){
    		HashMap<AtomType, Integer> map = position == AtomPosition.INSIDE_MEMBRANE ? mapAtomTypeToNumInside :
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
    	
    	public void resetAtomCount(AtomType atomType, AtomPosition position){
    		HashMap<AtomType, Integer> map = position == AtomPosition.INSIDE_MEMBRANE ? mapAtomTypeToNumInside :
    			mapAtomTypeToNumOutside;
    		map.put(atomType, new Integer(0));
    	}
    	
    	public int getNumAtomsInPosition(AtomType atomType, AtomPosition position){
    		HashMap<AtomType, Integer> map = position == AtomPosition.INSIDE_MEMBRANE ? mapAtomTypeToNumInside :
    			mapAtomTypeToNumOutside;
    		Integer currentCount = map.get(atomType);
    		if (currentCount == null){
    			currentCount = new Integer(0);
    		}
    		return currentCount.intValue();
    	}
    	
    	public int getTotalNumAtoms(AtomType atomType){
    		return (getNumAtomsInPosition(atomType, AtomPosition.INSIDE_MEMBRANE) + 
    				getNumAtomsInPosition(atomType, AtomPosition.OUTSIDE_MEMBRANE));
    	}
    	
    	public double getProportion(AtomType atomType, AtomPosition position){
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
    	 * Notification that a channel was added.  Note that is is
    	 * assumed that the listener will register with the channel itself
    	 * in order to get notified of its removal, which is why there is no
    	 * "channelRemoved" notification.
    	 * 
    	 * @param channel - Channel that was added.
    	 */
    	public void channelAdded(AbstractMembraneChannel channel);
    	
    	/**
    	 * Notification that the concentration gradient for the given atom
    	 * type had changed.
    	 * 
    	 * @param atomType - Atom for which the concentration gradient has
    	 * changed.
    	 */
    	public void concentrationRatioChanged(AtomType atomType);
    }
    
    public static class Adapter implements Listener{
		public void channelAdded(AbstractMembraneChannel channel) {}
		public void concentrationRatioChanged(AtomType atomType) {}
    }
    
    public static class IntegerWrapper{
    	private int value = 0;

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
    }
}
