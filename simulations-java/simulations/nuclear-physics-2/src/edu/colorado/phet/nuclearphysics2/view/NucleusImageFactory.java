package edu.colorado.phet.nuclearphysics2.view;

import java.util.Random;

import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;
import edu.umd.cs.piccolo.nodes.PImage;


/**
 * This class generates a nucleus image from the constituent proton and
 * neutron images based on the size of the nucleus.
 * 
 * @author John Blanco
 */
public class NucleusImageFactory {
    
    static Random _rand = new Random();
    
    // Not intended for instantiation.
    private NucleusImageFactory(){}
    
    public static PImage generateNucleusImage(int numProtons, int numNeutrons){
        
        // TODO: JPB TBD Essentially stubbed for now.
        return NuclearPhysics2Resources.getImageNode("Uranium Nucleus Small.png");
    }
}
