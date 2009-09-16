/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;


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
        // TODO: This is probably not correct, and we will need to have some
        // initial concentration grandient.  Assume 50% for now.
        int i = TOTAL_INITIAL_ATOMS;
        Atom newAtom;
        while (true){
        	newAtom = new PotassiumIon();
        	positionAtomInsideMembrane(newAtom);
        	atoms.add(newAtom);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new SodiumIon();
        	positionAtomInsideMembrane(newAtom);
        	atoms.add(newAtom);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new PotassiumIon();
        	positionAtomOutsideMembrane(newAtom);
        	atoms.add(newAtom);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new SodiumIon();
        	positionAtomOutsideMembrane(newAtom);
        	atoms.add(newAtom);
        	i--;
        	if (i == 0){
        		break;
        	}
        }
        
        // Add some channels.  TODO - This is temporary, to be replaced by
        // explicit additions and removals by the user.
        addChannel(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL);
        addChannel(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL);
        addChannel(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL);
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

    //----------------------------------------------------------------------------
    // Other Methods
    //----------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent){
    	
    	for (int i = atomUpdateOffset; i < atoms.size(); i += ATOM_UPDATE_INCREMENT){
    		updateAtomVelocity(atoms.get(i));
    	}
    	
    	atomUpdateOffset = (atomUpdateOffset + 1) % ATOM_UPDATE_INCREMENT;
    	
    	for (Atom atom : atoms){
    		atom.stepInTime(clockEvent.getSimulationTimeChange());
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
    		// TODO.
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
    	
    	// Position the channel randomly on the membrane.
    	membraneChannel.setRotationalAngle(angle);
    	membraneChannel.setCenterLocation(newLocation);
    	
    	channels.add(membraneChannel);
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
}
