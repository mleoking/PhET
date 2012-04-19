// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
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

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.createPolar;
import static edu.colorado.phet.fractions.util.immutable.Vector2D.v;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.Direction.*;
import static fj.data.List.*;

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

    //Same grid orientation as for the "tetris-grid.jpg" in the doc directory
    public static Pattern letterLShapedDiagonal( final int cellLength, final int numberOfPairs ) {
        return new Pattern( iterableList( new ArrayList<Shape>() {{
            for ( int i = 0; i < numberOfPairs; i++ ) {
                add( topL( cellLength, i * 2, i * 1 ) );
                add( bottomL( cellLength, i * 2, i * 1 ) );
            }
        }} ) );
    }

    private static Shape topL( final int cellLength, final int x, final int y ) {
        return AffineTransform.getTranslateInstance( x * cellLength, y * cellLength ).createTransformedShape( fromPoints( cellLength, list( v( 0, 0 ), v( 2, 0 ), v( 2, 3 ), v( 1, 3 ), v( 1, 1 ), v( 0, 1 ) ) ) );
    }

    private static Shape bottomL( final int cellLength, final int x, final int y ) {
        return AffineTransform.getTranslateInstance( x * cellLength, y * cellLength ).createTransformedShape( fromPoints( cellLength, list( v( 0, 1 ), v( 1, 1 ), v( 1, 3 ), v( 2, 3 ), v( 2, 4 ), v( 0, 4 ) ) ) );
    }

    public static Pattern tetrisPiece( final int d ) {

        //Use points in a 5x5 grid, trace pieces clockwise.  See "tetris-grid.jpg" in the doc directory
        return new Pattern( list( fromPoints( d / 3.0, list( v( 0, 0 ), v( 3, 0 ), v( 3, 1 ), v( 2, 1 ), v( 2, 2 ), v( 1, 2 ), v( 1, 1 ), v( 0, 1 ) ) ),
                                  fromPoints( d / 3.0, list( v( 3, 0 ), v( 4, 0 ), v( 4, 3 ), v( 3, 3 ), v( 3, 2 ), v( 2, 2 ), v( 2, 1 ), v( 3, 1 ) ) ),
                                  fromPoints( d / 3.0, list( v( 4, 3 ), v( 4, 4 ), v( 1, 4 ), v( 1, 3 ), v( 2, 3 ), v( 2, 2 ), v( 3, 2 ), v( 3, 3 ) ) ),
                                  fromPoints( d / 3.0, list( v( 0, 4 ), v( 0, 1 ), v( 1, 1 ), v( 1, 2 ), v( 2, 2 ), v( 2, 3 ), v( 1, 3 ), v( 1, 4 ) ) )
        ) );
    }

    private static Shape fromPoints( final double length, final List<Vector2D> v ) {
        return new DoubleGeneralPath( v.head().toImmutableVector2D().times( length ) ) {{
            for ( Vector2D vector2D : v.tail() ) {
                lineTo( vector2D.toImmutableVector2D().times( length ) );
            }
            lineTo( v.head().toImmutableVector2D().times( length ) );
        }}.getGeneralPath();
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

    public static class Polygon {

        //http://www.mathsisfun.com/geometry/interior-angles-polygons.html
        public static Pattern create( double diameter, final int numSides ) {
            final double triAngle = Math.PI * 2.0 / numSides;
//            final double eachAngle = ( numSides - 2 ) * Math.PI / numSides;
            final double radius = diameter / 2;
            return new Pattern( range( 0, numSides ).map( new F<Integer, Shape>() {
                @Override public Shape f( final Integer side ) {
                    final double startAngle = Math.PI / 2 - triAngle / 2 + side * triAngle;
                    final double endAngle = Math.PI / 2 + triAngle / 2 + side * triAngle;
                    return new DoubleGeneralPath( ZERO ) {{
                        lineTo( createPolar( radius, startAngle ) );
                        lineTo( createPolar( radius, endAngle ) );
                        lineTo( ZERO );
                    }}.getGeneralPath();
                }
            } ) );
        }
    }

    public static void main( String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            @Override public void run() {
                new PFrame() {{
                    setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
                    setSize( 1024, 768 );
                    getCanvas().getLayer().addChild( new FNode() {{
                        for ( int i = 4; i <= 8; i++ ) {
                            final int finalI = i;
                            addChild( new PatternNode( FilledPattern.sequentialFill( Polygon.create( 50, finalI ), finalI / 2 ), Color.red ) {{translate( 200 + finalI * 60, 200 );}} );
                        }
                        addChild( new PatternNode( FilledPattern.sequentialFill( tetrisPiece( 50 ), 4 ), Color.red ) {{translate( 200, 400 );}} );
                        addChild( new PatternNode( FilledPattern.sequentialFill( letterLShapedDiagonal( 10, 2 ), 4 ), Color.red ) {{translate( 200, 500 );}} );
                    }} );
                }}.setVisible( true );
            }
        } );
    }
}