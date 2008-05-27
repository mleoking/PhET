/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;

/**
 * This class represents a model element that is generally the product
 * of a fission reaction.  This is a non-composite nucleus, meaning that it
 * does not create or keep track of constituent nucleons.
 *
 * @author John Blanco
 */
public class DaughterNucleus extends AtomicNucleus {

    public DaughterNucleus(NuclearPhysics2Clock clock, Point2D position, int numProtons, int numNeutrons){

        super(clock, position, numProtons, numNeutrons);
        
    }

}
