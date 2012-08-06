// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories;

import fj.F;
import fj.data.List;
import lombok.EqualsAndHashCode;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.fractions.common.util.Dimension2D;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.Slice;

import static edu.colorado.phet.fractions.common.util.FJUtils.ord;

/**
 * Factory pattern for creating circular pies and PieSets.
 *
 * @author Sam Reid
 */
public @EqualsAndHashCode(callSuper = false) class VerticalSliceFactory extends SliceFactory {

    public final double barWidth;
    public final double barHeight;

    //For water glasses, bucket slices should have the full height (since they look like water glasses), so that bounds intersection will
    private final boolean fullBars;

    private final double distanceBetweenBars;
    private double x;

    public final boolean isWaterGlasses;

    //Private, require users to use singleton
    public VerticalSliceFactory( double x, double barWidth, double barHeight, boolean fullBars, Vector2D bucketPosition, Dimension2D bucketSize, Color sliceColor, final double distanceBetweenBars, final boolean waterGlasses ) {
        super( 15.0, bucketPosition, bucketSize, sliceColor );
        this.barWidth = barWidth;
        this.barHeight = barHeight;
        this.fullBars = fullBars;
        this.distanceBetweenBars = distanceBetweenBars;
        this.x = x;
        this.isWaterGlasses = waterGlasses;
    }

    //Returns the shape for the slice
    final F<Slice, Shape> createToShape( final double height ) {
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

    @Override public Slice getDropTarget( final PieSet pieSet, final Slice s ) {
        return isWaterGlasses ? getWaterGlassesDropTarget( pieSet, s ) : super.getDropTarget( pieSet, s );
    }

    private Slice getWaterGlassesDropTarget( final PieSet pieSet, final Slice s ) {
        if ( pieSet.getEmptyCells().length() == 0 ) { return null; }
        final Slice closestCell = getClosestEmptyCell( pieSet, s.getCenter() );
        return closestCell != null && waterGlassShapeOverlaps( s, closestCell ) ? closestCell : null;
    }

    //For water glasses, have to fill from the bottom, even though the problem is already solved a different way in the view
    //It was creating problems for the model.
    Slice getClosestEmptyCell( PieSet pieSet, final Vector2D point ) {
        final List<Slice> sorted = pieSet.getEmptyCells().sort( ord( new F<Slice, Double>() {
            @Override public Double f( final Slice slice ) {
                return Math.abs( slice.getCenter().x - point.x );
            }
        } ) );
        if ( sorted.length() == 0 ) { return null; }
        final Slice closestPrototype = sorted.head();

        //find the cells that are closest horizontally
        List<Slice> closestSlicesHorizontally = pieSet.getEmptyCells().filter( new F<Slice, Boolean>() {
            @Override public Boolean f( final Slice slice ) {
                return Math.abs( slice.position.x - closestPrototype.position.x ) < 1E-4;//allow tolerance for some floating point error in layout
            }
        } );

        //Find the lowest piece (i.e. y value is max)
        return closestSlicesHorizontally.maximum( ord( new F<Slice, Double>() {
            @Override public Double f( final Slice slice ) {
                return slice.getCenter().y;
            }
        } ) );
    }

    boolean waterGlassShapeOverlaps( final Slice dropped, final Slice target ) {

        final Rectangle2D targetShape = target.getShape().getBounds2D();
        final Rectangle2D droppedShape = new Rectangle2D.Double( dropped.getShape().getBounds2D().getX(), targetShape.getY(), dropped.getShape().getBounds2D().getWidth(), targetShape.getHeight() );
        boolean overlapsHorizontally = droppedShape.intersects( targetShape );
        double y = dropped.getShape().getBounds2D().getY();
        return overlapsHorizontally && y < 365;//Determined empirically with a System.out.println statement of y above
    }
}