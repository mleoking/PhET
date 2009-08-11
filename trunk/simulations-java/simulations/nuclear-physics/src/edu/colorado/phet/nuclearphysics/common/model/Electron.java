/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.common.model;

/**
 * This class represents electrons in the model.  For this simulation, since
 * electrons are just shot out of some nuclei, the implementation is very
 * simple.
 * 
 * @author John Blanco
 */
public class Electron extends SubatomicParticle {

	public Electron(double xPos, double yPos) {
		super(xPos, yPos);
	}
}
