// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

/**
 * This strategy dissolves crystals incrementally so that the concentration will be below or near the saturation point.
 *
 * @author Sam Reid
 */
public class IncrementalDissolve implements DissolveStrategy {

    //Debugging tool to visualize the dissolving process
    long lastDissolve = System.currentTimeMillis();

    //Dissolve the lattice incrementally so that we get as close as possible to the saturation point
    public void dissolve( ItemList<? extends Crystal> crystals, final Crystal crystal, ItemList<Particle> freeParticles, ObservableProperty<Boolean> unsaturated ) {

        while ( unsaturated.get() && crystal.numberConstituents() > 0
//                && System.currentTimeMillis() - lastDissolve > 1000
                ) {

            Constituent constituent = crystal.getConstituentToDissolve();
            crystal.dissolve( constituent );

            freeParticles.add( constituent.particle );

            crystal.removeConstituent( constituent );

            lastDissolve = System.currentTimeMillis();
        }

        //Remove the crystal from the list so it will no longer keep its constituents together
        if ( crystal.numberConstituents() == 0 ) {
            crystals.remove( crystal );
        }
    }
}