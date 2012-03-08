// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.data.List;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

import static edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Direction.*;
import static fj.data.List.iterableList;
import static fj.data.List.list;

/**
 * An abstraction over shape-based representations for fractions, such as a grid of cells, or a pyramid of triangles.
 *
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

    public static class Grid extends Pattern {
        public static final double length = 20;

        public Grid( final int edgeCount ) {
            super( iterableList( new ArrayList<Shape>() {{
                for ( int i = 0; i < edgeCount; i++ ) {
                    for ( int j = 0; j < edgeCount; j++ ) {
                        add( square( i * length, j * length, length ) );
                    }
                }
            }} ) );
        }
    }

    public static class PlusSigns extends Pattern {
        public static final LinearFunction fun = new LinearFunction( 1, 6, 20, 10 );

        public PlusSigns( int numberPlusSigns ) {
            super( list( plusSign( 1, 0, fun.evaluate( numberPlusSigns ) ),
                         plusSign( 0, 2, fun.evaluate( numberPlusSigns ) ),
                         plusSign( 3, 1, fun.evaluate( numberPlusSigns ) ),
                         plusSign( 2, 3, fun.evaluate( numberPlusSigns ) ),
                         plusSign( 5, 2, fun.evaluate( numberPlusSigns ) ),
                         plusSign( 4, 4, fun.evaluate( numberPlusSigns ) )
            ).take( numberPlusSigns ) );
        }
    }
}