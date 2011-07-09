package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.sugarandsaltsolutions.common.util.Units;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Ion;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.Ion.SodiumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.lattice.SaltLattice;

/**
 * This lattice for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SaltCrystal extends Particle implements Iterable<LatticeConstituent> {
    private ArrayList<LatticeConstituent> latticeConstituents = new ArrayList<LatticeConstituent>();

    //The time the lattice entered the water, if any
    private Option<Double> underwaterTime = new None<Double>();
    private double sizeScale = 0.5;

    public SaltCrystal( ImmutableVector2D position, SaltLattice lattice ) {
        super( position );

        //Recursive method to traverse the graph and create particles
        fill( lattice, lattice.ions.getFirst(), new ArrayList<Ion>(), new ImmutableVector2D() );

        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
    }

    //Recursive method to traverse the graph and create particles
    private void fill( SaltLattice lattice, Ion ion, ArrayList<Ion> handled, ImmutableVector2D relativePosition ) {
        final double chlorideRadius = Units.picometersToMeters( 181 ) * sizeScale;
        final double sodiumRadius = Units.picometersToMeters( 102 ) * sizeScale;
        final double spacing = chlorideRadius + sodiumRadius;
        if ( ion instanceof SodiumIon ) {
            final SphericalParticle sodium = new SphericalParticle( sodiumRadius, new ImmutableVector2D( 0, 0 ), Color.red );
            latticeConstituents.add( new LatticeConstituent( sodium, relativePosition ) );
        }
        else {
            final SphericalParticle chloride = new SphericalParticle( chlorideRadius, new ImmutableVector2D( 0, 0 ), Color.blue );
            latticeConstituents.add( new LatticeConstituent( chloride, relativePosition ) );
        }
        handled.add( ion );
        ArrayList<Bond> bonds = lattice.getBonds( ion );
        for ( Bond bond : bonds ) {
            ImmutableVector2D delta = null;
            if ( bond.type == BondType.LEFT ) {
                delta = new ImmutableVector2D( -spacing, 0 );
            }
            else if ( bond.type == BondType.RIGHT ) {
                delta = new ImmutableVector2D( spacing, 0 );
            }
            else if ( bond.type == BondType.UP ) {
                delta = new ImmutableVector2D( 0, spacing );
            }
            else if ( bond.type == BondType.DOWN ) {
                delta = new ImmutableVector2D( 0, -spacing );
            }
            if ( !handled.contains( bond.destination ) ) {
                fill( lattice, bond.destination, handled, relativePosition.plus( delta ) );
            }
        }
    }

    public boolean contains( SphericalParticle particle ) {
        for ( LatticeConstituent latticeConstituent : latticeConstituents ) {
            if ( latticeConstituent.particle == particle ) {
                return true;
            }
        }
        return false;
    }

    @Override public void stepInTime( ImmutableVector2D acceleration, double dt ) {
        super.stepInTime( acceleration, dt );
        for ( LatticeConstituent latticeConstituent : latticeConstituents ) {
            latticeConstituent.particle.position.set( position.get().plus( latticeConstituent.location ) );
        }
    }

    //The shape of a lattice is the combined area of its constituents
    @Override public Shape getShape() {
        return new Area() {{
            for ( LatticeConstituent constituent : latticeConstituents ) {
                add( new Area( constituent.particle.getShape() ) );
            }
        }};
    }

    public Iterator<LatticeConstituent> iterator() {
        return latticeConstituents.iterator();
    }

    public boolean isUnderwater() {
        return underwaterTime.isSome();
    }

    public void setUnderwater( double time ) {
        this.underwaterTime = new Some<Double>( time );
    }

    public double getUnderWaterTime() {
        return underwaterTime.get();
    }
}