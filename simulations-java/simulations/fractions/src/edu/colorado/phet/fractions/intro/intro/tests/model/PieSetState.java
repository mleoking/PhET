// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.tests.model;

import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.util.ImmutableList;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Immutable model representing the entire state at one instant, including the number and location of slices
 *
 * @author Sam Reid
 */
public class PieSetState {
    public final int numerator;
    public final int denominator;

    public final ImmutableList<Slice> cells;
    public final ImmutableList<MovableSlice> slices;

    public PieSetState( int numerator, int denominator, ImmutableList<Slice> cells, ImmutableList<MovableSlice> slices ) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.cells = cells;
        this.slices = slices;
    }

    public PieSetState stepInTime() {
        final ImmutableList<MovableSlice> slices = this.slices.map( new Function1<MovableSlice, MovableSlice>() {
            public MovableSlice apply( final MovableSlice s ) {
                if ( s.dragging ) {

                    Slice closest = cells.minBy( new Comparator<Slice>() {
                        public int compare( Slice o1, Slice o2 ) {
                            return Double.compare( o1.center.distance( s.center ), o2.center.distance( s.center ) );
                        }
                    } );

                    //Account for winding number
                    double closestAngle = closest.angle;
                    if ( Math.abs( closestAngle - s.angle ) > Math.PI ) {
                        if ( closestAngle > s.angle ) { closestAngle -= 2 * Math.PI; }
                        else if ( closestAngle < s.angle ) { closestAngle += 2 * Math.PI; }
                    }
                    double delta = closestAngle - s.angle;
                    final MovableSlice rotated = s.angle( s.angle + delta / 6 );//Xeno effect

                    //Keep the center in the same place
                    return rotated.translate( s.center.minus( rotated.center ) );
                }
                else {
                    return s;
                }
            }
        } );
        return slices( slices );
    }

    public PieSetState slices( ImmutableList<MovableSlice> slices ) {
        return new PieSetState( numerator, denominator, cells, slices );
    }

    //Make all pieces move to the closest cell
    public PieSetState snapTo() {
        return slices( slices.map( new Function1<MovableSlice, MovableSlice>() {
            public MovableSlice apply( final MovableSlice s ) {
                Slice closest = cells.minBy( new Comparator<Slice>() {
                    public int compare( Slice o1, Slice o2 ) {
                        return Double.compare( o1.center.distance( s.center ), o2.center.distance( s.center ) );
                    }
                } );
                return s.angle( closest.angle ).tip( closest.tip ).container( closest );
            }
        } ) );
    }

    public boolean cellFilled( final Slice cell ) {
        return slices.contains( new Function1<MovableSlice, Boolean>() {
            public Boolean apply( MovableSlice m ) {
                return m.container == cell;
            }
        } );
    }
}