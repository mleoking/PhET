// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Model element for the sugar dispenser, which includes its position and rotation.  Sugar is emitted from the sugar dispenser only
 * while the user is rotating it, and while it is at a good angle (quadrant III).
 *
 * @author Sam Reid
 */
public class SugarDispenser extends Dispenser {
    //True if the user has chosen to use sugar
    public final Property<Boolean> enabled = new Property<Boolean>( false );

    //True if the flap on the top of the dispenser is open and sugar can flow out
    public final Property<Boolean> open = new Property<Boolean>( false );

    //Randomness for the outgoing crystal velocity
    private final Random random = new Random();

    //Below this y-value, the dispenser will rotate
    public double deltaAngle = 0.0;

    public SugarDispenser( double x, double y, Beaker beaker ) {
        super( x, y, 0.0, beaker );
        //Open the top of the dispenser if the sugar dispenser is pointed down and to the left
        angle.addObserver( new VoidFunction1<Double>() {
            public void apply( Double angle ) {
                open.set( angle > Math.PI / 2 );
            }
        } );

        //keep track of the changes in angle since the amount of sugar emitted is proportional to how much the user has rotated the sugar dispenser
        angle.addObserver( new ChangeObserver<Double>() {
            public void update( Double newValue, Double oldValue ) {
                deltaAngle += Math.abs( newValue - oldValue );
            }
        } );
    }

    //Called when the model steps in time, and adds any sugar crystals to the sim if the dispenser is pouring
    public void updateModel( SugarAndSaltSolutionModel sugarAndSaltSolutionModel ) {
        //Check to see if we should be emitting sugar crystals-- if the sugar is enabled and its top is open and it is rotating
        if ( enabled.get() && open.get() && deltaAngle > 0 ) {

            //Then emit a number of crystals proportionate to the amount the dispenser was rotated so that vigorous rotation emits more, but clamping it so there can't be too many
            int numCrystals = (int) MathUtil.clamp( 1, (int) deltaAngle * 5, 5 );
            for ( int i = 0; i < numCrystals; i++ ) {
                //Determine where the sugar should come out
                final ImmutableVector2D outputPoint = center.get().plus( ImmutableVector2D.parseAngleAndMagnitude( 0.03, angle.get() + Math.PI / 2 * 1.23 ) );//Hand tuned to match up with the image, will need to be re-tuned if the image changes

                //Add the sugar, with some randomness in the velocity
                sugarAndSaltSolutionModel.addSugar( new Sugar( outputPoint ) {{
                    velocity.set( getCrystalVelocity( outputPoint ).plus( ( random.nextDouble() - 0.5 ) * 0.05, ( random.nextDouble() - 0.5 ) * 0.05 ) );
                }} );
            }

            //Clear out any stored shakes, so the user has to keep rotating the sugar dispenser
            deltaAngle = 0;
        }
    }
}