// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.AllPairs;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalGrowth;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalStrategy;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.IFormulaUnit;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Provides growth for sodium chloride crystals.  Works with IncrementalGrowth by giving it specific information about seeding and creating sodium chloride crystals
 *
 * @author Sam Reid
 */
public class SodiumChlorideCrystalGrowth extends CrystalGrowth<SphericalParticle, SodiumChlorideCrystal> {
    public SodiumChlorideCrystalGrowth( MicroModel model, ItemList<SodiumChlorideCrystal> crystals ) {
        super( model, crystals );
    }

    @Override protected ArrayList<IFormulaUnit> getAllSeeds() {
        return new AllPairs( model.freeParticles, Sodium.class, Chloride.class );
    }

    @Override protected SodiumChlorideCrystal newCrystal( ImmutableVector2D position ) {
        return new SodiumChlorideCrystal( position, randomAngle() ) {{setUpdateStrategy( new CrystalStrategy( model, model.sodiumChlorideCrystals, model.sodiumChlorideSaturated ) );}};
    }

}