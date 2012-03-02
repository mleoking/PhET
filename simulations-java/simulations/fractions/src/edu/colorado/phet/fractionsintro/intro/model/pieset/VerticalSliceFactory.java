// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * Factory pattern for creating circular pies and PieSets.
 *
 * @author Sam Reid
 */
public class VerticalSliceFactory extends SliceFactory {

//    public static final VerticalSliceFactory VerticalSliceFactory = new VerticalSliceFactory( 125, 225, false );
//
//    //Water glasses are a bit smaller to match up with the graphics exactly
//    public static final VerticalSliceFactory WaterGlassSetFactory = new VerticalSliceFactory( 100, 200, true );

    public final double barWidth;
    public final double barHeight;

    //For water glasses, bucket slices should have the full height (since they look like water glasses), so that bounds intersection will
    private final boolean fullBars;

    private final double distanceBetweenBars;
    private double x;

    //Private, require users to use singleton
    public VerticalSliceFactory( double x, double barWidth, double barHeight, boolean fullBars, Vector2D bucketPosition, Color sliceColor, final double distanceBetweenBars ) {
        super( 15.0, bucketPosition, sliceColor );
        this.barWidth = barWidth;
        this.barHeight = barHeight;
        this.fullBars = fullBars;
        this.distanceBetweenBars = distanceBetweenBars;
        this.x = x;
    }

    //Returns the shape for the slice
    public final Function1<Slice, Shape> createToShape( final double height ) {
        return new Function1<Slice, Shape>() {
            @Override public Shape apply( Slice slice ) {
                return new Rectangle2D.Double( slice.position.getX() - barWidth / 2, slice.position.getY() - height / 2, barWidth, height );
            }
        };
    }

    //Put the pieces right in the center of the bucket hole.
    public Slice createBucketSlice( int denominator ) {
        final Slice cell = createPieCell( 6, 0, 0, denominator, fullBars ? barHeight : barHeight / denominator );
        final Shape shape = cell.shape();
        double leftEdgeBucketHole = getBucketCenter().getX() - bucket.getHoleShape().getBounds2D().getWidth() / 2 + shape.getBounds2D().getWidth() / 2 + 20;
        double rightEdgeBucketHole = getBucketCenter().getX() + bucket.getHoleShape().getBounds2D().getWidth() / 2 - shape.getBounds2D().getWidth() / 2 - 20;
        double desiredCenter = random.nextDouble() * ( rightEdgeBucketHole - leftEdgeBucketHole ) + leftEdgeBucketHole;

        //Stagger vertically in the bucket to make them more distinguishable
        return cell.tip( new Vector2D( desiredCenter, getBucketCenter().getY() + random.nextDouble() * yRange ) );
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
        return new Slice( new Vector2D( x + barX + offset, 265 + cellHeight * ( denominator - cell ) + linearFunction.evaluate( denominator ) ), 0, false, null, createToShape( cellHeight ), sliceColor );
    }
}