/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import sun.security.x509.IssuerAlternativeNameExtension;

/**
 * This class defines the types of particles and their dimensions that are
 * allowed to exist in the States of Matter simulation.
 * 
 * TODO: JPB TBD - This class is probably temporary until the model is working
 * solidly and then refactored, at which time this will probably be rolled
 * into the class that makes up the basic particle.
 *
 * @author John Blanco
 */
public class StatesOfMatterParticleType {

    public final static int INVALID = 0;
    public final static int OXYGEN  = 1;
    public final static int NEON    = 2;
    public final static int ARGON   = 3;
    
    // Not meant for instantiation.
    private StatesOfMatterParticleType(){}
    
    /**
     * Get the diameter of a given molecule type.  The values used were found
     * on wikipedia.org and were specified for each element as "Atomic Radius 
     * (calc)".
     */
    public static final double getParticleDiameter(int particleType){
        
        double diameter = 0;
        
        if (isSupportedType(particleType)){
        
            switch (particleType){
            
            case OXYGEN:
                diameter = 304;
                break;
                
            case NEON:
                diameter = 308;
                break;
                
            case ARGON:
                diameter = 376;
                break;
                
            case INVALID:
            default:
                // Should never get here, debug it if we do.
                System.err.println("Error: Unexpected particle type.");
                assert false;
                diameter = 0;
                break;
            }
        }
        else{
            System.err.println("Error: Unsupported particle type, returning diameter of 0.");
            diameter = 0;
        }
        
        return diameter;
    }
    
    public static boolean isSupportedType (int particleType){
        
        if ((particleType == OXYGEN) ||
            (particleType == ARGON)  ||
            (particleType == NEON)) {
            
            return true;
        }
        else{
            return false;
        }
    }
}