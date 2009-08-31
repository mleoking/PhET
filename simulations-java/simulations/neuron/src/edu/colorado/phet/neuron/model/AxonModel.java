/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.util.ArrayList;
import java.util.Random;


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
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    private final NeuronClock clock;
    private final AxonMembrane axonMembrane = new AxonMembrane();
    private ArrayList<Atom> ions = new ArrayList<Atom>();
    private final double crossSectionInnerDiameter;
    private final double crossSectionOuterDiameter;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AxonModel( NeuronClock clock ) {
        this.clock = clock;
        
        crossSectionInnerDiameter = axonMembrane.getCrossSectionDiameter() - (axonMembrane.getMembraneThickness() / 2); 
        crossSectionOuterDiameter = axonMembrane.getCrossSectionDiameter() + (axonMembrane.getMembraneThickness() / 2); 
        
        // Add the atoms.
        // TODO: This is probably not correct, and we will need to have some
        // initial concentration grandient.  Assume 50% for now.
        int i = TOTAL_INITIAL_ATOMS;
        Atom newAtom;
        while (true){
        	newAtom = new PotassiumIon();
        	positionAtomInsideMembrane(newAtom);
        	ions.add(newAtom);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new SodiumIon();
        	positionAtomInsideMembrane(newAtom);
        	ions.add(newAtom);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new PotassiumIon();
        	positionAtomOutsideMembrane(newAtom);
        	ions.add(newAtom);
        	i--;
        	if (i == 0){
        		break;
        	}
        	newAtom = new SodiumIon();
        	positionAtomOutsideMembrane(newAtom);
        	ions.add(newAtom);
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
    	return ions;
    }
    
    public AxonMembrane getAxonMembrane(){
    	return axonMembrane;
    }

    //----------------------------------------------------------------------------
    // Other Methods
    //----------------------------------------------------------------------------
    
    /**
     * Place an atom at a random location inside the axon membrane.
     */
    private void positionAtomInsideMembrane(Atom atom){
    	double distance = RAND.nextDouble() * (crossSectionInnerDiameter / 2 - atom.getDiameter());
    	double angle = RAND.nextDouble() * Math.PI * 2;
    	atom.setPosition(distance * Math.cos(angle), distance * Math.sin(angle));
    }

    /**
     * Place an atom at a random location outside the axon membrane.
     */
    private void positionAtomOutsideMembrane(Atom atom){
    	double maxDistance = crossSectionOuterDiameter * 0.6; // Arbitrary multiplier, tweak as needed.
    	double distance = RAND.nextDouble() * (maxDistance - (crossSectionOuterDiameter / 2)) + 
    		(crossSectionOuterDiameter / 2) + atom.getDiameter();
    	double angle = RAND.nextDouble() * Math.PI * 2;
    	atom.setPosition(distance * Math.cos(angle), distance * Math.sin(angle));
    }
}
