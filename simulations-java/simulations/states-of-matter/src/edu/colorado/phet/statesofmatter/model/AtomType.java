/* Copyright 2009, University of Colorado */

package edu.colorado.phet.statesofmatter.model;


/**
 * This is essentially an enum for the atom types used in this simulation.  We
 * are restricted to Java 1.4 at this time, so enums are not available.
 * 
 * @author John Blanco
 */
public class AtomType{
	
	private String m_name;
	
	public static final AtomType NEON = new AtomType("Neon");
	public static final AtomType ARGON = new AtomType("Argon");
	public static final AtomType OXYGEN = new AtomType("Oxygen");
	public static final AtomType HYDROGEN = new AtomType("Hydrogen");
	public static final AtomType ADJUSTABLE = new AtomType("Adjustable");
	public static final AtomType UNDEFINED = new AtomType("Undefined");  // TODO: This may be able to be removed if and
	                                                                     // when the base atom class becomes abstract.

	public AtomType(String name) {
		this.m_name = name;
	}
	
	public String toString(){
		return m_name;
	}
}

