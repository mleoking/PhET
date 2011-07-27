package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.ChlorideIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.SodiumIonParticle;

/**
 * This crystal for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SodiumChlorideCrystal extends Crystal<SphericalParticle> {
    public SodiumChlorideCrystal( ImmutableVector2D position, SodiumChlorideLattice lattice ) {
        super( position, new ChlorideIonParticle().radius + new SodiumIonParticle().radius, new Function2<Component, Double, SphericalParticle>() {
                   public SphericalParticle apply( Component component, Double angle ) {
                       if ( component instanceof SodiumIon ) {
                           return new SodiumIonParticle();
                       }
                       else {
                           return new ChlorideIonParticle();
                       }
                   }
               }, lattice );
    }
}