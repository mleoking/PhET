// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.common.util.Units;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Component;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Component.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.SodiumNitrateLattice;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;

/**
 * This crystal for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SodiumNitrateCrystal extends Crystal {
    public SodiumNitrateCrystal( ImmutableVector2D position, SodiumNitrateLattice lattice, double sizeScale ) {
        super( position, sizeScale );

        //Recursive method to traverse the graph and create particles
        fill( lattice, lattice.components.getFirst(), new ArrayList<Component>(), new ImmutableVector2D() );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    //Recursive method to traverse the graph and create particles
    private void fill( SodiumNitrateLattice lattice, Component component, ArrayList<Component> handled, ImmutableVector2D relativePosition ) {
        final double nitrogenRadius = Units.picometersToMeters( 75 ) * sizeScale;
        final double oxygenRadius = Units.picometersToMeters( 73 ) * sizeScale;
        final double sodiumRadius = Units.picometersToMeters( 102 ) * sizeScale;

        //TODO: fix the spacing
        final double spacing = nitrogenRadius + sodiumRadius * 2 + oxygenRadius;
        ImmutableVector2D zero = new ImmutableVector2D( 0, 0 );
        if ( component instanceof SodiumIon ) {
            latticeConstituents.add( new LatticeConstituent( new SphericalParticle( sodiumRadius, zero, Color.red ), relativePosition ) );
        }
        //Nitrate
        else {
            double delta = nitrogenRadius + oxygenRadius;
            latticeConstituents.add( new LatticeConstituent( new SphericalParticle( oxygenRadius, zero, Color.gray ), relativePosition.plus( parseAngleAndMagnitude( delta, Math.PI * 2 * 0 / 3.0 ) ) ) );
            latticeConstituents.add( new LatticeConstituent( new SphericalParticle( oxygenRadius, zero, Color.gray ), relativePosition.plus( parseAngleAndMagnitude( delta, Math.PI * 2 * 1 / 3.0 ) ) ) );
            latticeConstituents.add( new LatticeConstituent( new SphericalParticle( oxygenRadius, zero, Color.gray ), relativePosition.plus( parseAngleAndMagnitude( delta, Math.PI * 2 * 2 / 3.0 ) ) ) );

            latticeConstituents.add( new LatticeConstituent( new SphericalParticle( nitrogenRadius, zero, Color.gray ), relativePosition ) );
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