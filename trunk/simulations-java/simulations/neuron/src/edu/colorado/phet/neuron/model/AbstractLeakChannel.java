package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public abstract class AbstractLeakChannel extends AbstractMembraneChannel {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CAPTURE_DISTANCE = 4.5; // In nanometers.
	
	private static final int ATOM_MOTION_UPDATE_PERIOD = 4; // Controls frequency at which motion of atoms inside
	                                                        // this channel is updated.  Lower numbers mean more
	                                                        // frequent updates, 1 is min value.
	
	private static final double MAX_ATOM_VELOCITY = 300; // In nanometers per second, I think.
	
	private static final double PROBABILITY_OF_DOMINANT_MOTION = 0.8;
	
	// Value used to prevent atoms from being recaptured as soon as they are released.
	private static final int ATOM_RECAPTURE_COUNTER = 500;
	
	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------
	
	private MembraneChannelFlowDirection flowDirection = MembraneChannelFlowDirection.NONE;
	private int atomMotionUpdateCounter = 0;
	private final AtomType allowedAtom; // Atom that can move through this channel.
	private ArrayList<AtomCountdownPair> recentlyReleaseAtoms = new ArrayList<AtomCountdownPair>();
	
	//----------------------------------------------------------------------------
	// Constructor
	//----------------------------------------------------------------------------

	public AbstractLeakChannel(double channelWidth, double channelHeight, AtomType allowedAtom) {
		super(channelWidth, channelHeight);
		this.allowedAtom = allowedAtom;
	}
	
	//----------------------------------------------------------------------------
	// Methods
	//----------------------------------------------------------------------------
	
	@Override
	public void checkReleaseControlAtoms(ArrayList<Atom> freeAtoms) {
		Atom atom = null;
		for (int i = 0; i < getOwnedAtomsRef().size(); i++){
			atom = getOwnedAtomsRef().get(i);
			if (atom.getPositionReference().distance(getCenterLocation()) > CAPTURE_DISTANCE * 1.25){
				// Atom is far enough away that it can be released.
				getOwnedAtomsRef().remove(atom);
				freeAtoms.add(atom);
				recentlyReleaseAtoms.add(new AtomCountdownPair(atom, ATOM_RECAPTURE_COUNTER));
				break;
			}
		}
	}

	@Override
	public void checkTakeControlAtoms(ArrayList<Atom> freeAtoms) {
		
		ArrayList<Atom> ownedAtoms = getOwnedAtomsRef();
		
		// Only move one atom at a time.
		if (ownedAtoms.size() == 0){
			Atom atom = null;
			for (int i = 0; i < freeAtoms.size(); i++){
				// See if this atom is in the right place to be captured.
				atom = freeAtoms.get(i);
				if ( atom.getType() == allowedAtom && 
					 atom.getPosition().distance(getCenterLocation()) < CAPTURE_DISTANCE &&
					 !recentlyCaptured(atom)){
					// Capture this guy.
					captureAtom(atom, freeAtoms, ownedAtoms);
					break;
				}
			}
		}
	}

	@Override
	public MembraneChannelTypes getChannelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stepInTime(double dt) {
		if (!getOwnedAtomsRef().isEmpty()){
			// This channel has at least one atom in it, so update the atom motion.
			atomMotionUpdateCounter = (atomMotionUpdateCounter + 1) % ATOM_MOTION_UPDATE_PERIOD;
			if (atomMotionUpdateCounter == 0){
				// Time to update the motion of the atoms in this channel.
				updateMotionOfAtoms();
			}
			
			for (Atom atom : getOwnedAtomsRef()){
				atom.stepInTime(dt);
			}
		}
		
		// Update the data structure that keeps track of recently released
		// atoms.
		ArrayList<AtomCountdownPair> atomsToRemoveFromList = new ArrayList<AtomCountdownPair>();
		for (AtomCountdownPair atomCountdownPair : recentlyReleaseAtoms){
			if (atomCountdownPair.coundownComplete()){
				// It has been long enough - remove this guy from the list.
				atomsToRemoveFromList.add(atomCountdownPair);
			}
			else{
				atomCountdownPair.decrementCount();
			}
		}
		recentlyReleaseAtoms.removeAll(atomsToRemoveFromList);
	}
	
	private void captureAtom(Atom atom, ArrayList<Atom> freeAtoms, ArrayList<Atom> ownedAtoms){
		
		// Transfer the atom to the list of atoms "owned" by this channel.
		freeAtoms.remove(atom);
		ownedAtoms.add(atom);
		
		// Move the atom so that it is exactly at the entrance of the channel.
		if ( atom.getPositionReference().distance(new Point2D.Double(0, 0)) >
		     getCenterLocation().distance(new Point2D.Double(0, 0)))
		{
			// Atom is outside of the membrane, so move it to the outer
			// entrance of the channel.
			double xPos = CAPTURE_DISTANCE * Math.cos(getRotationalAngle()) + getCenterLocation().getX();
			double yPos = CAPTURE_DISTANCE * Math.sin(getRotationalAngle()) + getCenterLocation().getY();
			atom.setPosition(xPos, yPos);
			
			// Set the velocity of the atom such that it is moving from the
			// outside to the inside of the membrane.
			atom.setVelocity(-MAX_ATOM_VELOCITY * Math.cos(getRotationalAngle()),
					-MAX_ATOM_VELOCITY * Math.sin(getRotationalAngle()));
			
			// Set the direction of flow.
			flowDirection = MembraneChannelFlowDirection.IN;
		}
		else{
			// Atom is inside of the membrane, so move it to the inner
			// entrance of the channel.
			double xPos = -CAPTURE_DISTANCE * Math.cos(getRotationalAngle()) + getCenterLocation().getX();
			double yPos = -CAPTURE_DISTANCE * Math.sin(getRotationalAngle()) + getCenterLocation().getY();
			atom.setPosition(xPos, yPos);
			
			// Set the velocity of the atom such that it is moving from the
			// outside to the inside of the membrane.
			atom.setVelocity(MAX_ATOM_VELOCITY * Math.cos(getRotationalAngle()),
					MAX_ATOM_VELOCITY * Math.sin(getRotationalAngle()));

			// Set the direction of flow.
			flowDirection = MembraneChannelFlowDirection.OUT;
		}
	}
	
	private boolean recentlyCaptured(Atom atom){
		boolean recentlyCaptured = false;
		for (AtomCountdownPair atomCountdownPair : recentlyReleaseAtoms){
			if (atomCountdownPair.getAtom() == atom){
				recentlyCaptured = true;
				break;
			}
		}
		return recentlyCaptured;
	}
	
	private void updateMotionOfAtoms(){
		
		if (getOwnedAtoms().size() == 0){
			// No atoms to update, so bail now.
			return;
		}
		
		for (Atom atom : getOwnedAtomsRef()){
			
			// Decide on the direction of motion, which will tend towards the
			// current flow direction, but has a probability to move the opposite
			// way.  This creates an appearance of somewhat random motion, which
			// is what we're after.
			double directionMultiplier;
			if (flowDirection == MembraneChannelFlowDirection.IN){
				if (RAND.nextDouble() < PROBABILITY_OF_DOMINANT_MOTION){
					directionMultiplier = -1;
				}
				else{
					directionMultiplier = 1;
				}
			}
			else if (flowDirection == MembraneChannelFlowDirection.OUT){
				if (RAND.nextDouble() < PROBABILITY_OF_DOMINANT_MOTION){
					directionMultiplier = 1;
				}
				else{
					directionMultiplier = -1;
				}
			}
			else{
				System.err.println(getClass().getName() + " - Error: Unexpected flow direction.");
				assert false;
				directionMultiplier = 1;
			}
			
			// Update the motion of the atom.
			double velocity = MAX_ATOM_VELOCITY * RAND.nextDouble() * directionMultiplier;
			atom.setVelocity(velocity * Math.cos(getRotationalAngle()), velocity * Math.sin(getRotationalAngle()));
		}
	}
	
	private static class AtomCountdownPair{
		
		Atom atom = null;
		int count = 0;
		
		public AtomCountdownPair(Atom atom, int initialCount) {
			this.atom = atom;
			this.count = initialCount;
		}
		
		public void decrementCount(){
			if (count > 0){
				count--;
			}
		}
		
		public boolean coundownComplete(){
			return (count == 0);
		}
		
		public Atom getAtom(){
			return atom;
		}
	}
}
