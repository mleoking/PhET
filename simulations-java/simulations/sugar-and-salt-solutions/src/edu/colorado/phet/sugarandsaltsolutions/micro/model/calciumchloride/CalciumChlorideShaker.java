// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.CalciumIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.ChlorideIonParticle;

/**
 * This shaker adds calcium chloride to the model when shaken
 *
 * @author Sam Reid
 */
public class CalciumChlorideShaker extends MicroShaker {
    public CalciumChlorideShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type );
    }

    @Override protected void addCrystal( MicroModel model, ImmutableVector2D outputPoint, double volumePerSolidMole, ImmutableVector2D crystalVelocity ) {

        //for a ratio of 2:1, the total number of components must be 3x, but growing it from 1 means we must specify 3x-1 growth steps, such as 2,5,8,...
        int numComponents = 7;
        final int numGrowthSteps = 3 * numComponents - 1;
        CalciumChlorideLattice lattice = generateRandomLattice( new Function0<CalciumChlorideLattice>() {
                                                                    public CalciumChlorideLattice apply() {

                                                                        //TODO: can we get rid of this cast?
                                                                        return (CalciumChlorideLattice) new CalciumChlorideLattice().grow( numGrowthSteps );
                                                                    }
                                                                }, new Function1<CalciumChlorideLattice, Boolean>() {
            public Boolean apply( CalciumChlorideLattice lattice ) {
                //CaCl2 should have twice as many Cl as Ca
                return lattice.count( CalciumIonParticle.class ) * 2 == lattice.count( ChlorideIonParticle.class );
            }
        }
        );
        model.addCalciumChlorideCrystal( new CalciumChlorideCrystal( outputPoint, lattice ) );
    }
}