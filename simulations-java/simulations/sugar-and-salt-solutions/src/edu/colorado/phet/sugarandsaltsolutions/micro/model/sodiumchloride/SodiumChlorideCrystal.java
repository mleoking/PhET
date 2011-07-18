package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeConstituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.ChlorideIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.SodiumIonParticle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;

/**
 * This crystal for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SodiumChlorideCrystal extends Crystal {
    public SodiumChlorideCrystal( ImmutableVector2D position, SodiumChlorideLattice lattice ) {
        super( position );

        //Recursive method to traverse the graph and create particles
        fill( lattice, lattice.components.getFirst(), new ArrayList<edu.colorado.phet.sugarandsaltsolutions.micro.model.Component>(), new ImmutableVector2D() );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( ZERO, 0.0 );
    }

    //Recursive method to traverse the graph and create particles
    private void fill( SodiumChlorideLattice lattice, edu.colorado.phet.sugarandsaltsolutions.micro.model.Component component, ArrayList<edu.colorado.phet.sugarandsaltsolutions.micro.model.Component> handled, ImmutableVector2D relativePosition ) {
        final double spacing = new ChlorideIonParticle().radius + new SodiumIonParticle().radius;
        if ( component instanceof SodiumIon ) {
            latticeConstituents.add( new LatticeConstituent( new SodiumIonParticle(), relativePosition ) );
        }
        else {
            latticeConstituents.add( new LatticeConstituent( new ChlorideIonParticle(), relativePosition ) );
        }
        handled.add( component );
        ArrayList<Bond> bonds = lattice.getBonds( component );
        for ( Bond bond : bonds ) {
            if ( !handled.contains( bond.destination ) ) {
                fill( lattice, bond.destination, handled, relativePosition.plus( getDelta( spacing, bond ).getRotatedInstance( angle ) ) );
            }
        }
    }
}