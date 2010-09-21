/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;


/**
 * This class is a singleton that is the place where individual molecules come
 * in order to determine their probability of absorbing a photon (assuming
 * that the wavelength of the photon is one that they will absorb).  This
 * exists to allow this probablity to be varied using developer controls, and
 * at some point may be replaced by a simple constant.
 * 
 * @author John Blanco
 */
public class SingleMoleculePhotonAbsorptionProbability {

    private static final double DEFAULT_ABSORPTION_PROBABILITY = 0.5;
    
    private static SingleMoleculePhotonAbsorptionProbability instance = null;
    private double absorptionsProbability = DEFAULT_ABSORPTION_PROBABILITY;
  
    /**
     * Constructor.  This is private since it is not meant to be explicitly
     * instantiated.
     */
    private SingleMoleculePhotonAbsorptionProbability(){
        
    }
    
    public static SingleMoleculePhotonAbsorptionProbability getInstance(){
        if (instance == null){
            instance = new SingleMoleculePhotonAbsorptionProbability();
        }
        return instance;
    }
    
    public double getAbsorptionsProbability() {
        return absorptionsProbability;
    }

    
    public void setAbsorptionsProbability( double absorptionsProbability ) {
        this.absorptionsProbability = absorptionsProbability;
    }
}
