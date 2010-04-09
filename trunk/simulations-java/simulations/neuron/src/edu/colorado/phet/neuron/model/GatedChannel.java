package edu.colorado.phet.neuron.model;

import java.util.ArrayList;

/**
 * Base class for gated membrane channels, i.e. channels that can open and
 * close.
 * 
 * @author John Blanco
 */
public abstract class GatedChannel extends MembraneChannel {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final double CAPTURE_DISTANCE = 5.0; // In nanometers.
	
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
	private ArrayList<AtomCountdownPair> recentlyReleaseAtoms = new ArrayList<AtomCountdownPair>();
	
	//----------------------------------------------------------------------------
	// Constructor
	//----------------------------------------------------------------------------

	public GatedChannel(double channelWidth, double channelHeight, IParticleCapture modelContainingParticles) {
		super(channelWidth, channelHeight, modelContainingParticles);
		setOpenness(0);  // Gated channels are assumed to be initially closed.
	}
	
	//----------------------------------------------------------------------------
	// Methods
	//----------------------------------------------------------------------------
	
	@Override
	public ArrayList<Particle> checkReleaseControlParticles(ArrayList<Particle> freeAtoms) {
		Particle atom = null;
		ArrayList<Particle> releasedAtoms = null;
		for (int i = 0; i < getOwnedAtomsRef().size(); i++){
			atom = getOwnedAtomsRef().get(i);
			if (atom.getPositionReference().distance(getCenterLocation()) > CAPTURE_DISTANCE * 1.25){
				// Atom is far enough away that it can be released.
				getOwnedAtomsRef().remove(atom);
				releasedAtoms = new ArrayList<Particle>();
				releasedAtoms.add(atom);
				recentlyReleaseAtoms.add(new AtomCountdownPair(atom, ATOM_RECAPTURE_COUNTER));
				break;
			}
		}
		
		return releasedAtoms;
	}

	/*
	 * TODO: Feb 12 2010 - The paradigm for moving particles around is changing from having
	 * them controlled by the AxonModel and the channels to having a motion strategy set on
	 * them and have them move themselves.  This routine is being removed as part of that
	 * effort, and should be deleted or reinstated at some point in time.

	@Override
	public ArrayList<Particle> checkTakeControlParticles(ArrayList<Particle> freeAtoms) {
		
		ArrayList<Particle> ownedAtoms = getOwnedAtomsRef();
		ArrayList<Particle> atomsToTake = null;
		
		// Only move one atom can be in the channel at a time.
		if (ownedAtoms.size() == 0){
			Particle atom = null;
			for (int i = 0; i < freeAtoms.size(); i++){
				// See if this atom is in the right place to be captured.
				atom = freeAtoms.get(i);
				if ( atom.getType() == allowedAtom && 
					 atom.getPosition().distance(getCenterLocation()) < CAPTURE_DISTANCE &&
					 !recentlyCaptured(atom)){
					// Capture this guy.
					captureAtom(atom, freeAtoms, ownedAtoms);
					atomsToTake = new ArrayList<Particle>();
					atomsToTake.add(atom);
					break;
				}
			}
		}
		
		return atomsToTake;
	}
	*/

	@Override
	public MembraneChannelTypes getChannelType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		
		/*
		 * TODO: Feb 12 2010 - The paradigm for moving particles around is changing from having
		 * them controlled by the AxonModel and the channels to having a motion strategy set on
		 * them and have them move themselves.  This code is being removed as part of that
		 * effort, and should be deleted or reinstated at some point in time.

		if (!getOwnedAtomsRef().isEmpty()){
			// This channel has at least one atom in it, so update the atom motion.
			atomMotionUpdateCounter = (atomMotionUpdateCounter + 1) % ATOM_MOTION_UPDATE_PERIOD;
			if (atomMotionUpdateCounter == 0){
				// Time to update the motion of the atoms in this channel.
				updateMotionOfAtoms();
			}
			
			for (Particle atom : getOwnedAtomsRef()){
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
		*/
	}
	
	/*
	 * TODO: Feb 12 2010 - The paradigm for moving particles around is changing from having
	 * them controlled by the AxonModel and the channels to having a motion strategy set on
	 * them and have them move themselves.  This routine is being removed as part of that
	 * effort, and should be deleted or reinstated at some point in time.
	
	private void captureAtom(Particle atom, ArrayList<Particle> freeAtoms, ArrayList<Particle> ownedAtoms){
		
		// Transfer the atom to the list of atoms "owned" by this channel.
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
	 */
	
	private boolean recentlyCaptured(Particle atom){
		boolean recentlyCaptured = false;
		for (AtomCountdownPair atomCountdownPair : recentlyReleaseAtoms){
			if (atomCountdownPair.getAtom() == atom){
				recentlyCaptured = true;
				break;
			}
		}
		return recentlyCaptured;
	}
	
	/*
	 * TODO: Feb 12 2010 - The paradigm for moving particles around is changing from having
	 * them controlled by the AxonModel and the channels to having a motion strategy set on
	 * them and have them move themselves.  This routine is being removed as part of that
	 * effort, and should be deleted or reinstated at some point in time.

	private void updateMotionOfAtoms(){
		
		if (getOwnedParticles().size() == 0){
			// No atoms to update, so bail now.
			return;
		}
		
		for (Particle atom : getOwnedAtomsRef()){
			
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
	*/
	
	private static class AtomCountdownPair{
		
		Particle atom = null;
		int count = 0;
		
		public AtomCountdownPair(Particle atom, int initialCount) {
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
		
		public Particle getAtom(){
			return atom;
		}
	}
}
