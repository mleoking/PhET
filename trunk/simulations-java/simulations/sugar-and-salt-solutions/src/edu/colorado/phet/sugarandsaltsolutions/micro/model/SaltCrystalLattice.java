package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;

/**
 * This lattice for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SaltCrystalLattice extends Particle implements Iterable<LatticeConstituent> {
    private ArrayList<LatticeConstituent> latticeConstituents = new ArrayList<LatticeConstituent>();

    //The time the lattice entered the water, if any
    private Option<Double> underwaterTime = new None<Double>();

//    public static SaltCrystalLattice grow( ImmutableVector2D position, int numConstituents ) {
//        return grow( new SaltCrystalLattice( position ), numConstituents );
//    }

//    public static SaltCrystalLattice grow( SaltCrystalLattice lattice, int numConstituents ) {
//        if ( lattice.latticeConstituents.size() >= numConstituents ) {
//            return lattice;
//        }
//        else {
//            //Scale the ions down from their actual van der waals radii so they don't take up too much space on the screen
//            double sizeScale = 0.5;
//
//            final double sodiumRadius = picometersToMeters( 102 ) * sizeScale;
//            final SphericalParticle sodium = new SphericalParticle( sodiumRadius, new ImmutableVector2D( 0, 0 ), Color.red );
//            //new SaltCrystalLattice( position, new LatticeConstituent( sodium, 0, 0 ) );
//
//            //Position overwritten by lattice
//
//            //Create the ions
//            //Colors and sizes from the table in https://docs.google.com/document/d/1h6aWxvNMhXUNJiTa2GCMRUqvzUNoiGxN_JEXYjam8Kc/edit?hl=en#
//
//
////        sodiumList.add( sodium );
////        final double chlorideRadius = picometersToMeters( 181 ) * sizeScale;
////        final SphericalParticle chloride = new SphericalParticle( chlorideRadius, zero, Color.blue );
////        chlorideList.add( chloride );
//
//            return new
////        new LatticeConstituent( sodium, 0, 0 ),
////                new LatticeConstituent( chloride, 0, sodium.radius + chlorideRadius )));
//        }
//    }

    public SaltCrystalLattice( ImmutableVector2D position, LatticeConstituent... constituents ) {
        super( position );
        for ( LatticeConstituent constituent : constituents ) {
            latticeConstituents.add( constituent );
        }
        //Update positions so the lattice position overwrites constituent particle positions
        stepInTime( new ImmutableVector2D(), 0.0 );
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

    public void setUnderWater( double time ) {
        this.underwaterTime = new Some<Double>( time );
    }

    public double getUnderWaterTime() {
        return underwaterTime.get();
    }
}