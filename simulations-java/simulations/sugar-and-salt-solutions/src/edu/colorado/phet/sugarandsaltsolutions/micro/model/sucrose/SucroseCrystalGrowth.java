// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.AllPairs;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalGrowth;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.CrystalStrategy;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics.IFormulaUnit;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;

/**
 * Provides growth for sucrose crystals.  Works with IncrementalGrowth by giving it specific information about seeding and creating sucrose crystals
 *
 * @author Sam Reid
 */
public class SucroseCrystalGrowth extends CrystalGrowth<Sucrose, SucroseCrystal> {
    public SucroseCrystalGrowth( MicroModel model, ItemList<SucroseCrystal> crystals ) {
        super( model, crystals );
    }

    @Override protected ArrayList<IFormulaUnit> getAllSeeds() {
        return new AllPairs( model.freeParticles, Sucrose.class, Sucrose.class );
    }

    @Override protected SucroseCrystal newCrystal( ImmutableVector2D position ) {
        return new SucroseCrystal( position, randomAngle() ) {{setUpdateStrategy( new CrystalStrategy( model, model.sucroseCrystals, model.sucroseSaturated ) );}};
    }

}