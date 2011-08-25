// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalGrowth;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalStrategy;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.IFormulaUnit;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Provides growth for calcium chloride crystals.  Works with IncrementalGrowth by giving it specific information about seeding and creating calcium chloride crystals
 *
 * @author Sam Reid
 */
public class CalciumChlorideCrystalGrowth extends CrystalGrowth<SphericalParticle, CalciumChlorideCrystal> {
    public CalciumChlorideCrystalGrowth( MicroModel model, ItemList<CalciumChlorideCrystal> crystals ) {
        super( model, crystals );
    }

    @Override protected ArrayList<IFormulaUnit> getAllSeeds() {

        ItemList<Particle> aList = model.freeParticles.filter( SphericalParticle.Calcium.class );
        ItemList<Particle> bList = model.freeParticles.filter( SphericalParticle.Chloride.class );
        ArrayList<IFormulaUnit> formulaUnits = new ArrayList<IFormulaUnit>();
        for ( Particle a : aList ) {
            for ( Particle b : bList ) {
                for ( Particle c : bList ) {
                    //Check for equality in case typeA==typeB, as in the case of Sucrose
                    if ( b != c ) {
                        formulaUnits.add( new ThreeParticleFormulaUnit<Particle>( a, b, c ) );
                    }
                }
            }
        }
        return formulaUnits;

    }

    @Override protected CalciumChlorideCrystal newCrystal( ImmutableVector2D position ) {
        return new CalciumChlorideCrystal( position, randomAngle() ) {{setUpdateStrategy( new CrystalStrategy( model, model.calciumChlorideCrystals, model.calciumChlorideSaturated ) );}};
    }
}