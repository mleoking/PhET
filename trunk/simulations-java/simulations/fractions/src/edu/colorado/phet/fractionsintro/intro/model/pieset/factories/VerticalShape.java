// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset.factories;

import fj.F;
import lombok.Data;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;

/**
 * Function that gives shapes for vertical slices. Factored out to enable equality testing for regression tests, so that we can add lombok annotation for equals.
 *
 * @author Sam Reid
 */
public @Data class VerticalShape extends F<Slice, Shape> {
    private final double height;
    private final double barWidth;

    public VerticalShape( final double height, final double barWidth ) {
        this.height = height;
        this.barWidth = barWidth;
    }

    @Override public Shape f( final Slice slice ) {
        return new Rectangle2D.Double( slice.position.getX() - barWidth / 2, slice.position.getY() - height / 2, barWidth, height );
    }

    public static void main( String[] args ) {
        System.out.println( "new VerticalShape( 123,4 ) = " + new VerticalShape( 123, 4 ).equals( new VerticalShape( 123, 4 ) ) );
    }
}