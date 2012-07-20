// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroShaker;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * This shaker adds calcium chloride to the model when shaken
 *
 * @author Sam Reid
 */
public class CalciumChlorideShaker extends MicroShaker {
    public CalciumChlorideShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type, MicroModel model ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type, model );
    }

    @Override protected void addCrystal( MicroModel model, Vector2D outputPoint ) {
        model.addCalciumChlorideCrystal( new CalciumChlorideCrystal( outputPoint, randomAngle() ) {{ grow( 6 ); }} );
    }

    //Test for creating calcium chloride crystals, which are susceptible to dead ends.  Make sure there are enough successes
    public static void main( String[] args ) {
        for ( int i = 0; i < 10000; i++ ) {
            System.out.println( "creating " + i );
            final int finalI = i;
            new CalciumChlorideCrystal( Vector2D.ZERO, randomAngle() ) {{
                boolean success = grow( 6 );
                if ( !success ) {
                    System.out.println( "Failed to grow at i=" + finalI );
                }
            }};
        }
    }
}