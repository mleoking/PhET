// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.glucose;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.AllPairs;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalGrowth;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalStrategy;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.IFormulaUnit;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Provides growth for glucose crystals.  Works with IncrementalGrowth by giving it specific information about seeding and creating glucose crystals
 *
 * @author Sam Reid
 */
public class GlucoseCrystalGrowth extends CrystalGrowth<Glucose, GlucoseCrystal> {
    public GlucoseCrystalGrowth( MicroModel model, ItemList<GlucoseCrystal> crystals ) {
        super( model, crystals );
    }

    @Override protected ArrayList<IFormulaUnit> getAllSeeds() {
        return new AllPairs( model.freeParticles, Glucose.class, Glucose.class );
    }

    @Override protected GlucoseCrystal newCrystal( Vector2D position ) {
        return new GlucoseCrystal( position, randomAngle() ) {{setUpdateStrategy( new CrystalStrategy( model, model.glucoseCrystals, model.glucoseSaturated ) );}};
    }
}