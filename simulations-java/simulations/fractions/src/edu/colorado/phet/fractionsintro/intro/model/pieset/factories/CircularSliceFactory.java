// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset.factories;

import fj.F;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.fractions.util.immutable.Dimension2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Site;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;

/**
 * Factory pattern for creating circular pies and PieSets.
 *
 * @author Sam Reid
 */
public class CircularSliceFactory extends SliceFactory {

    public final double diameter;
    private final double x;
    private final double y;
    private final F<Site, Site> siteMap;
    public final double spacing = 10;
    private final int piesPerRow;
    private final double radius;

    //Private, require users to use singleton
    public CircularSliceFactory( int piesPerRow, Vector2D bucketPosition, Dimension2D bucketSize, double diameter, double x, double y, F<Site, Site> siteMap, Color sliceColor ) {
        super( 0.0, bucketPosition, bucketSize, sliceColor );
        this.piesPerRow = piesPerRow;
        this.diameter = diameter;
        this.x = x;
        this.y = y;
        this.siteMap = siteMap;
        this.radius = diameter / 2;
    }

    //Returns the shape for the slice, but gets rid of the "crack" appearing to the right in full circles by using an ellipse instead.
    public final Function1<Slice, Shape> getShapeFunction( final double extent ) {
        return new Function1<Slice, Shape>() {
            @Override public Shape apply( Slice slice ) {
                double epsilon = 1E-6;
                Vector2D tip = slice.position;
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
        final Vector2D location = new Vector2D( getBucketCenter().getX() + ( random.nextDouble() * 2 - 1 ) * radius, getBucketCenter().getY() - radius / 2 );
        return new Slice( location, 3 * Math.PI / 2 - anglePerSlice / 2, false, null, getShapeFunction( anglePerSlice ), sliceColor );
    }

    public Slice createPieCell( int maxPies, int pie, int cell, int denominator ) {
        //Center
        double offset = new LinearFunction( 1, 6, diameter * 3 - diameter / 3, 0 ).evaluate( maxPies );

        //See if there are just supposed to be 2 rows, if so, they layout is different
        Site site = siteMap.f( new Site( pie / piesPerRow, pie % piesPerRow ) );

        double spaceBetweenRows = 15;

        final double anglePerSlice = 2 * Math.PI / denominator;
        final Vector2D location = new Vector2D( x + diameter * ( site.column + 1 ) + spacing * ( site.column + 1 ) - 80 + offset, y + radius * 2 * site.row + spaceBetweenRows * site.row );
        return new Slice( location, anglePerSlice * cell, false, null, getShapeFunction( anglePerSlice ), sliceColor );
    }
}