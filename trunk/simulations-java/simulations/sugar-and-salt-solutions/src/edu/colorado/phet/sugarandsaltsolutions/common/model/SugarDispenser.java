// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model element for the sugar dispenser, which includes its position and rotation.
 *
 * @author Sam Reid
 */
public class SugarDispenser extends Dispenser {
    public final Property<Boolean> enabled = new Property<Boolean>( false );
    public final Property<Boolean> open = new Property<Boolean>( false );

    //Keep track of the number of model updates so that sugar crystals can be added once every n steps (to decrease output sugar density)
    protected int updateModelCount = 0;

    public SugarDispenser( double x, double y, Beaker beaker ) {
        super( x, y, beaker );
    }

    public void translate( Dimension2D delta ) {
        super.translate( delta );
        open.set( angle.get() > Math.PI / 2 );
    }

    //Called when the model steps in time, and adds any sugar crystals to the sim if the dispenser is pouring
    public void updateModel( SugarAndSaltSolutionModel sugarAndSaltSolutionModel ) {
        updateModelCount++;

        //Check to see if we should be emitting sugar crystals-- if the sugar is enabled and its top is open, and we haven't emitted sugar in the previous steps
        if ( enabled.get() && open.get() && updateModelCount % 1 == 0 ) {

            //Determine where the sugar should come out
            final ImmutableVector2D outputPoint = center.get().plus( ImmutableVector2D.parseAngleAndMagnitude( 0.03, angle.get() + Math.PI / 2 * 1.23 ) );//Hand tuned to match up with the image, will need to be re-tuned if the image changes

            //Add the sugar
            sugarAndSaltSolutionModel.addSugar( new Sugar( outputPoint ) {{
                velocity.set( getCrystalVelocity( outputPoint ) );
            }} );
        }
    }
}