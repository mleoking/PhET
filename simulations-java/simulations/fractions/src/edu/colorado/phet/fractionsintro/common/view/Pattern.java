// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.common.view;

import fj.data.List;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

import static edu.colorado.phet.fractionsintro.common.view.Pattern.Direction.*;
import static fj.data.List.iterableList;

/**
 * @author Sam Reid
 */
public class Pattern {
    public final List<? extends Shape> shapes;

    public Pattern( List<? extends Shape> shapes ) {
        this.shapes = shapes;
    }

    public static Rectangle2D.Double square( double x, double y, double length ) {
        return new Rectangle2D.Double( x, y, length, length );
    }

    public static class Direction {
        public static Direction RIGHT = new Direction( 1, 0 );
        public static Direction LEFT = new Direction( -1, 0 );
        public static Direction DOWN = new Direction( 0, 1 );
        public static Direction UP = new Direction( 0, -1 );
        public final Vector2D vector;

        public Direction( double x, double y ) {
            this.vector = new Vector2D( x, y );
        }
    }

    public static GeneralPath plusSign( double gridX, double gridY, final double edgeLength ) {
        return new DoubleGeneralPath( gridX * edgeLength + edgeLength, gridY * edgeLength ) {
            {
                move( RIGHT, DOWN, RIGHT, DOWN, LEFT, DOWN, LEFT, UP, LEFT, UP, RIGHT, UP );
            }

            private void move( Direction... dir ) {
                for ( Direction direction : dir ) {
                    lineToRelative( direction.vector.times( edgeLength ).toImmutableVector2D() );
                }
            }
        }.getGeneralPath();
    }

    public static class NineGrid extends Pattern {
        public NineGrid() {
            super( iterableList( new ArrayList<Shape>() {{
                double length = 20;
                for ( int i = 0; i < 3; i++ ) {
                    for ( int j = 0; j < 3; j++ ) {
                        add( square( i * length, j * length, length ) );
                    }
                }
            }} ) );
        }
    }

    public static class SixPlusSigns extends Pattern {
        public SixPlusSigns() {
            super( iterableList( new ArrayList<Shape>() {{
                final double edgeLength = 20 / 2;
                add( plusSign( 1, 0, edgeLength ) );
                add( plusSign( 3, 1, edgeLength ) );
                add( plusSign( 5, 2, edgeLength ) );
                add( plusSign( 0, 2, edgeLength ) );
                add( plusSign( 2, 3, edgeLength ) );
                add( plusSign( 4, 4, edgeLength ) );
            }} ) );
        }
    }
}