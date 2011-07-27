// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

/**
 * This strategy dissolves entire crystals at a time, probably our simulation should use a more incremental approach.
 * TODO: Can be deleted when partial dissolve strategies are working properly.
 *
 * @author Sam Reid
 */
public class DissolveCompleteCrystals implements DissolveStrategy {
    //Dissolve the lattice, or parts of it if dissolving the entire lattice would surpass the saturation point
    public void dissolve( ItemList<? extends Crystal> crystals, final Crystal crystal, ItemList<Particle> freeParticles, ObservableProperty<Boolean> concentration ) {

        //Dissolve the lattice and collect the particles that should move about freely
        ArrayList<? extends Particle> components = crystal.dissolve();

        //Remove the crystal from the list so it will no longer keep its constituents together
        crystals.remove( crystal );

        //Add the released particles into the set of free particles so they will move with random motion
        freeParticles.addAll( components );
    }
}