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
public @Data class VerticalSliceFactory extends SliceFactory {

    public final double barWidth;
    public final double barHeight;

    //For water glasses, bucket slices should have the full height (since they look like water glasses), so that bounds intersection will
    private final boolean fullBars;

    private final double distanceBetweenBars;
    private double x;

    //Private, require users to use singleton
    public VerticalSliceFactory( double x, double barWidth, double barHeight, boolean fullBars, Vector2D bucketPosition, Dimension2D bucketSize, Color sliceColor, final double distanceBetweenBars ) {
        super( 15.0, bucketPosition, bucketSize, sliceColor );
        this.barWidth = barWidth;
        this.barHeight = barHeight;
        this.fullBars = fullBars;
        this.distanceBetweenBars = distanceBetweenBars;
        this.x = x;
    }

    //Returns the shape for the slice
    public final F<Slice, Shape> createToShape( final double height ) {
        return new VerticalShape( height, barWidth );
    }

    //Put the pieces right in the center of the bucket hole.
    public Slice createBucketSlice( int denominator, long randomSeed ) {
        Random random = new Random( randomSeed );
        final Slice cell = createPieCell( 6, 0, 0, denominator, fullBars ? barHeight : barHeight / denominator );
        final Shape shape = cell.getShape();
        double leftEdgeBucketHole = getBucketCenter().getX() - bucket.getHoleShape().getBounds2D().getWidth() / 2 + shape.getBounds2D().getWidth() / 2 + 20;
        double rightEdgeBucketHole = getBucketCenter().getX() + bucket.getHoleShape().getBounds2D().getWidth() / 2 - shape.getBounds2D().getWidth() / 2 - 20;
        double desiredCenter = random.nextDouble() * ( rightEdgeBucketHole - leftEdgeBucketHole ) + leftEdgeBucketHole;

        //Stagger vertically in the bucket to make them more distinguishable
        return cell.withPosition( new Vector2D( desiredCenter, getBucketCenter().getY() + random.nextDouble() * yRange ) );
    }

    public Slice createPieCell( int numPies, int pie, int cell, int denominator ) {
        return createPieCell( numPies, pie, cell, denominator, barHeight / denominator );
    }

    private Slice createPieCell( int numPies, int pie, int cell, int denominator, double cellHeight ) {
        double offset = new LinearFunction( 1, 6, barWidth * 3 - barWidth / 3, 0 ).evaluate( numPies );
        double delta = barWidth + distanceBetweenBars;
        final double barX = delta + pie * delta;

        //Account for offset, determined empirically: den=1 => offset = 0, den = 2 => offset = -cellHeight/2
        LinearFunction linearFunction = new LinearFunction( 1, 2, -barHeight, -barHeight + cellHeight / 2 );
        return new Slice( new Vector2D( x + barX + offset, 282 + cellHeight * ( denominator - cell ) + linearFunction.evaluate( denominator ) ), 0, false, null, createToShape( cellHeight ), sliceColor );
    }
}