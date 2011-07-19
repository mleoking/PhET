// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NitrogenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.OxygenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.SodiumIonParticle;

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
            constituents.add( new Constituent( new SodiumIonParticle(), relativePosition ) );
        }
        //Nitrate
        else {
            //Keep track of relative positions so the nitrate group won't dissolve
            addNitrate( new Nitrate( nitrogenOxygenSpacing, angle, relativePosition ), relativePosition );
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

    private void addNitrate( Nitrate nitrate, ImmutableVector2D relativePosition ) {
        nitrates.add( nitrate );
        for ( Constituent constituent : nitrate ) {
            constituents.add( new Constituent( constituent.particle, constituent.location.plus( relativePosition ) ) );
        }
    }

    public ArrayList<Nitrate> getNitrates() {
        return nitrates;
    }
}