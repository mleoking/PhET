// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.CHLORIDE;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SODIUM;

/**
 * In order to treat sucrose and sodium chloride uniformly in the water tab, we use two levels of hierarchy for each:
 * Crystal contains Compound contains Atom
 * However, for sodium chloride all compounds just contain the single atom, which is modeled by SaltIon
 * <p/>
 * This allows us to efficiently reuse software components in both the model and the view.
 *
 * @author Sam Reid
 */
public class SaltIon extends Compound<SphericalParticle> {

    private String name;

    public SaltIon( SphericalParticle particle, String name ) {
        super( ZERO, 0 );
        this.name = name;
        addConstituent( new Constituent<SphericalParticle>( particle, ZERO ) );
    }

    //Return the name of the ion such as "Na+" to be shown on the graphic as a label
    public String getName() {
        return name;
    }

    public static class SodiumIon extends SaltIon {
        public SodiumIon() {
            super( new SphericalParticle.Sodium(), SODIUM );
        }
    }

    public static class ChlorideIon extends SaltIon {
        public ChlorideIon() {
            super( new SphericalParticle.Chloride(), CHLORIDE );
        }
    }
}
