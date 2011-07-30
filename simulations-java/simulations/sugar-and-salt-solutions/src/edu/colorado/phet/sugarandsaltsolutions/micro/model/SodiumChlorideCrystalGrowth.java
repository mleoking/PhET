// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideCrystal;

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

    @Override protected SodiumChlorideCrystal toCrystal( SphericalParticle particle ) {
        return new SodiumChlorideCrystal( particle.getPosition(), randomAngle() );
    }

    protected Option<?> selectSeed() {
        return model.freeParticles.selectRandom( Sodium.class, Chloride.class );
    }
}