// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset.factories;

import fj.F;
import lombok.Data;

import java.awt.Color;
import java.awt.Shape;
import java.util.Random;

import edu.colorado.phet.fractions.util.immutable.Dimension2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;

/**
 * Factory pattern for creating stacked horizontal bars for equality lab.  Copied from HorizontalSliceFactory.  Maybe they could be joined in the future.
 *
 * @author Sam Reid
 */
public @Data class StackedHorizontalSliceFactory extends SliceFactory {

    private final double x;
    private final double y;
    private final boolean backwards;

    public StackedHorizontalSliceFactory( Vector2D bucketPosition, Dimension2D bucketSize, Color sliceColor, double x, double y, final boolean backwards ) {
        super( 15.0, bucketPosition, bucketSize, sliceColor );
        this.x = x;
        this.y = y;
        this.backwards = backwards;
    }

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public final F<Slice, Shape> createToShape( final double width ) {
        return new StackedHorizontalSliceToShape( width, barHeight );
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
        return cell.position( new Vector2D( desiredCenter, getBucketCenter().getY() + random.nextDouble() * yRange ) );
    }

    public final double barHeight = 40;
    public final double distanceBetweenBars = 30;

    private final double oneBarWidth = 200;//Make it fit in the small bucket

    public Slice createPieCell( int numPies, int pie, int cell, int denominator ) {

        if ( backwards ) {
            cell = denominator - cell - 1;
        }
        int row = pie;

        final double cellWidth = oneBarWidth / denominator;

        double rowHeight = barHeight + distanceBetweenBars;

        final Vector2D barCenter = new Vector2D( oneBarWidth / 2, 400 - row * rowHeight );

        final double barX = barCenter.x - oneBarWidth / 2;
        final double distanceInBar = oneBarWidth - cellWidth * cell;
        final double cellCenterX = barX + distanceInBar + cellWidth / 2;// + new LinearFunction( 1, 0, 6, -oneBarWidth ).evaluate( denominator );

        return new Slice( new Vector2D( cellCenterX - cellWidth + x, barCenter.y + y ), Math.PI, false, null, createToShape( oneBarWidth / denominator ), sliceColor );
    }
}