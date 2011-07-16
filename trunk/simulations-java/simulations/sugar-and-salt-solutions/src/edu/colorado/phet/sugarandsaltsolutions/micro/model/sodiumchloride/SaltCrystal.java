package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.util.Units;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeConstituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Bond;

/**
 * This crystal for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SaltCrystal extends Crystal {
    public SaltCrystal( ImmutableVector2D position, SaltLattice lattice, double sizeScale ) {
        super( position, sizeScale );

        //Recursive method to traverse the graph and create particles
        fill( lattice, lattice.components.getFirst(), new ArrayList<edu.colorado.phet.sugarandsaltsolutions.micro.model.Component>(), new ImmutableVector2D() );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    //Recursive method to traverse the graph and create particles
    private void fill( SaltLattice lattice, edu.colorado.phet.sugarandsaltsolutions.micro.model.Component component, ArrayList<edu.colorado.phet.sugarandsaltsolutions.micro.model.Component> handled, ImmutableVector2D relativePosition ) {
        final double chlorideRadius = Units.picometersToMeters( 181 ) * sizeScale;
        final double sodiumRadius = Units.picometersToMeters( 102 ) * sizeScale;
        final double spacing = chlorideRadius + sodiumRadius;
        if ( component instanceof SodiumIon ) {
            latticeConstituents.add( new LatticeConstituent( new SphericalParticle( sodiumRadius, new ImmutableVector2D( 0, 0 ), Color.red ), relativePosition ) );
        }
        else {
            latticeConstituents.add( new LatticeConstituent( new SphericalParticle( chlorideRadius, new ImmutableVector2D( 0, 0 ), Color.blue ), relativePosition ) );
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