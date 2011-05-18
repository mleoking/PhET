// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.sugarandsaltsolutions.intro.model.MacroCrystal;

/**
 * Keep track of how many moles of crystal are in the air, since we need to prevent user from adding more than 10 moles to the system
 * That is, we need to shut off salt/sugar when there is salt/sugar in the air that could get added to the solution
 *
 * @author Sam Reid
 */
public class AirborneCrystalMoles extends CompositeDoubleProperty {
    public AirborneCrystalMoles( final ArrayList<? extends MacroCrystal> list ) {
        super( new Function0<Double>() {
            public Double apply() {

                //Sum up the total amount of moles of crystals that are in the air
                double sum = 0;
                for ( MacroCrystal crystal : list ) {
                    if ( crystal.position.get().getY() > 0 ) {
                        sum += crystal.getMoles();
                    }
                }
                return sum;
            }
        } );

        //Notification based on changes is handled in SugarAndSaltSolutionModel when the crystal list is modified
    }
}
