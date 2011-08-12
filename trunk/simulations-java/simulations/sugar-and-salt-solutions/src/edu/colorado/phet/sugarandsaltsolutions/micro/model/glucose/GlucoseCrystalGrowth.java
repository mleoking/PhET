// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.glucose;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalStrategy;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.IncrementalGrowth;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.ParticlePair;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Provides growth for glucose crystals.  Works with IncrementalGrowth by giving it specific information about seeding and creating glucose crystals
 *
 * @author Sam Reid
 */
public class GlucoseCrystalGrowth extends IncrementalGrowth<Glucose, GlucoseCrystal> {
    public GlucoseCrystalGrowth( MicroModel model, ItemList<GlucoseCrystal> crystals ) {
        super( model, crystals );
    }

    @Override protected ArrayList<ParticlePair> getAllPairs() {
        return generateAllPairs( Glucose.class, Glucose.class );
    }

    @Override protected GlucoseCrystal newCrystal( ImmutableVector2D position ) {
        return new GlucoseCrystal( position, randomAngle() ) {{setUpdateStrategy( new CrystalStrategy( model, model.sucroseCrystals, model.sucroseSaturated ) );}};
    }
}