// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Factory pattern for creating circular pies and PieSets.
 *
 * @author Sam Reid
 */
public class CircularSliceFactory extends AbstractSliceFactory {

    public static final CircularSliceFactory CircularSliceFactory = new CircularSliceFactory();

    public final double diameter = 155;
    public final double radius = diameter / 2;
    public final double spacing = 10;

    //Private, require users to use singleton
    private CircularSliceFactory() {}

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public final Function1<Slice, Shape> getShapeFunction( final double extent ) {
        return new Function1<Slice, Shape>() {
            @Override public Shape apply( Slice slice ) {
                double epsilon = 1E-6;
                ImmutableVector2D tip = slice.position;
                double angle = slice.angle;
                return extent >= Math.PI * 2 - epsilon ?
                       new Ellipse2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2 ) :
                       new Arc2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2, angle * 180.0 / Math.PI, extent * 180.0 / Math.PI, Arc2D.PIE );
            }
        };
    }

    //Put the pieces right in the center of the bucket hole.
    //They are pointing up so that when they rotate to align with the closest targets (the bottom ones) they don't have far to rotate, since the bottom targets are also pointing up
    public Slice createBucketSlice( int denominator ) {
        final double anglePerSlice = 2 * Math.PI / denominator;
        final ImmutableVector2D location = new ImmutableVector2D( getBucketCenter().getX() + ( random.nextDouble() * 2 - 1 ) * radius, getBucketCenter().getY() - radius / 2 );
        return new Slice( location, 3 * Math.PI / 2 - anglePerSlice / 2, false, null, getShapeFunction( anglePerSlice ) );
    }

    public Slice createPieCell( int maxPies, int pie, int cell, int denominator ) {
        //Center
        double offset = new LinearFunction( 1, 6, diameter * 3 - diameter / 3, 0 ).evaluate( maxPies );

        final double anglePerSlice = 2 * Math.PI / denominator;
        final ImmutableVector2D location = new ImmutableVector2D( diameter * ( pie + 1 ) + spacing * ( pie + 1 ) - 80 + offset, 250 );
        return new Slice( location, anglePerSlice * cell, false, null, getShapeFunction( anglePerSlice ) );
    }
}