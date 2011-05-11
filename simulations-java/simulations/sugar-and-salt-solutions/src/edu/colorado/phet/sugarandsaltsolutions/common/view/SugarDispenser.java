// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Sugar;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;

/**
 * Model element for the sugar dispenser, which includes its position and rotation.
 *
 * @author Sam Reid
 */
public class SugarDispenser {
    //Start centered above the fluid
    public final Property<ImmutableVector2D> rotationPoint = new Property<ImmutableVector2D>( new ImmutableVector2D( -0.018626373626373614, 0.5091208791208807 ) );//Values sampled from a sim runtime
    public final DoubleProperty angle = new DoubleProperty( 0.0 );
    public final Property<Boolean> enabled = new Property<Boolean>( false );
    public final Property<Boolean> open = new Property<Boolean>( false );

    //Keep track of the number of model updates so that sugar crystals can be added once every n steps (to decrease output sugar density)
    private int updateModelCount = 0;

    public void rotate( double v ) {
        angle.add( v );
    }

    public void translate( Dimension2D delta ) {
        ImmutableVector2D proposedPoint = rotationPoint.get().plus( delta );
        double y = MathUtil.clamp( 0.4, proposedPoint.getY(), Double.POSITIVE_INFINITY );
        rotationPoint.set( new ImmutableVector2D( proposedPoint.getX(), y ) );
        double yRotate = 0.5;//Below this y-value, the sugar dispenser will rotate
        if ( rotationPoint.get().getY() < yRotate ) {
            double amountPast = yRotate - rotationPoint.get().getY();
            angle.set( amountPast * 20 );
        }
        open.set( angle.get() > Math.PI / 2 );
    }

    //Called when the model steps in time, and adds any sugar crystals to the sim if the dispenser is pouring
    public void updateModel( SugarAndSaltSolutionModel sugarAndSaltSolutionModel ) {
        updateModelCount++;

        //Check to see if we should be emitting sugar crystals-- if the sugar is enabled and its top is open, and we haven't emitted sugar in the previous steps
        if ( enabled.get() && open.get() && updateModelCount % 2 == 0 ) {

            //Determine where the sugar should come out
            final ImmutableVector2D outputPoint = rotationPoint.get().plus( ImmutableVector2D.parseAngleAndMagnitude( 0.105, angle.get() + Math.PI / 2 * 1.23 ) );//Hand tuned to match up with the image, will need to be re-tuned if the image changes

            //Add the sugar
            sugarAndSaltSolutionModel.addSugar( new Sugar( outputPoint ) {{

                //Give the sugar an appropriate velocity when it comes out so it arcs
                ImmutableVector2D directionVector = outputPoint.minus( rotationPoint.get() );
                double anglePastTheHorizontal = angle.get() - Math.PI / 2;
                velocity.set( directionVector.getInstanceOfMagnitude( 0.4 + 0.3 * Math.sin( anglePastTheHorizontal ) ) );
            }} );
        }
    }

    public void reset() {
        //Only need to set the primary properties, others (e.g., open/enabled) are derived and will auto-reset
        rotationPoint.reset();
        angle.reset();
    }
}