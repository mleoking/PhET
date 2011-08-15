// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.UpdateStrategy.FREE_PARTICLE_SPEED;
import static java.lang.Math.PI;
import static java.lang.Math.random;

/**
 * This strategy dissolves crystals incrementally so that the concentration will be below or near the saturation point.
 *
 * @author Sam Reid
 */
public class IncrementalDissolve<T extends Particle> {

    //Debugging tool to visualize the dissolving process
    private long lastDissolve = System.currentTimeMillis();

    private final MicroModel model;

    public IncrementalDissolve( MicroModel model ) {
        this.model = model;
    }

    //Dissolve the lattice incrementally so that we get as close as possible to the saturation point
    public void dissolve( ItemList<Crystal<T>> crystals, final Crystal<T> crystal, ObservableProperty<Boolean> saturated ) {
        while ( !saturated.get() && crystal.numberConstituents() > 0

                //For some unknown reason, limiting this to one dissolve element per step fixes bugs in dissolving the lattices
                //Without this limit, crystals do not dissolve when they should
                && System.currentTimeMillis() - lastDissolve > 2 ) {
            lastDissolve = System.currentTimeMillis();
            Constituent<T> constituent = crystal.getConstituentToDissolve( model.solution.shape.get().getBounds2D() );
            if ( constituent != null ) {
                removeConstituent( crystal, constituent );
            }
        }

        //If the crystal has only one constituent, then dissolve it and remove the crystal.
        //Note that this is the only place in the code where crystals are reduced, so as long as crystals are always created with 2 or more constituents,
        //this will guarantee that there are never any 1-particle crystals.
        //1-particle crystals should be avoided because it is unrealistic for an ion to hang out by itself in solid form; you need both an Na and a Cl to make a salt grain
        if ( crystal.numberConstituents() == 1 ) {
            removeConstituent( crystal, crystal.getConstituent( 0 ) );
        }

        //Remove the crystal from the list so it will no longer keep its constituents together
        if ( crystal.numberConstituents() == 0 ) {
            crystals.remove( crystal );
        }
    }

    //Remove the specified constituent from the containing crystal, by dissolving it off
    private void removeConstituent( Crystal<T> crystal, Constituent<T> constituent ) {

        //If the particle is above the water when dissolved off the crystal, then make sure it starts moving downward, otherwise it will "jump" into the air above the beaker
        boolean particleAboveWater = constituent.particle.getShape().getBounds2D().getMaxY() > model.solution.shape.get().getBounds2D().getMaxY();
        double velocityAngle = particleAboveWater ? 0 : random() * PI * 2;
        ImmutableVector2D velocity = new ImmutableVector2D( 0, -1 ).times( FREE_PARTICLE_SPEED ).getRotatedInstance( velocityAngle );
        constituent.particle.velocity.set( velocity );

        //Remove the constituent from the crystal and instead make it move under a random walk
        crystal.removeConstituent( constituent );
        model.freeParticles.add( constituent.particle );
        constituent.particle.setUpdateStrategy( new FreeParticleStrategy( model ) );
    }
}