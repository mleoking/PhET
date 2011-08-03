// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalStrategy;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.IncrementalGrowth;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Provides growth for sodium nitrate crystals.  Works with IncrementalGrowth by giving it specific information about seeding and creating sodium nitrate crystals
 *
 * @author Sam Reid
 */
public class SodiumNitrateCrystalGrowth extends IncrementalGrowth<Particle, SodiumNitrateCrystal> {
    public SodiumNitrateCrystalGrowth( MicroModel model, ItemList<SodiumNitrateCrystal> crystals ) {
        super( model, crystals );
    }

    @Override protected SodiumNitrateCrystal newCrystal( ImmutableVector2D position ) {
        return new SodiumNitrateCrystal( position, randomAngle() ) {{setUpdateStrategy( new CrystalStrategy( model, model.sodiumNitrateCrystals, model.sodiumNitrateSaturated ) );}};
    }

    //Randomly choose any free sodium or nitrate to seed the crystal
    protected Option<Particle> selectSeed() {
        return model.freeParticles.selectRandom( Sodium.class, Nitrate.class );
    }
}