// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SaltShakerNode;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSalt;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;

/**
 * Model element for the salt shaker, which includes its position and rotation and adds salt to the model when shaken.
 * Shaking (by acceleration and deceleration) along the axis produce salt.
 *
 * @author Sam Reid
 */
public class SaltShaker extends Dispenser {
    //Some randomness in number of generated crystals when shaken
    private final Random random = new Random();

    //Keep track of how much the salt shaker was shaken, if so, then generate salt on the next updateModel() step
    private double shakeAmount;

    //Keep track of recorded positions when the shaker is translated so we can compute accelerations, which are responsible for shaking out the salt
    private ArrayList<ImmutableVector2D> positions = new ArrayList<ImmutableVector2D>();

    public SaltShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale ) {
        super( x, y, Math.PI * 3 / 4, beaker, moreAllowed, name, distanceScale );
        moreAllowed.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean allowed ) {
                //If the shaker is emptied, prevent spurious grains from coming out the next time it is refilled by setting the shake amount to 0.0 and clearing the sampled positions
                if ( !allowed ) {
//                    System.out.println( "SaltShaker.apply. cleared" );
                    shakeAmount = 0;
                    positions.clear();
                }
            }
        } );
    }

    //Translate the dispenser by the specified delta in model coordinates
    public void translate( Dimension2D delta ) {
        super.translate( delta );

        //Only increment the shake amount if the shaker is non-empty, otherwise when it refills it might automatically emit salt even though the user isn't controlling it
        if ( moreAllowed.get() ) {
            //Add the new position to the list, but keep the list short so there is no memory leak
            positions.add( center.get() );
            while ( positions.size() > 50 ) {
                positions.remove( 0 );
            }

            //Make sure we have enough data, then compute accelerations of the shaker in the direction of its axis
            //to determine how much to shake out
            if ( positions.size() >= 20 ) {

                //Average the second derivatives
                ImmutableVector2D sum = new ImmutableVector2D();
                int numIterations = 10;
                for ( int i = 0; i < numIterations; i++ ) {
                    sum = sum.plus( getSecondDerivative( i ) );
                }
                sum = sum.times( 1.0 / numIterations );

                //But only take the component along the axis
                double dist = Math.abs( sum.dot( parseAngleAndMagnitude( 1, angle.get() + Math.PI / 2 ) ) );//Have to rotate by 90 degrees since for positions 0 degrees is to the right, but for the shaker 0 degrees is up

                //Account for the distance scale so we produce the same amount for micro translations as for macro translations
                dist = dist * distanceScale;

                //only add to the shake amount if it was vigorous enough
                if ( dist > 1E-4 ) {
                    shakeAmount += dist;
//                    System.out.println( "shakeAmount = " + shakeAmount );
                }
            }
        }
    }

    //Called when the model steps in time, and adds any salt crystals to the sim if the dispenser is pouring
    public void updateModel( SugarAndSaltSolutionModel model ) {
        //Check to see if we should be emitting salt crystals-- if the shaker was shaken enough
        if ( enabled.get() && shakeAmount > 0 && moreAllowed.get() ) {
//            System.out.println( "Emitted salt, shake amount = " + shakeAmount + ", moreAllowed = " + moreAllowed.get() );
            int numCrystals = (int) ( random.nextInt( 2 ) + Math.min( shakeAmount * 4000, 4 ) );
            for ( int i = 0; i < numCrystals; i++ ) {
                //Determine where the salt should come out
                double randUniform = ( random.nextDouble() - 0.5 ) * 2;
                final ImmutableVector2D outputPoint = center.get().plus( parseAngleAndMagnitude( 0.027 / distanceScale, angle.get() + Math.PI / 2 + randUniform * Math.PI / 32 * 1.2 ) );//Hand tuned to match up with the image, will need to be re-tuned if the image changes

                //Add the salt
                model.addMacroSalt( new MacroSalt( outputPoint, model.salt.volumePerSolidMole ) {{
                    //Give the salt an appropriate velocity when it comes out so it arcs
                    velocity.set( getCrystalVelocity( outputPoint ) );
                }} );
                shakeAmount = 0.0;

                //don't clear the position array here since the user may still be shaking the shaker
            }
        }
    }

    //Create a SaltShakerNode for display and interaction with this model element
    @Override public PNode createNode( ModelViewTransform transform, double beakerHeight ) {
        return new SaltShakerNode( transform, this, beakerHeight );
    }

    @Override public void reset() {
        super.reset();
        //Additionally make it so it won't emit salt right after reset
        shakeAmount = 0.0;
        positions.clear();
    }

    private ImmutableVector2D getSecondDerivative( int i ) {
        ImmutableVector2D x0 = positions.get( positions.size() - 1 - i );
        ImmutableVector2D x1 = positions.get( positions.size() - 2 - i );
        ImmutableVector2D x2 = positions.get( positions.size() - 3 - i );

        return x0.minus( x1.times( 2 ) ).plus( x2 );
    }
}