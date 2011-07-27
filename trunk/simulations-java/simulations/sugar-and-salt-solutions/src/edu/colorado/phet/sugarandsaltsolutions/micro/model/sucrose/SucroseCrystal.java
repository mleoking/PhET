package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.nanometersToMeters;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel.sizeScale;
import static java.lang.Math.PI;
import static java.lang.Math.random;

/**
 * This crystal for sugar updates the positions of the molecules to ensure they move together
 *
 * @author Sam Reid
 */
public class SucroseCrystal extends Crystal<SucroseMolecule> {

    public SucroseCrystal( ImmutableVector2D position, SucroseLattice lattice ) {
        super( position );

        //Recursive method to traverse the graph and create particles
        fill( lattice, lattice.components.getFirst(), new ArrayList<Component>(), new ImmutableVector2D() );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    //Recursive method to traverse the graph and create particles
    private void fill( SucroseLattice lattice, Component component, ArrayList<Component> handled, ImmutableVector2D relativePosition ) {

        //Sugar size is actually about 1 nm, but we need to make them closer together or the sucrose lattices look disjoint
        //Also, scale everything by the model sizeScale, including distances between atoms
        final double spacing = nanometersToMeters( 0.5 ) * sizeScale / 2;

        //Create and add sucrose molecules in the right relative locations
        constituents.add( new Constituent<SucroseMolecule>( new SucroseMolecule( relativePosition ), relativePosition ) );

        handled.add( component );
        ArrayList<Bond> bonds = lattice.getBonds( component );
        for ( Bond bond : bonds ) {
            if ( !handled.contains( bond.destination ) ) {
                fill( lattice, bond.destination, handled, relativePosition.plus( getDelta( spacing, bond ).getRotatedInstance( angle ) ) );
            }
        }
    }

    //Breaks apart the sucrose crystal, but keeps sucrose as molecules
    public ArrayList<? extends Particle> dissolve() {
        ArrayList<Particle> freeParticles = new ArrayList<Particle>();
        for ( final Constituent<SucroseMolecule> constituent : constituents ) {
            SucroseMolecule sucroseMolecule = constituent.particle;
            sucroseMolecule.position.set( position.get() );
            sucroseMolecule.velocity.set( velocity.get().getRotatedInstance( random() * PI * 2 ) );
            freeParticles.add( sucroseMolecule );
        }
        return freeParticles;
    }

    //TODO: implement dissolving single molecule
}