// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.common.view;

/**
 * Enum of possible nucleus image types that can be used when representing a
 * nucleus in the view.
 * 
 * @author John Blanco
 */
public class AtomicNucleusImageType {

	private final String name;
   	private AtomicNucleusImageType(String name) {
   		this.name = name;
   	}
   	public static final AtomicNucleusImageType NUCLEONS_VISIBLE = new AtomicNucleusImageType("nucleons-visible");
   	public static final AtomicNucleusImageType GRADIENT_SPHERE = new AtomicNucleusImageType("gradient-sphere");
   	public static final AtomicNucleusImageType CIRCLE_WITH_HIGHLIGHT = new AtomicNucleusImageType("circle-with-highlight");

   	public String toString() {
   		return name;
    }
}
