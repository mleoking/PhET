// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset.factories;

import fj.F;
import fj.Ordering;
import lombok.Data;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.fractions.util.immutable.Dimension2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;

import static fj.Ord.ord;

/**
 * Factory pattern for creating pieces of cakes.  Even though cake slices are images, use an underlying shape representation to efficiently re-use code for other representations.
 *
 * @author Sam Reid
 */
public @Data class CakeSliceFactory extends SliceFactory {

    public static final double CAKE_SIZE_SCALE = 0.93;
    public final double diameter = 155 * CAKE_SIZE_SCALE;
    public final double radius = diameter / 2;
    public final double spacing = 22;

    //Private, require users to use singleton
    public CakeSliceFactory( Vector2D bucketPosition, Dimension2D bucketSize ) {super( 0.0, bucketPosition, bucketSize, null ); }

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public final F<Slice, Shape> getShapeFunction( final double extent ) {
        return new CakeSliceFunction( extent, radius, spacing );
    }

    //Put the pieces right in the center of the bucket hole.
    //They are pointing up so that when they rotate to align with the closest targets (the bottom ones) they don't have far to rotate, since the bottom targets are also pointing up
    public Slice createBucketSlice( int denominator, long randomSeed ) {
        Random random = new Random( randomSeed );
        final double anglePerSlice = 2 * Math.PI / denominator;
        final Vector2D location = new Vector2D( getBucketCenter().x + ( random.nextDouble() * 2 - 1 ) * radius, getBucketCenter().y + radius / 4 );
        return new Slice( location, 3 * Math.PI / 2 - anglePerSlice / 2, false, null, getShapeFunction( anglePerSlice ), null );
    }

    public Slice createPieCell( int maxPies, int pie, int cell, int denominator ) {
        //Center the pie cell
        double offset = new LinearFunction( 1, 6, diameter * 3 - diameter / 3, 0 ).evaluate( maxPies );

        final double anglePerSlice = 2 * Math.PI / denominator;
        final Vector2D location = new Vector2D( diameter * ( pie + 1 ) + spacing * ( pie + 1 ) - 80 + offset, 300 );
        return new Slice( location, anglePerSlice * cell, false, null, getShapeFunction( anglePerSlice ), null );
    }

    //Find which should appear before/after others in z-ordering.  Must be back to front.
    public static final HashMap<Integer, int[]> _renderOrder = new HashMap<Integer, int[]>() {{
        put( 1, new int[] { 1 } );
        put( 2, new int[] { 2, 1 } );
        put( 3, new int[] { 1, 2, 3 } );
        put( 4, new int[] { 1, 2, 3, 4 } );
        put( 5, new int[] { 2, 1, 3, 5, 4 } );
        put( 6, new int[] { 2, 1, 3, 6, 4, 5 } );
        put( 7, new int[] { 2, 3, 1, 4, 7, 5, 6 } );
        put( 8, new int[] { 2, 3, 1, 4, 5, 8, 6, 7 } );
    }};

    //Fix the z-ordering for cake slices.  This is done in the model since it applies to any possible view
    public static PieSet sort( final PieSet p ) {
        final int[] renderOrder = _renderOrder.get( p.denominator );
        final ArrayList<Integer> list = new ArrayList<Integer>() {{
            for ( int e : renderOrder ) {
                add( e );
            }
        }};
        return p.slices( p.slices.sort( ord( new F<Slice, F<Slice, Ordering>>() {
            @Override public F<Slice, Ordering> f( final Slice a ) {
                return new F<Slice, Ordering>() {
                    @Override public Ordering f( Slice b ) {
                        int cellA = list.indexOf( a.cell( p.denominator ) + 1 );
                        int cellB = list.indexOf( b.cell( p.denominator ) + 1 );
                        return cellA > cellB ? Ordering.GT :
                               cellA < cellB ? Ordering.LT :
                               //Use any rule that differentiates the slices so the bucket slices don't jump around because of all being equal
                               a.hashCode() > b.hashCode() ? Ordering.GT :
                               a.hashCode() < b.hashCode() ? Ordering.LT :
                               Ordering.EQ;
                    }
                };
            }
        } ) ) );
    }
}