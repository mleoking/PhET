// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;

/**
 * Update the crystals by moving them about and possibly dissolving them
 * Dissolve the crystals if they are below the saturation points
 * In CaCl2, the factor of 2 accounts for the fact that CaCl2 needs 2 Cl- for every 1 Ca2+
 * No saturation point for ethanol, which is miscible
 *
 * @author Sam Reid
 */
public class CrystalStrategy extends UpdateStrategy {
    //Strategy rule to use for dissolving the crystals
    public final IncrementalDissolve incrementalDissolve;
    private ItemList<? extends Crystal> crystals;
    private ObservableProperty<Boolean> saturated;

    public CrystalStrategy( MicroModel model, ItemList<? extends Crystal> crystals, ObservableProperty<Boolean> saturated ) {
        super( model );
        this.crystals = crystals;
        this.saturated = saturated;
        incrementalDissolve = new IncrementalDissolve( model );
    }

    @Override public void stepInTime( Particle particle, double dt ) {
        Crystal crystal = (Crystal) particle;

        //If the crystal has ever gone underwater, set a flag so that it can be kept from leaving the top of the water
        if ( solution.shape.get().contains( crystal.getShape().getBounds2D() ) ) {
            crystal.setSubmerged();
        }

        //Accelerate the particle due to gravity and perform an euler integration step
        //This number was obtained by guessing and checking to find a value that looked good for accelerating the particles out of the shaker
        double mass = 1E10;

        //Cache the value to improve performance by 30% when number of particles is large
        final boolean anyPartUnderwater = model.isAnyPartUnderwater( crystal );

        //If any part touched the water, the lattice should slow down and move at a constant speed
        if ( anyPartUnderwater ) {
            crystal.velocity.set( new ImmutableVector2D( 0, -1 ).times( 0.25E-9 ) );
        }

        //Collide with the bottom of the beaker before doing underwater check so that crystals will dissolve
        model.boundToBeakerBottom( crystal );

        //If completely underwater, lattice should prepare to dissolve
        if ( !crystal.isUnderwaterTimeRecorded() && !model.isCrystalTotallyAboveTheWater( crystal ) ) {
            crystal.setUnderwater( model.getTime() );
        }
        crystal.stepInTime( model.getExternalForce( anyPartUnderwater ).times( 1.0 / mass ), dt );

        //Collide with the bottom of the beaker
        model.boundToBeakerBottom( crystal );


        boolean dissolve = false;
        //Determine whether it is time for the lattice to dissolve
        if ( crystal.isUnderwaterTimeRecorded() ) {
            final double timeUnderwater = model.getTime() - crystal.getUnderWaterTime();

            //Make sure it has been underwater for a certain period of time (in seconds)
            if ( timeUnderwater > 0.5 ) {
                dissolve = true;
            }
        }

        //Keep the particle within the beaker solution bounds
        model.preventFromLeavingBeaker( crystal );

        //Dissolve the crystal if necessary
        if ( dissolve ) {
            incrementalDissolve.dissolve( crystals, crystal, saturated );
        }
    }
}