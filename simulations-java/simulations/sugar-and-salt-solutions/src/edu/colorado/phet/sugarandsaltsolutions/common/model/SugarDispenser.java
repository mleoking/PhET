// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroSugar;

/**
 * Model element for the sugar dispenser, which includes its position and rotation.  Sugar is emitted from the sugar dispenser only
 * while the user is rotating it, and while it is at a good angle (quadrant III).
 *
 * @author Sam Reid
 */
public class SugarDispenser extends Dispenser {
    //True if the flap on the top of the dispenser is open and sugar can flow out
    public final Property<Boolean> open = new Property<Boolean>( false );

    //Randomness for the outgoing crystal velocity
    private final Random random = new Random();

    private boolean translating = false;
    private ArrayList<ImmutableVector2D> positions = new ArrayList<ImmutableVector2D>();

    public SugarDispenser( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed ) {
        super( x, y, 1.2, beaker, moreAllowed );
    }

    @Override public void translate( Dimension2D delta ) {
        super.translate( delta );
        setTranslating( true );
    }

    private void setTranslating( boolean translating ) {
        this.translating = translating;
        open.set( translating );
    }

    //Called when the model steps in time, and adds any sugar crystals to the sim if the dispenser is pouring
    public void updateModel( SugarAndSaltSolutionModel sugarAndSaltSolutionModel ) {

        //Add the new position to the list, but keep the list short so there is no memory leak.  The list size also determines the lag time for when the shaker rotates down and up
        positions.add( center.get() );
        while ( positions.size() > 8 ) {
            positions.remove( 0 );
        }

        //Keep track of speeds, since we use a nonzero speed to rotate the dispenser
        ArrayList<Double> speeds = new ArrayList<Double>();
        for ( int i = 0; i < positions.size() - 1; i++ ) {
            ImmutableVector2D a = positions.get( i );
            ImmutableVector2D b = positions.get( i + 1 );
            speeds.add( a.minus( b ).getMagnitude() );
        }

        //Compute the average speed over the last positions.size() time steps
        double sum = 0.0;
        for ( Double speed : speeds ) {
            sum += speed;
        }
        double avgSpeed = sum / speeds.size();

        //Should be considered to be translating only if it was moving fast enough
        setTranslating( avgSpeed > 1E-5 );

        //animate toward the target angle
        double tiltedDownAngle = 2.0;
        double tiltedUpAngle = 1.2;
        double targetAngle = translating ? tiltedDownAngle : tiltedUpAngle;
        double delta = 0;
        double deltaMagnitude = 0.25;
        if ( targetAngle > angle.get() ) {
            delta = deltaMagnitude;
        }
        else if ( targetAngle < angle.get() ) {
            delta = -deltaMagnitude;
        }

        //Make sure it doesn't go past the final angles or it will stutter
        double proposedAngle = angle.get() + delta;
        if ( proposedAngle > tiltedDownAngle ) { proposedAngle = tiltedDownAngle; }
        if ( proposedAngle < tiltedUpAngle ) { proposedAngle = tiltedUpAngle; }
        angle.set( proposedAngle );

        //Check to see if we should be emitting sugar crystals-- if the sugar is enabled and its top is open and it is rotating
        if ( enabled.get() && translating && angle.get() > Math.PI / 2 && moreAllowed.get() ) {

            //Then emit a number of crystals proportionate to the amount the dispenser was rotated so that vigorous rotation emits more, but clamping it so there can't be too many
            int numCrystals = (int) MathUtil.clamp( 1, (int) avgSpeed * 5, 5 );
            for ( int i = 0; i < numCrystals; i++ ) {
                //Determine where the sugar should come out
                final ImmutableVector2D outputPoint = center.get().plus( ImmutableVector2D.parseAngleAndMagnitude( 0.03, angle.get() + Math.PI / 2 * 1.23 ) );//Hand tuned to match up with the image, will need to be re-tuned if the image changes

                //Add the sugar, with some randomness in the velocity
                sugarAndSaltSolutionModel.addMacroSugar( new MacroSugar( outputPoint ) {{
                    velocity.set( getCrystalVelocity( outputPoint ).plus( ( random.nextDouble() - 0.5 ) * 0.05, ( random.nextDouble() - 0.5 ) * 0.05 ) );
                }} );
            }
        }
    }

    @Override public void reset() {
        super.reset();
        //Additionally reset the user drag events so the user has to drag it again to create sugar
        translating = false;
        positions.clear();
    }
}