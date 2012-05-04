// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset.factories;

import fj.F;
import lombok.Data;

import java.awt.Color;
import java.awt.Shape;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.fractions.util.immutable.Dimension2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;

/**
 * Factory pattern for creating circular pies and PieSets.
 *
 * @author Sam Reid
 */
public @Data class HorizontalSliceFactory extends SliceFactory {

    public HorizontalSliceFactory( Vector2D bucketPosition, Dimension2D bucketSize, Color sliceColor ) {
        super( 15.0, bucketPosition, bucketSize, sliceColor );
    }

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public final F<Slice, Shape> createToShape( final double width ) {
        return new HorizontalShapeFunction( width, barHeight );
    }

    //Put the pieces right in the center of the bucket hole.
    public Slice createBucketSlice( int denominator, long randomSeed ) {
        Random random = new Random( randomSeed );
        final Slice cell = createPieCell( 6, 0, 0, denominator );
        final Shape shape = cell.getShape();
        double leftEdgeBucketHole = getBucketCenter().getX() - bucket.getHoleShape().getBounds2D().getWidth() / 2 + shape.getBounds2D().getWidth() / 2 + 20;
        double rightEdgeBucketHole = getBucketCenter().getX() + bucket.getHoleShape().getBounds2D().getWidth() / 2 - shape.getBounds2D().getWidth() / 2 - 20;
        double desiredCenter = random.nextDouble() * ( rightEdgeBucketHole - leftEdgeBucketHole ) + leftEdgeBucketHole;

        //Stagger vertically in the bucket to make them more distinguishable
        return cell.withPosition( new Vector2D( desiredCenter, getBucketCenter().getY() + random.nextDouble() * yRange ) );
    }

    private static final int NUM_BARS_PER_LINE = 3;
    public final double barHeight = 50;

    //Find how much space we can use for 3 bars horizontally
    private final int distanceBetweenBars = 20;
    private final double oneBarWidth = 310;

    public Slice createPieCell( int numPies, int pie, int cell, int denominator ) {
        int numColumns = Math.min( numPies, 3 );

        double barPlusSpace = oneBarWidth + distanceBetweenBars;
        double offset = new LinearFunction( 1, 3, barPlusSpace * 1, 0 ).evaluate( numColumns );
        int row = pie / NUM_BARS_PER_LINE;
        int column = pie % NUM_BARS_PER_LINE;

        final double cellWidth = oneBarWidth / denominator;

        double rowHeight = barHeight + distanceBetweenBars;

        final Vector2D center = new Vector2D( oneBarWidth / 2 + column * barPlusSpace + distanceBetweenBars, 250 + row * rowHeight );

        final double barX = center.getX() - oneBarWidth / 2;
        final double distanceInBar = cellWidth * cell;
        final double cellCenterX = barX + distanceInBar + cellWidth / 2;

        return new Slice( new Vector2D( cellCenterX + offset, center.getY() ), Math.PI, false, null, createToShape( oneBarWidth / denominator ), sliceColor );
    }
}