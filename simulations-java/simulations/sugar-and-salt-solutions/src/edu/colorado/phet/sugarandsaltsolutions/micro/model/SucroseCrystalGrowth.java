// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseCrystal;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Provides growth for sucrose crystals.  Works with IncrementalGrowth by giving it specific information about seeding and creating sucrose crystals
 *
 * @author Sam Reid
 */
public class SucroseCrystalGrowth extends IncrementalGrowth<Sucrose, SucroseCrystal> {
    public SucroseCrystalGrowth( MicroModel model, ItemList<SucroseCrystal> crystals ) {
        super( model, crystals );
    }

    @Override protected SucroseCrystal newCrystal( ImmutableVector2D position ) {
        return new SucroseCrystal( position, randomAngle() );
    }

    //Randomly choose any free sucrose molecule to see the crystal
    protected Option<Sucrose> selectSeed() {
        Option<Particle> particleOption = model.freeParticles.selectRandom( Sucrose.class );
        if ( particleOption.isNone() ) { return new None<Sucrose>(); }
        else { return new Some<Sucrose>( (Sucrose) particleOption.get() ); }
    }
}