/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AtomicNucleus;

/**
 * This class represents a model element that is generally the product
 * of a fission reaction.  This is a non-composite nucleus, meaning that it
 * does not create or keep track of constituent nucleons.
 *
 * @author John Blanco
 */
public class DaughterNucleus extends AtomicNucleus {

    public DaughterNucleus(NuclearPhysicsClock clock, Point2D position, int numProtons, int numNeutrons){

        super(clock, position, numProtons, numNeutrons);
        
    }

}
