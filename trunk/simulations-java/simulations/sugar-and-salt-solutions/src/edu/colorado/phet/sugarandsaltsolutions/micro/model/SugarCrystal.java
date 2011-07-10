package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.util.Units;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.SugarLattice;

/**
 * This crystal for sugar updates the positions of the molecules to ensure they move together
 *
 * @author Sam Reid
 */
public class SugarCrystal extends Crystal {
    public SugarCrystal( ImmutableVector2D position, SugarLattice lattice, double sizeScale ) {
        super( position, sizeScale );

        //Recursive method to traverse the graph and create particles
        fill( lattice, lattice.components.getFirst(), new ArrayList<Component>(), new ImmutableVector2D() );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    //Recursive method to traverse the graph and create particles
    private void fill( SugarLattice lattice, Component component, ArrayList<Component> handled, ImmutableVector2D relativePosition ) {
        final double spacing = Units.picometersToMeters( 200 ) * sizeScale;
        latticeConstituents.add( new LatticeConstituent( new SugarMolecule( new ImmutableVector2D(), spacing ), relativePosition ) );
        handled.add( component );
        ArrayList<Bond> bonds = lattice.getBonds( component );
        for ( Bond bond : bonds ) {
            if ( !handled.contains( bond.destination ) ) {
                fill( lattice, bond.destination, handled, relativePosition.plus( getDelta( spacing, bond ).getRotatedInstance( angle ) ) );
            }
        }
    }
}