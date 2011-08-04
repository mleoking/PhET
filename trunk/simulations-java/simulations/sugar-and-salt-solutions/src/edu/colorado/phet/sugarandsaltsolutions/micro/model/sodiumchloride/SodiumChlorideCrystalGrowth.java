// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalStrategy;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.IncrementalGrowth;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.ParticlePair;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Provides growth for sodium chloride crystals.  Works with IncrementalGrowth by giving it specific information about seeding and creating sodium chloride crystals
 *
 * @author Sam Reid
 */
public class SodiumChlorideCrystalGrowth extends IncrementalGrowth<SphericalParticle, SodiumChlorideCrystal> {
    public SodiumChlorideCrystalGrowth( MicroModel model, ItemList<SodiumChlorideCrystal> crystals ) {
        super( model, crystals );
    }

    @Override protected ArrayList<ParticlePair> getAllPairs() {
        return generateAllPairs( Sodium.class, Chloride.class );
    }

    @Override protected SodiumChlorideCrystal newCrystal( ImmutableVector2D position ) {
        return new SodiumChlorideCrystal( position, randomAngle() ) {{setUpdateStrategy( new CrystalStrategy( model, model.sodiumChlorideCrystals, model.sodiumChlorideSaturated ) );}};
    }

}