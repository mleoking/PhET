// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;

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
        super( position, new Chloride().radius + new edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium().radius, angle );
    }

    //Randomly choose an initial particle for the crystal lattice
    public SaltIon createSeed() {
        return random.nextBoolean() ? new SaltIon( new edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium() ) : new SaltIon( new Chloride() );
    }

    //Determine whether Na or Cl should be removed from the crystal when dissolving to maintain the ionic balance
    @Override protected Class<? extends Particle> getMajorityType() {
        return getMajorityType( SaltIon.SodiumIon.class, SaltIon.ChlorideIon.class );
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