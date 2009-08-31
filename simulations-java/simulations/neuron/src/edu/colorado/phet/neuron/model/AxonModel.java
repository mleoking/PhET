/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.util.ArrayList;


/**
 * This class represents the main class for modeling the axon.  It acts as the
 * central location where the interaction between the membrane, the atoms
 * (i.e. ions), and the gates is all governed.
 *
 * @author John Blanco
 */
public class AxonModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final NeuronClock clock;
    private AxonMembrane axonMembrane = new AxonMembrane();
    private ArrayList<Atom> ions = new ArrayList<Atom>();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AxonModel( NeuronClock clock ) {
        this.clock = clock;
        
        // TODO: Temp for testing ions.
        Atom atom = new PotassiumIon();
        atom.setPosition(20, 20);
        ions.add(atom);
        atom = new SodiumIon();
        atom.setPosition(-20, -20);
        ions.add(atom);
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
}
