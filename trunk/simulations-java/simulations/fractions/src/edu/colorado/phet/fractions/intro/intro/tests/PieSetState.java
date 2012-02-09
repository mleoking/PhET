// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.simsharing.state.ImmutableList;
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
    public final ImmutableList<Slice> slices;

    public PieSetState( int numerator, int denominator, ImmutableList<Slice> cells, ImmutableList<Slice> slices ) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.cells = cells;
        this.slices = slices;
    }

    public PieSetState stepInTime() {
        final ImmutableList<Slice> slices = this.slices.map( new Function1<Slice, Slice>() {
            public Slice apply( final Slice s ) {
                if ( s.dragging ) {

                    final ArrayList<Slice> list = cells.toArrayList();//TODO: filter out occupied cells
                    Slice closest = Collections.min( list, new Comparator<Slice>() {
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
                    final Slice rotated = s.angle( s.angle + delta / 6 );//Xeno effect

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

    public PieSetState slices( ImmutableList<Slice> slices ) {
        return new PieSetState( numerator, denominator, cells, slices );
    }

    //Make all pieces move to the closest cell
    public PieSetState snapTo() {
        final ImmutableList<Slice> slices = this.slices.map( new Function1<Slice, Slice>() {
            public Slice apply( final Slice s ) {
                final ArrayList<Slice> list = cells.toArrayList();//TODO: filter out occupied cells
                Slice closest = Collections.min( list, new Comparator<Slice>() {
                    public int compare( Slice o1, Slice o2 ) {
                        return Double.compare( o1.center.distance( s.center ), o2.center.distance( s.center ) );
                    }
                } );
                return s.angle( closest.angle ).tip( closest.tip );
            }
        } );
        return slices( slices );
    }
}