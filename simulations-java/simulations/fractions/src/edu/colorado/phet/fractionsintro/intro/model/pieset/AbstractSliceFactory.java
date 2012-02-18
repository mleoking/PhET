// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.fractionsintro.intro.model.Container;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;

import static fj.data.List.iterableList;

/**
 * Immutable factory class for creating slices.  Abstract class, subclasses provide specific behavior/shapes/layout for that kind of object.
 *
 * @author Sam Reid
 */
public abstract class AbstractSliceFactory {

    public final int numPies = 6;
    public final Dimension2DDouble stageSize = new Dimension2DDouble( 1024, 768 );
    public final Color bucketColor = new Color( 136, 177, 240 );//A shade that looks good behind the green objects
    public final Bucket bucket = new Bucket( stageSize.width / 2, -stageSize.height + 200, new Dimension2DDouble( 350, 100 ), bucketColor, "" );
    public final Random random = new Random();

    //Create some cells for the empty pies
    public List<Pie> createEmptyPies( final int denominator ) {
        ArrayList<Pie> pies = new ArrayList<Pie>() {{
            for ( int i = 0; i < numPies; i++ ) {
                ArrayList<Slice> cells = new ArrayList<Slice>();
                for ( int k = 0; k < denominator; k++ ) {
                    cells.add( createPieCell( i, k, denominator ) );
                }
                add( new Pie( iterableList( cells ) ) );
            }
        }};

        return iterableList( pies );
    }

    public abstract Slice createBucketSlice( int denominator );

    public abstract Slice createPieCell( int container, int cell, int denominator );

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
//        final int numSlicesForBucket = containerSetState.getEmptyCells().length();
        final int numSlicesForBucket = 10;
        return iterableList( all ).append( createSlicesForBucket( containerSetState.denominator, numSlicesForBucket ) );
    }

    //Slices to put in the buckets
    public List<Slice> createSlicesForBucket( final int denominator, final int numSlices ) {
        return iterableList( new ArrayList<Slice>() {{
            for ( int i = 0; i < numSlices; i++ ) {
                add( createBucketSlice( denominator ) );
            }
        }} );
    }

    public ImmutableVector2D getBucketCenter() {
        final double x = bucket.getHoleShape().getBounds2D().getCenterX() + bucket.getPosition().getX();
        final double y = -bucket.getHoleShape().getBounds2D().getCenterY() - bucket.getPosition().getY();
        return new ImmutableVector2D( x, y );
    }
}
