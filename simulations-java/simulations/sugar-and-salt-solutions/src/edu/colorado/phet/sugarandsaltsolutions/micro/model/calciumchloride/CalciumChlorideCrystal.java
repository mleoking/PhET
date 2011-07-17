// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.util.Units;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeConstituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;

import static edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel.sizeScale;

/**
 * This crystal for Calcium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class CalciumChlorideCrystal extends Crystal {
    public CalciumChlorideCrystal( ImmutableVector2D position, CalciumChlorideLattice lattice ) {
        super( position );

        //Recursive method to traverse the graph and create particles
        fill( lattice, lattice.components.getFirst(), new ArrayList<edu.colorado.phet.sugarandsaltsolutions.micro.model.Component>(), new ImmutableVector2D() );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    //Recursive method to traverse the graph and create particles
    private void fill( CalciumChlorideLattice lattice, edu.colorado.phet.sugarandsaltsolutions.micro.model.Component component, ArrayList<edu.colorado.phet.sugarandsaltsolutions.micro.model.Component> handled, ImmutableVector2D relativePosition ) {
        final double chlorideRadius = Units.picometersToMeters( 181 ) * sizeScale;
        final double calciumRadius = Units.picometersToMeters( 100 ) * sizeScale;

        //TODO: fix the spacing
        final double spacing = chlorideRadius + calciumRadius;
        ImmutableVector2D zero = new ImmutableVector2D( 0, 0 );

        //Calcium
        if ( component instanceof edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.CalciumIon ) {
            latticeConstituents.add( new LatticeConstituent( new SphericalParticle( chlorideRadius, zero, Color.red ), relativePosition ) );
        }
        //Chloride
        else {
            latticeConstituents.add( new LatticeConstituent( new SphericalParticle( calciumRadius, zero, Color.blue ), relativePosition ) );
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