// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Calcium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride.CalciumChlorideCrystal;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Provides growth for calcium chloride crystals.  Works with IncrementalGrowth by giving it specific information about seeding and creating calcium chloride crystals
 *
 * @author Sam Reid
 */
public class CalciumChlorideCrystalGrowth extends IncrementalGrowth<SphericalParticle, CalciumChlorideCrystal> {
    public CalciumChlorideCrystalGrowth( MicroModel model, ItemList<CalciumChlorideCrystal> crystals ) {
        super( model, crystals );
    }

    @Override protected CalciumChlorideCrystal newCrystal( ImmutableVector2D position ) {
        return new CalciumChlorideCrystal( position, randomAngle() );
    }

    //Randomly choose any free sodium or chloride ion to seed the crystal
    protected Option<SphericalParticle> selectSeed() {
        Option<Particle> particleOption = model.freeParticles.selectRandom( Calcium.class, Chloride.class );
        if ( particleOption.isNone() ) { return new None<SphericalParticle>(); }
        else { return new Some<SphericalParticle>( (SphericalParticle) particleOption.get() ); }
    }
}