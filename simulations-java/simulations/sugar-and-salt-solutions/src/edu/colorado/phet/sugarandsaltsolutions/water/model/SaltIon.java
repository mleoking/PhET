// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Compound;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

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

    public SaltIon( SphericalParticle particle ) {
        super( ImmutableVector2D.ZERO, 0 );
        addConstituent( new Constituent<SphericalParticle>( particle, ImmutableVector2D.ZERO ) );
    }

    public static class SodiumIon extends SaltIon {
        public SodiumIon() {
            super( new SphericalParticle.Sodium() );
        }
    }

    public static class ChlorideIon extends SaltIon {
        public ChlorideIon() {
            super( new SphericalParticle.Chloride() );
        }
    }
}
