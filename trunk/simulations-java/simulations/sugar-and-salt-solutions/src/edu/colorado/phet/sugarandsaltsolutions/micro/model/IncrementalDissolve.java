// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

import static java.lang.Math.PI;
import static java.lang.Math.random;

/**
 * This strategy dissolves crystals incrementally so that the concentration will be below or near the saturation point.
 *
 * @author Sam Reid
 */
public class IncrementalDissolve {

    //Debugging tool to visualize the dissolving process
    long lastDissolve = System.currentTimeMillis();

    private final MicroModel model;

    public IncrementalDissolve( MicroModel model ) {
        this.model = model;
    }

    //Dissolve the lattice incrementally so that we get as close as possible to the saturation point
    public void dissolve( ItemList crystals, final Crystal crystal, ObservableProperty<Boolean> unsaturated ) {

        while ( unsaturated.get() && crystal.numberConstituents() > 0

                //For some unknown reason, limiting this to one dissolve element per step fixes bugs in dissolving the lattices
                //Without this limit, crystals do not dissolve when they should
                && System.currentTimeMillis() - lastDissolve > 2
                ) {
            lastDissolve = System.currentTimeMillis();
            Constituent constituent = crystal.getConstituentToDissolve( model.solution.shape.get().getBounds2D() );
            if ( constituent != null ) {
                constituent.particle.velocity.set( crystal.velocity.get().getRotatedInstance( random() * PI * 2 ) );

                if ( model.freeParticles.contains( constituent.particle ) ) {
                    System.out.println( "Error: tried to free a particle that was already free" );
                }
                else {
                    model.freeParticles.add( constituent.particle );
                }

                crystal.removeConstituent( constituent );
            }
        }

        //Remove the crystal from the list so it will no longer keep its constituents together
        if ( crystal.numberConstituents() == 0 ) {
            crystals.remove( crystal );
        }
    }
}