// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Dimension2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Model element for the salt shaker, which includes its position and rotation and adds salt to the model when shaken
 *
 * @author Sam Reid
 */
public class SaltShaker extends Dispenser {
    private final Random random = new Random();

    //Keep track of whether the salt shaker was shaken, if so, then generate salt on the next updateModel() step
    private boolean translated;

    public SaltShaker( double x, double y, Beaker beaker ) {
        super( x, y, Math.PI * 3 / 4, beaker );
    }

    @Override public void translate( Dimension2D delta ) {
        super.translate( delta );
        translated = true;
    }

    //Called when the model steps in time, and adds any salt crystals to the sim if the dispenser is pouring
    public void updateModel( SugarAndSaltSolutionModel model ) {
        //Check to see if we should be emitting salt crystals-- if the shaker was shaken up then down it will be ready to emit salt
        if ( enabled.get() && translated ) {
            int numCrystals = random.nextInt( 6 ) + 2;
            for ( int i = 0; i < numCrystals; i++ ) {
                //Determine where the salt should come out
                double randUniform = ( random.nextDouble() - 0.5 ) * 2;
                final ImmutableVector2D outputPoint = center.get().plus( ImmutableVector2D.parseAngleAndMagnitude( 0.027, angle.get() + Math.PI / 2 + randUniform * Math.PI / 32 * 1.2 ) );//Hand tuned to match up with the image, will need to be re-tuned if the image changes

                //Add the salt
                model.addSalt( new Salt( outputPoint ) {{
                    //Give the salt an appropriate velocity when it comes out so it arcs
                    velocity.set( getCrystalVelocity( outputPoint ) );
                }} );
                translated = false;
            }
        }
    }
}