// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.CalciumIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.ChlorideIonParticle;

/**
 * Model for growing a calcium chloride lattice, using the same 2d crystal structure as in Soluble Salts.
 *
 * @author Sam Reid
 */
public class CalciumChlorideSite extends LatticeSite<SphericalParticle> {
    public CalciumChlorideSite( SphericalParticle component, BondType type ) {
        super( component, type );
    }

    @Override public Lattice<SphericalParticle> grow( Lattice<SphericalParticle> lattice ) {
        SphericalParticle newIon = ( component instanceof ChlorideIonParticle ) ? new CalciumIonParticle() : new ChlorideIonParticle();
        return new CalciumChlorideLattice( new ImmutableList<SphericalParticle>( lattice.components, newIon ), new ImmutableList<Bond<SphericalParticle>>( lattice.bonds, new Bond<SphericalParticle>( component, newIon, type ) ) );
    }
}
