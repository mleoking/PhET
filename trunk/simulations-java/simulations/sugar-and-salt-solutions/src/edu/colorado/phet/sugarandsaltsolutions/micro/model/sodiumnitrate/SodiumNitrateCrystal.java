// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeConstituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NitrogenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.OxygenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.SodiumIonParticle;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;

/**
 * This crystal for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SodiumNitrateCrystal extends Crystal {
    private ArrayList<Nitrate> nitrates = new ArrayList<Nitrate>();

    public SodiumNitrateCrystal( ImmutableVector2D position, SodiumNitrateLattice lattice ) {
        super( position );

        //Recursive method to traverse the graph and create particles
        fill( lattice, lattice.components.getFirst(), new ArrayList<edu.colorado.phet.sugarandsaltsolutions.micro.model.Component>(), new ImmutableVector2D() );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    //Recursive method to traverse the graph and create particles at the right locations
    private void fill( SodiumNitrateLattice lattice, Component component, ArrayList<Component> handled, ImmutableVector2D relativePosition ) {

        //The distance between nitrogen and oxygen should be the sum of their radii, but the blue background makes it hard to tell that N and O are bonded.
        //Therefore we bring the outer O's closer to the N so there is some overlap.
        double nitrogenOxygenSpacing = ( new NitrogenIonParticle().radius + new OxygenIonParticle().radius ) * 0.85;

        if ( component instanceof SodiumIon ) {
            latticeConstituents.add( new LatticeConstituent( new SodiumIonParticle(), relativePosition ) );
        }
        //Nitrate
        else {
            SphericalParticle o1 = new OxygenIonParticle();
            ImmutableVector2D o1Position = parseAngleAndMagnitude( nitrogenOxygenSpacing, Math.PI * 2 * 0 / 3.0 + angle );
            latticeConstituents.add( new LatticeConstituent( o1, relativePosition.plus( o1Position ) ) );

            SphericalParticle o2 = new OxygenIonParticle();
            ImmutableVector2D o2Position = parseAngleAndMagnitude( nitrogenOxygenSpacing, Math.PI * 2 * 1 / 3.0 + angle );
            latticeConstituents.add( new LatticeConstituent( o2, relativePosition.plus( o2Position ) ) );

            SphericalParticle o3 = new OxygenIonParticle();
            ImmutableVector2D o3Position = parseAngleAndMagnitude( nitrogenOxygenSpacing, Math.PI * 2 * 2 / 3.0 + angle );
            latticeConstituents.add( new LatticeConstituent( o3, relativePosition.plus( o3Position ) ) );

            SphericalParticle nitrogen = new NitrogenIonParticle();
            latticeConstituents.add( new LatticeConstituent( nitrogen, relativePosition ) );

            //Keep track of relative positions so the nitrate group won't dissolve
            addNitrate( new Nitrate( o1, o2, o3, nitrogen, o1Position, o2Position, o3Position ) );
        }
        handled.add( component );
        ArrayList<Bond> bonds = lattice.getBonds( component );
        final double spacing = new SodiumIonParticle().radius * 2 + nitrogenOxygenSpacing;
        for ( Bond bond : bonds ) {
            if ( !handled.contains( bond.destination ) ) {
                fill( lattice, bond.destination, handled, relativePosition.plus( getDelta( spacing, bond ).getRotatedInstance( angle ) ) );
            }
        }
    }

    private void addNitrate( Nitrate nitrate ) {
        nitrates.add( nitrate );
    }

    public ArrayList<Nitrate> getNitrates() {
        return nitrates;
    }
}