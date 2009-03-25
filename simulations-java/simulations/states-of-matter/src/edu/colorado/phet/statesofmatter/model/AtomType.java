/* Copyright 2009, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;

/**
 * This is essentially an enum for the atom types used in this simulation.  We
 * are restricted to Java 1.4 at this time, so enums are not available.
 * 
 * @author John Blanco
 */
public class AtomType{
	private String m_name;
	
	public static final AtomType NEON = new AtomType("Neon", 308, 32.8);
	public static final AtomType ARGON = new AtomType("Argon", 362, 111.84);
	public static final AtomType OXYGEN = new AtomType("Oxygen", 324, 200);
	public static final AtomType ADJUSTABLE = new AtomType("Adjustable", 390, 155);

    private double m_sigma;    // Sigma represents the diameter of the molecule, roughly speaking.
    private double m_epsilon;  // Epsilon is the interaction potential for two of this type of molecule.  The units
                               // are such that epsilon/k-Boltzmann is in degrees Kelvin.

	public AtomType(String name, double sigma, double epsilon) {
		this.m_name = name;
		this.m_sigma = sigma;
		this.m_epsilon = epsilon;
	}
	
	public String toString(){
		return m_name;
	}
	
	public double getSigma(){
		return m_sigma;
	}
	
	/**
	 * Set the value of sigma for this molecule type.  This should only be
	 * used for the adjustable type, but there is no enforcement of this.
	 * 
	 * @param sigma
	 */
	public void setSigma(double sigma){
        if (sigma > StatesOfMatterConstants.MAX_SIGMA){
            m_sigma = StatesOfMatterConstants.MAX_SIGMA;
        }
        else if ( sigma < StatesOfMatterConstants.MIN_SIGMA ){
            m_sigma = StatesOfMatterConstants.MIN_SIGMA;
        }
        else{
            m_sigma = sigma;
        }
	}
	
	public double getEpsilon(){
		return m_epsilon;
	}
	
	/**
	 * Set the value of epsilon for this molecule type.  This should only be
	 * used for the adjustable type, but there is no enforcement of this.
	 * 
	 * @param epsilon
	 */
	public void setEpsilon(double epsilon){
        if (epsilon > StatesOfMatterConstants.MAX_EPSILON){
            m_epsilon = StatesOfMatterConstants.MAX_EPSILON;
        }
        else if ( epsilon < StatesOfMatterConstants.MAX_EPSILON ){
        	m_epsilon = StatesOfMatterConstants.MAX_EPSILON;
        }
        else{
        	m_epsilon = epsilon;
        }
	}
}

