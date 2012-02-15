// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import fj.data.List;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.fractionsintro.intro.model.Container;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;

import static fj.data.List.iterableList;

/**
 * @author Sam Reid
 */
public class CircularPieSet implements SliceFactory {

    //Is that confusing to use the same name for static instance as for class name?
    public static final CircularPieSet CircularPieSet = new CircularPieSet();

    //Private, require users to use singleton
    private CircularPieSet() {}

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public final Function1<Slice, Shape> toShape = new Function1<Slice, Shape>() {
        @Override public Shape apply( Slice slice ) {
            double epsilon = 1E-6;
            ImmutableVector2D tip = slice.tip;
            double radius = slice.radius;
            double angle = slice.angle;
            double extent = slice.extent;
            return extent >= Math.PI * 2 - epsilon ?
                   new Ellipse2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2 ) :
                   new Arc2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2, angle * 180.0 / Math.PI, extent * 180.0 / Math.PI, Arc2D.PIE );
        }
    };
    public final int NUM_PIES = 6;
    public final Dimension2DDouble STAGE_SIZE = new Dimension2DDouble( 1024, 768 );
    public final Color BUCKET_COLOR = new Color( 136, 177, 240 );//A shade that looks good behind the green objects
    public final Bucket BUCKET = new Bucket( STAGE_SIZE.width / 2, -STAGE_SIZE.height + 200, new Dimension2DDouble( 300, 100 ), BUCKET_COLOR, "" );
    public final Random RANDOM = new Random();
    public final double PIE_DIAMETER = 155;
    public final double PIE_RADIUS = PIE_DIAMETER / 2;
    public final double PIE_SPACING = 10;

    @Override public Bucket bucket() {
        return BUCKET;
    }

    //Create some cells for the empty pies
    public List<Pie> createEmptyPies( final int denominator ) {
        ArrayList<Pie> pies = new ArrayList<Pie>() {{
            for ( int i = 0; i < NUM_PIES; i++ ) {
                ArrayList<Slice> cells = new ArrayList<Slice>();
                for ( int k = 0; k < denominator; k++ ) {
                    cells.add( createPieCell( i, k, denominator ) );
                }
                add( new Pie( iterableList( cells ) ) );
            }
        }};

        return iterableList( pies );
    }

    //Put the pieces right in the center of the bucket hole.
    //They are pointing up so that when they rotate to align with the closest targets (the bottom ones) they don't have far to rotate, since the bottom targets are also pointing up
    public Slice createBucketSlice( int denominator ) {
        final double x = BUCKET.getHoleShape().getBounds2D().getCenterX() + BUCKET.getPosition().getX();
        final double y = -BUCKET.getHoleShape().getBounds2D().getCenterY() - BUCKET.getPosition().getY();

        final double anglePerSlice = 2 * Math.PI / denominator;
        return new Slice( new ImmutableVector2D( x + ( RANDOM.nextDouble() * 2 - 1 ) * PIE_RADIUS, y - PIE_RADIUS / 2 ), 3 * Math.PI / 2 - anglePerSlice / 2, anglePerSlice, PIE_RADIUS, false, null, toShape );
    }

    public Slice createPieCell( int pie, int cell, int denominator ) {
        final double anglePerSlice = 2 * Math.PI / denominator;
        return new Slice( new ImmutableVector2D( PIE_DIAMETER * ( pie + 1 ) + PIE_SPACING * ( pie + 1 ) - 80, 250 ), anglePerSlice * cell, anglePerSlice, PIE_DIAMETER / 2, false, null, toShape );
    }

    public PieSet fromContainerSetState( ContainerSet containerSetState ) {
        final List<Pie> emptyPies = createEmptyPies( containerSetState.denominator );
        return new PieSet( containerSetState.denominator, emptyPies, createSlices( emptyPies, containerSetState ), this );
    }

    private List<Slice> createSlices( final List<Pie> emptyPies, final ContainerSet containerSetState ) {
        ArrayList<Slice> all = new ArrayList<Slice>();
        for ( int i = 0; i < containerSetState.containers.length(); i++ ) {
            Container c = containerSetState.containers.index( i );
            for ( Integer cell : c.filledCells ) {
                all.add( emptyPies.index( i ).cells.index( cell ) );
            }
        }
        return iterableList( all ).append( createSlicesForBucket( containerSetState.denominator, containerSetState.getEmptyCells().length() ) );
    }

    //Slices to put in the buckets
    public List<Slice> createSlicesForBucket( final int denominator, final int numSlices ) {
        return iterableList( new ArrayList<Slice>() {{
            for ( int i = 0; i < numSlices; i++ ) {
                add( createBucketSlice( denominator ) );
            }
        }} );
    }
}