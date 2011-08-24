// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Formula;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.CHLORIDE;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SODIUM;

/**
 * In order to treat sucrose and sodium chloride uniformly in the water tab, we use two levels of hierarchy for each:
 * Crystal contains Compound contains Atom
 * However, for sodium chloride all compounds just contain the single atom, which is modeled by SaltIon.
 * <p/>
 * This allows us to efficiently reuse software components in both the model and the view.
 *
 * @author Sam Reid
 */
public class SodiumChlorideCrystal extends Crystal<SaltIon> {

    public SodiumChlorideCrystal( ImmutableVector2D position, double angle ) {
        super( new Formula( SaltIon.SodiumIon.class, SaltIon.ChlorideIon.class ), position, new Chloride().radius + new edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium().radius, angle );
    }

    //Randomly choose an initial particle for the crystal lattice
    public SaltIon createConstituentParticle( Class<? extends Particle> type ) {
        return type == SaltIon.SodiumIon.class ? new SaltIon( new SphericalParticle.Sodium(), SODIUM ) : new SaltIon( new Chloride(), CHLORIDE );
    }

    //Determine whether Na or Cl should be added to the crystal when growing to maintain the ionic balance
    @Override public Class<? extends Particle> getMinorityType() {
        return getMinorityType( SaltIon.SodiumIon.class, SaltIon.ChlorideIon.class );
    }

    //Create the bonding partner for growing the crystal
    @Override public SaltIon createPartner( SaltIon original ) {
        return original instanceof SaltIon.SodiumIon ? new SaltIon.ChlorideIon() : new SaltIon.SodiumIon();
    }
}