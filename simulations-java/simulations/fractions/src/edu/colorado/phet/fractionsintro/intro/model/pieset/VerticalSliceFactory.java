// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Factory pattern for creating circular pies and PieSets.
 *
 * @author Sam Reid
 */
public class VerticalSliceFactory extends AbstractSliceFactory {

    public static final VerticalSliceFactory VerticalSliceFactory = new VerticalSliceFactory();

    public final double barWidth = 125;

    //Private, require users to use singleton
    private VerticalSliceFactory() {}

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public final Function1<Slice, Shape> createToShape( final double height ) {
        return new Function1<Slice, Shape>() {
            @Override public Shape apply( Slice slice ) {
                return new Rectangle2D.Double( slice.position.getX() - barWidth / 2, slice.position.getY() - height / 2, barWidth, height );
            }
        };
    }

    //Put the pieces right in the center of the bucket hole.
    public Slice createBucketSlice( int denominator ) {
        final Slice cell = createPieCell( 6, 0, 0, denominator );
        final Shape shape = cell.shape();
        double leftEdgeBucketHole = getBucketCenter().getX() - bucket.getHoleShape().getBounds2D().getWidth() / 2 + shape.getBounds2D().getWidth() / 2 + 20;
        double rightEdgeBucketHole = getBucketCenter().getX() + bucket.getHoleShape().getBounds2D().getWidth() / 2 - shape.getBounds2D().getWidth() / 2 - 20;
        double desiredCenter = random.nextDouble() * ( rightEdgeBucketHole - leftEdgeBucketHole ) + leftEdgeBucketHole;
        return cell.tip( new ImmutableVector2D( desiredCenter, getBucketCenter().getY() ) );
    }

    public Slice createPieCell( int numPies, int pie, int cell, int denominator ) {
        double offset = new LinearFunction( 1, 6, barWidth * 3 - barWidth / 3, 0 ).evaluate( numPies );
        double barHeight = 225;
        final double cellHeight = barHeight / denominator;
        int distanceBetweenBars = 20;
        double delta = barWidth + distanceBetweenBars;
        final double barX = delta + pie * delta;

        //Account for offset, determined empirically: den=1 => offset = 0, den = 2 => offset = -cellHeight/2
        LinearFunction linearFunction = new LinearFunction( 1, 2, -barHeight, -barHeight + cellHeight / 2 );
        return new Slice( new ImmutableVector2D( barX + offset, 265 + cellHeight * ( denominator - cell ) + linearFunction.evaluate( denominator ) ), 0, false, null, createToShape( cellHeight ) );
    }
}