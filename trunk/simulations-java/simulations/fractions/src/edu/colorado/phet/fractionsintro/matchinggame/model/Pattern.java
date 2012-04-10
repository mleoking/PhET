// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.data.List;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D.UnitVector2D;
import edu.colorado.phet.fractions.view.FNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FilledPattern;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.umd.cs.piccolox.PFrame;

import static edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Direction.*;
import static fj.data.List.iterableList;
import static fj.data.List.list;

/**
 * An abstraction over shape-based representations for fractions, such as a grid of cells, or a pyramid of triangles.
 *
 * @author Sam Reid
 */
public class Pattern {
    public final List<Shape> shapes;

    public Pattern( List<Shape> shapes ) {
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

    public static Shape plusSign( double gridX, double gridY, final double edgeLength ) {
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

    //Equilateral triangle
    public static Shape triangle( final double length, final Vector2D tip, final UnitVector2D direction ) {
        return new DoubleGeneralPath() {{
            moveTo( tip.toPoint2D() );
            lineTo( tip.plus( direction.rotate( Math.toRadians( 90 + 60 ) ).times( length ) ).toPoint2D() );
            lineTo( tip.plus( direction.rotate( Math.toRadians( -90 - 60 ) ).times( length ) ).toPoint2D() );
            lineTo( tip.toPoint2D() );
        }}.getGeneralPath();
    }

    public static class Pyramid {
        public static final UnitVector2D UP = new UnitVector2D( 0, -1 );
        public static final UnitVector2D DOWN = new UnitVector2D( 0, 1 );

        //The height of an equilateral triangle with side length given
        private static double getHeight( final double length ) {return Math.sqrt( 3 ) / 2.0 * length;}

        public static Pattern single( double length ) {
            return new Pattern( List.single( triangle( length, new Vector2D( 0, 0 ), UP ) ) );
        }

        public static Pattern four( double length ) {
            final double h = getHeight( length );
            return new Pattern( single( length ).shapes.append( list( triangle( length, new Vector2D( -length / 2, h ), UP ),
                                                                      triangle( length, new Vector2D( 0, h * 2 ), DOWN ),
                                                                      triangle( length, new Vector2D( length / 2, h ), UP ) ) ) );
        }

        public static Pattern nine( double length ) {
            final double h = getHeight( length );
            return new Pattern( four( length ).shapes.append( list( triangle( length, new Vector2D( -length, h * 2 ), UP ),
                                                                    triangle( length, new Vector2D( -length / 2, h * 3 ), DOWN ),
                                                                    triangle( length, new Vector2D( 0, h * 2 ), UP ),
                                                                    triangle( length, new Vector2D( length / 2, h * 3 ), DOWN ),
                                                                    triangle( length, new Vector2D( length, h * 2 ), UP ) ) ) );
        }
    }

    public static void main( String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            @Override public void run() {
                new PFrame() {{
                    setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
                    setSize( 1024, 768 );
                    getCanvas().getLayer().addChild( new FNode() {{
                        addChild( new PatternNode( FilledPattern.sequentialFill( Pyramid.nine( 100 ), 8 ), Color.red ) {{translate( 200, 0 );}} );
                    }} );
                }}.setVisible( true );
            }
        } );
    }
}