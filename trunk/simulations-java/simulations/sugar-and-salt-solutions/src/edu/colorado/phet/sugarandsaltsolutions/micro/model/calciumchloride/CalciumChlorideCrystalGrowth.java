// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Calcium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalStrategy;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.IncrementalGrowth;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.ParticlePair;

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

    @Override protected ArrayList<ParticlePair> getAllPairs() {
        return generateAllPairs( Calcium.class, Chloride.class );
    }

    @Override protected CalciumChlorideCrystal newCrystal( ImmutableVector2D position ) {
        return new CalciumChlorideCrystal( position, randomAngle() ) {{setUpdateStrategy( new CrystalStrategy( model, model.calciumChlorideCrystals, model.calciumChlorideSaturated ) );}};
    }
}