// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.common.view;

import fj.data.List;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

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

    public static GeneralPath plusSign( double gridX, double gridY, final double edgeLength ) {
        return new DoubleGeneralPath( gridX * edgeLength + edgeLength, gridY * edgeLength ) {
            {
                right();
                down();
                right();
                down();
                left();
                down();
                left();
                up();
                left();
                up();
                right();
                up();
            }

            private void right() {
                lineToRelative( edgeLength, 0 );
            }

            private void left() {
                lineToRelative( -edgeLength, 0 );
            }

            private void up() {
                lineToRelative( 0, -edgeLength );
            }

            private void down() {
                lineToRelative( 0, edgeLength );
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