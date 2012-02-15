// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.view.FractionsIntroCanvas;

/**
 * Factory pattern for creating circular pies and PieSets.
 *
 * @author Sam Reid
 */
public class HorizontalSliceFactory extends AbstractSliceFactory {

    public static final HorizontalSliceFactory HorizontalSliceFactory = new HorizontalSliceFactory();

    //Private, require users to use singleton
    private HorizontalSliceFactory() {}

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public final Function1<Slice, Shape> toShape = new Function1<Slice, Shape>() {
        @Override public Shape apply( Slice slice ) {
            ImmutableVector2D tip = slice.tip;
            double radius = slice.radius;
            return new Rectangle2D.Double( tip.getX() - radius, tip.getY() - barHeight / 2, radius * 2, barHeight );
        }
    };

    //Put the pieces right in the center of the bucket hole.
    public Slice createBucketSlice( int denominator ) {
        final Slice cell = createPieCell( 0, 0, denominator );
        final Shape shape = cell.shape();
        double leftEdgeBucketHole = getBucketCenter().getX() - bucket.getHoleShape().getBounds2D().getWidth() / 2 + shape.getBounds2D().getWidth() / 2 + 20;
        double rightEdgeBucketHole = getBucketCenter().getX() + bucket.getHoleShape().getBounds2D().getWidth() / 2 - shape.getBounds2D().getWidth() / 2 - 20;
        double desiredCenter = random.nextDouble() * ( rightEdgeBucketHole - leftEdgeBucketHole ) + leftEdgeBucketHole;
        return cell.tip( new ImmutableVector2D( desiredCenter, getBucketCenter().getY() ) );
    }

    private static final int NUM_BARS_PER_LINE = 3;
    public final double barHeight = 50;

    //Find how much space we can use for 3 bars horizontally
    //TODO: Use spaceForBars field
    private final double spaceForBars = FractionsIntroCanvas.WIDTH_FOR_REPRESENTATION / 2 - AbstractFractionsCanvas.INSET * 2;
    private final int distanceBetweenBars = 20;
    private final double oneBarWidth = 310;

    public Slice createPieCell( int pie, int cell, int denominator ) {

        int row = pie / NUM_BARS_PER_LINE;
        int column = pie % NUM_BARS_PER_LINE;

//        double leftBarX = AbstractFractionsCanvas.INSET + oneBarWidth / 2;
        double barPlusSpace = oneBarWidth + distanceBetweenBars;
        final double cellWidth = oneBarWidth / denominator;

        double rowHeight = barHeight + distanceBetweenBars;

        final ImmutableVector2D center = new ImmutableVector2D( oneBarWidth / 2 + column * barPlusSpace + distanceBetweenBars, 250 + row * rowHeight );

        final double barX = center.getX() - oneBarWidth / 2;
        final double distanceInBar = cellWidth * cell;
        final double cellCenterX = barX + distanceInBar + cellWidth / 2;

//        System.out.println( "pie = " + pie + ", cell = " + cell + ", row=" + row + ", column = " + column + ", position = " + center );
        return new Slice( new ImmutableVector2D( cellCenterX, center.getY() ), Math.PI, Math.PI, oneBarWidth / 2 / denominator, false, null, toShape );
    }
}