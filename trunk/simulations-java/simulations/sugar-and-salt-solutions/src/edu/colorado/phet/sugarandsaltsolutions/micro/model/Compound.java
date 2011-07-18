// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;

/**
 * A compound represents 0 or more (usually 1 or more) constituents which can be put into solution.  It may be constructed from a lattice.
 *
 * @author Sam Reid
 */
public class Compound extends Particle implements Iterable<LatticeConstituent> {
    protected ArrayList<LatticeConstituent> latticeConstituents = new ArrayList<LatticeConstituent>();
    //The time the lattice entered the water, if any
    private Option<Double> underwaterTime = new None<Double>();
    //Put the vectors at the same angle so all crystals don't come out at right angles
    protected final double angle = Math.random() * 2 * Math.PI;

    public Compound( ImmutableVector2D position ) {
        super( position );
    }

    //Determine a direction to move based on the bond type
    protected ImmutableVector2D getDelta( double spacing, Bond bond ) {
        if ( bond.type == BondType.LEFT ) { return new ImmutableVector2D( -spacing, 0 ); }
        else if ( bond.type == BondType.RIGHT ) { return new ImmutableVector2D( spacing, 0 ); }
        else if ( bond.type == BondType.UP ) { return new ImmutableVector2D( 0, spacing ); }
        else if ( bond.type == BondType.DOWN ) { return new ImmutableVector2D( 0, -spacing ); }
        else { throw new RuntimeException( "Unknown bond type: " + bond ); }
    }

    public boolean contains( Particle particle ) {
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

    //The shape of a lattice is the combined area of its constituents, using bounding rectangles to improve performance
    @Override public Shape getShape() {
        final Rectangle2D bounds2D = latticeConstituents.get( 0 ).particle.getShape().getBounds2D();
        Rectangle2D rect = new Rectangle2D.Double( bounds2D.getX(), bounds2D.getY(), bounds2D.getWidth(), bounds2D.getHeight() );
        for ( LatticeConstituent latticeConstituent : latticeConstituents ) {
            rect = rect.createUnion( latticeConstituent.particle.getShape().getBounds2D() );
        }
        return rect;
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

    //Count the lattice constituent particles with the specified type, for purposes of computing concentrations
    public int count( Class<?> particleType ) {
        int count = 0;
        for ( LatticeConstituent latticeConstituent : latticeConstituents ) {
            if ( particleType.isInstance( latticeConstituent.particle ) ) {
                count++;
            }
        }
        return count;
    }
}
