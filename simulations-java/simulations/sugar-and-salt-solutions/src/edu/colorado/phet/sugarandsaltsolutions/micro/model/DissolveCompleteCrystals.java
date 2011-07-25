// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;

/**
 * This strategy dissolves entire crystals at a time, probably our simulation should use a more incremental approach.
 *
 * @author Sam Reid
 */
public class DissolveCompleteCrystals implements DissolveStrategy {
    //Dissolve the lattice, or parts of it if dissolving the entire lattice would surpass the saturation point
    public ArrayList<? extends Particle> dissolve( ItemList<? extends Crystal> crystals, final Crystal crystal, double saturationPoint ) {

        //Dissolve the lattice and collect the particles that should move about freely
        ArrayList<? extends Particle> components = crystal.dissolve();

        //Remove the crystal from the list so it will no longer keep its constituents together
        crystals.getItems().remove( crystal );

        //Add the free particles to the free particle list so they will be propagated properly
        return components;
    }
}