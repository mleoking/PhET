// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.model;

import fj.F;
import fj.data.List;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.fractions.common.util.immutable.Vector2D.UnitVector2D;
import edu.colorado.phet.fractions.fractionmatcher.view.FilledPattern;
import edu.colorado.phet.fractions.fractionmatcher.view.PatternNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.PFrame;

import static edu.colorado.phet.common.phetcommon.math.Vector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.math.Vector2D.createPolar;
import static edu.colorado.phet.fractions.common.util.immutable.Vector2D.v;
import static edu.colorado.phet.fractions.fractionmatcher.model.Pattern.Direction.*;
import static fj.data.List.*;
import static java.lang.Math.*;

/**
 * An abstraction over shape-based representations for fractions, such as a grid of cells, or a pyramid of triangles.
 *
 * @author Sam Reid
 */
public class Pattern {

    //Shape for the outline
    public final Shape outline;

    //Individual shapes for the pieces of the fraction.
    public final List<Shape> shapes;

    //Create one pattern with the given shapes, uses Area composition to identify the outline (only works right for some shapes)
    Pattern( final List<Shape> shapes ) {
        this( new Area() {{
            for ( Shape shape : shapes ) {
                add( new Area( shape ) );
            }
        }}, shapes );
    }

    //Create a pattern with an explicit outline and list of shapes.
    private Pattern( Shape outline, final List<Shape> shapes ) {
        this.outline = outline;
        this.shapes = shapes;
    }

    //A single square
    private static Rectangle2D.Double square( double x, double y, double length ) {
        return new Rectangle2D.Double( x, y, length, length );
    }

    //Interleaved L shape (see sample main for how it looks)
    public static Pattern interleavedLShape( final int squareLength, final int numberPairColumns, final int numberPairRows ) {
        return new Pattern( iterableList( new ArrayList<Shape>() {{
            for ( int i = 0; i < numberPairColumns; i++ ) {
                for ( int j = 0; j < numberPairRows; j++ ) {
                    add( AffineTransform.getTranslateInstance( squareLength * i, squareLength * j ).createTransformedShape( interleavedLShapeLeftSide( squareLength ) ) );
                    add( AffineTransform.getTranslateInstance( squareLength * i, squareLength * j ).createTransformedShape( interleavedLShapeRightSide( squareLength ) ) );
                }
            }
        }} ) );
    }

    //Left part of the "interleaved L shape"
    private static Shape interleavedLShapeLeftSide( final double s ) {
        return pointsToShape( s, list( v( 0, 0 ), v( 1.0 / 3.0, 0 ), v( 1.0 / 3.0, 0.5 ), v( 2.0 / 3.0, 0.5 ), v( 2.0 / 3.0, 1 ), v( 0, 1 ) ) );
    }

    //Right part of the "interleaved L shape"
    private static Shape interleavedLShapeRightSide( final double s ) {
        return pointsToShape( s, list( v( 1, 0 ), v( 1, 1 ), v( 2.0 / 3, 1 ), v( 2.0 / 3.0, 0.5 ), v( 1.0 / 3.0, 0.5 ), v( 1.0 / 3, 0 ) ) );
    }

    //Same grid orientation as for the "tetris-grid.jpg" in the doc directory (see sample main for how it looks)
    public static Pattern letterLShapedDiagonal( final int cellLength, final int numberOfPairs ) {
        return new Pattern( iterableList( new ArrayList<Shape>() {{
            for ( int i = 0; i < numberOfPairs; i++ ) {
                add( letterLShapedDiagonalTopL( cellLength, i * 2, i ) );
                add( letterLShapedDiagonalBottomL( cellLength, i * 2, i ) );
            }
        }} ) );
    }

    private static Shape letterLShapedDiagonalTopL( final int cellLength, final int x, final int y ) {
        return AffineTransform.getTranslateInstance( x * cellLength, y * cellLength ).createTransformedShape( pointsToShape( cellLength, list( v( 0, 0 ), v( 2, 0 ), v( 2, 3 ), v( 1, 3 ), v( 1, 1 ), v( 0, 1 ) ) ) );
    }

    private static Shape letterLShapedDiagonalBottomL( final int cellLength, final int x, final int y ) {
        return AffineTransform.getTranslateInstance( x * cellLength, y * cellLength ).createTransformedShape( pointsToShape( cellLength, list( v( 0, 1 ), v( 1, 1 ), v( 1, 3 ), v( 2, 3 ), v( 2, 4 ), v( 0, 4 ) ) ) );
    }

    public static Pattern tetrisPiece( final int d ) {

        //Use points in a 5x5 grid, trace pieces clockwise.  See "tetris-grid.jpg" in the doc directory
        return new Pattern( list( pointsToShape( d / 3.0, list( v( 0, 0 ), v( 3, 0 ), v( 3, 1 ), v( 2, 1 ), v( 2, 2 ), v( 1, 2 ), v( 1, 1 ), v( 0, 1 ) ) ),
                                  pointsToShape( d / 3.0, list( v( 3, 0 ), v( 4, 0 ), v( 4, 3 ), v( 3, 3 ), v( 3, 2 ), v( 2, 2 ), v( 2, 1 ), v( 3, 1 ) ) ),
                                  pointsToShape( d / 3.0, list( v( 4, 3 ), v( 4, 4 ), v( 1, 4 ), v( 1, 3 ), v( 2, 3 ), v( 2, 2 ), v( 3, 2 ), v( 3, 3 ) ) ),
                                  pointsToShape( d / 3.0, list( v( 0, 4 ), v( 0, 1 ), v( 1, 1 ), v( 1, 2 ), v( 2, 2 ), v( 2, 3 ), v( 1, 3 ), v( 1, 4 ) ) )
        ) );
    }

    private static Shape pointsToShape( final double length, final List<edu.colorado.phet.fractions.common.util.immutable.Vector2D> v ) {
        return new DoubleGeneralPath( v.head().toImmutableVector2D().times( length ) ) {{
            for ( edu.colorado.phet.fractions.common.util.immutable.Vector2D vector2D : v.tail() ) {
                lineTo( vector2D.toImmutableVector2D().times( length ) );
            }
            lineTo( v.head().toImmutableVector2D().times( length ) );
        }}.getGeneralPath();
    }

    //Creates a flower with 6 petals.
    public static Pattern sixFlower() { return sixFlower( 18 ); }

    private static Pattern sixFlower( final Integer length ) {
        final double angle = Math.PI * 2 / 6;
        return new Pattern( range( 0, 6 ).map( new F<Integer, Shape>() {
            @Override public Shape f( final Integer index ) {
                final double x = sin( angle / 2 );
                final double y = cos( angle / 2 );
                return AffineTransform.getRotateInstance( angle * index, 0, 0 ).createTransformedShape( pointsToShape( length, list( v( 0, 0 ), v( x, y ), v( 0, y * 2 ), v( -x, y ) ) ) );
            }
        } ) );
    }

    public static Pattern horizontalBars( int numBars ) {
        final int width = 70;
        final int sliceHeight = width / numBars;
        return new Pattern( range( 0, numBars ).map( new F<Integer, Shape>() {
            @Override public Shape f( final Integer index ) {
                return new Rectangle2D.Double( 0, index * sliceHeight, width, sliceHeight );
            }
        } ).reverse() );
    }

    public static Pattern verticalBars( final int numBars ) {
        final int height = 70;
        final int sliceWidth = height / numBars;
        return new Pattern( range( 0, numBars ).map( new F<Integer, Shape>() {
            @Override public Shape f( final Integer index ) {
                return new Rectangle2D.Double( index * sliceWidth, 0, sliceWidth, height );
            }
        } ) );
    }

    public static Pattern pie( int numSlices ) {
        double degreesPerSlice = 360.0 / numSlices;
        final Rectangle area = new Rectangle( 0, 0, 70, 70 );

        ArrayList<Shape> shapes = new ArrayList<Shape>();
        for ( int i = 0; i < numSlices; i++ ) {
            double startDegrees = i * degreesPerSlice;
            final Arc2D.Double arc = new Arc2D.Double( area.getX(), area.getY(), area.getWidth(), area.getHeight(), startDegrees, degreesPerSlice, Arc2D.Double.PIE );
            final Ellipse2D.Double ellipse = new Ellipse2D.Double( area.getX(), area.getY(), area.getWidth(), area.getHeight() );
            final RectangularShape shape = numSlices <= 1 ? ellipse : arc;
            shapes.add( shape );
        }

        return new Pattern( new Ellipse2D.Double( area.x, area.y, area.width, area.height ), iterableList( shapes ) );
    }

    //Create a polygon with the specified diameter and number of sides
    //http://www.mathsisfun.com/geometry/interior-angles-polygons.html
    public static Pattern polygon( double diameter, final int numSides ) {
        final double triAngle = Math.PI * 2.0 / numSides;
        final double radius = diameter / 2;
        final List<Shape> shapes = range( 0, numSides ).map( new F<Integer, Shape>() {
            @Override public Shape f( final Integer side ) {
                final double startAngle = Math.PI / 2 - triAngle / 2 + side * triAngle;
                final double endAngle = Math.PI / 2 + triAngle / 2 + side * triAngle;
                return new DoubleGeneralPath( ZERO ) {{
                    lineTo( createPolar( radius, startAngle ) );
                    lineTo( createPolar( radius, endAngle ) );
                    lineTo( ZERO );
                }}.getGeneralPath();
            }
        } );

        //Create an explicit outline to avoid creating kinks in the outline stroke
        final Vector2D startPoint = createPolar( radius, Math.PI / 2 - triAngle / 2 );
        DoubleGeneralPath outlinePath = new DoubleGeneralPath( startPoint );
        for ( int i = 0; i < numSides; i++ ) {
            outlinePath.lineTo( createPolar( radius, Math.PI / 2 - triAngle / 2 + i * triAngle ) );
        }
        outlinePath.lineTo( startPoint );
        return new Pattern( outlinePath.getGeneralPath(), shapes );
    }

    public static class Direction {
        public static final Direction RIGHT = new Direction( 1, 0 );
        public static final Direction LEFT = new Direction( -1, 0 );
        public static final Direction DOWN = new Direction( 0, 1 );
        public static final Direction UP = new Direction( 0, -1 );
        public final edu.colorado.phet.fractions.common.util.immutable.Vector2D vector;

        public Direction( double x, double y ) {
            this.vector = new edu.colorado.phet.fractions.common.util.immutable.Vector2D( x, y );
        }
    }

    private static Shape plusSign( double gridX, double gridY, final double edgeLength ) {
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

    public static Grid grid( int edgeCount ) {
        return new Grid( edgeCount );
    }

    private static class Grid extends Pattern {
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

    public static PlusSigns plusSigns( int numberPlusSigns ) {
        return new PlusSigns( numberPlusSigns );
    }

    private static class PlusSigns extends Pattern {
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
    private static Shape triangle( final double length, final edu.colorado.phet.fractions.common.util.immutable.Vector2D tip, final UnitVector2D direction ) {
        return new DoubleGeneralPath() {{
            moveTo( tip.toPoint2D() );
            lineTo( tip.plus( direction.rotate( toRadians( 90 + 60 ) ).times( length ) ).toPoint2D() );
            lineTo( tip.plus( direction.rotate( toRadians( -90 - 60 ) ).times( length ) ).toPoint2D() );
            lineTo( tip.toPoint2D() );
        }}.getGeneralPath();
    }

    public static Pattern pyramidSingle() { return Pyramid.single(); }

    public static Pattern pyramidFour() { return Pyramid.four(); }

    public static Pattern pyramidNine() { return Pyramid.nine(); }

    private static class Pyramid {
        public static final UnitVector2D UP = new UnitVector2D( 0, -1 );
        public static final UnitVector2D DOWN = new UnitVector2D( 0, 1 );

        //The height of an equilateral triangle with side length given
        private static double getHeight( final double length ) {return Math.sqrt( 3 ) / 2.0 * length;}

        public static final double DEFAULT_SIZE = 80;

        public static Pattern single() { return single( DEFAULT_SIZE ); }

        public static Pattern four() { return four( DEFAULT_SIZE / 2 ); }

        public static Pattern nine() { return nine( DEFAULT_SIZE / 3 ); }

        public static Pattern single( double length ) {
            return new Pattern( List.single( triangle( length, new edu.colorado.phet.fractions.common.util.immutable.Vector2D( 0, 0 ), UP ) ) );
        }

        public static Pattern four( double length ) {
            final double h = getHeight( length );
            return new Pattern( single( length * 2 ).outline, single( length ).shapes.append( list( triangle( length, new edu.colorado.phet.fractions.common.util.immutable.Vector2D( -length / 2, h ), UP ),
                                                                                                    triangle( length, new edu.colorado.phet.fractions.common.util.immutable.Vector2D( 0, h * 2 ), DOWN ),
                                                                                                    triangle( length, new edu.colorado.phet.fractions.common.util.immutable.Vector2D( length / 2, h ), UP ) ) ) );
        }

        public static Pattern nine( double length ) {
            final double h = getHeight( length );
            return new Pattern( single( length * 3 ).outline, four( length ).shapes.append( list( triangle( length, new edu.colorado.phet.fractions.common.util.immutable.Vector2D( -length, h * 2 ), UP ),
                                                                                                  triangle( length, new edu.colorado.phet.fractions.common.util.immutable.Vector2D( -length / 2, h * 3 ), DOWN ),
                                                                                                  triangle( length, new edu.colorado.phet.fractions.common.util.immutable.Vector2D( 0, h * 2 ), UP ),
                                                                                                  triangle( length, new edu.colorado.phet.fractions.common.util.immutable.Vector2D( length / 2, h * 3 ), DOWN ),
                                                                                                  triangle( length, new edu.colorado.phet.fractions.common.util.immutable.Vector2D( length, h * 2 ), UP ) ) ) );
        }
    }

    //Designed as the level icon for level 7, also appears in the later levels
    public static Pattern ringOfHexagons() {
        double ds = 8;
        final double r = ( 50 - ds ) * 0.5;
        return new Pattern( list( translate( 0, -r, hex() ),
                                  translate( 0, 0, hex() ),
                                  translate( 0, r, hex() ),
                                  translate( r * cos( toRadians( 30 ) ), r * sin( toRadians( 30 ) ), hex() ),
                                  translate( r * cos( toRadians( 30 ) ), -r * sin( toRadians( 30 ) ), hex() ),
                                  translate( -r * cos( toRadians( 30 ) ), r * sin( toRadians( 30 ) ), hex() ),
                                  translate( -r * cos( toRadians( 30 ) ), -r * sin( toRadians( 30 ) ), hex() ) ) );
    }

    private static Shape hex() {return polygon( 25, 6 ).outline;}

    //Designed as the icon for level 8, also appears in the later levels
    public static Pattern ninjaStar() {
        double s = 1.0 / 3;
        double r1 = 100 * s;
        double r2 = 65 * s;
        double cos = Math.abs( Math.cos( toDegrees( 45 ) ) );
        final List<edu.colorado.phet.fractions.common.util.immutable.Vector2D> points = list( v( 0, -r1 ),
                                                                                              v( r2 * cos, -r2 * cos ),
                                                                                              v( r1, 0 ),
                                                                                              v( r2 * cos, r2 * cos ),
                                                                                              v( 0, r1 ),
                                                                                              v( -r2 * cos, r2 * cos ),
                                                                                              v( -r1, 0 ),
                                                                                              v( -r2 * cos, -r2 * cos ) );
        final F<Integer, Shape> tinyTriangle = new F<Integer, Shape>() {
            @Override public Shape f( final Integer index ) {
                return new DoubleGeneralPath( 0, 0 ) {{
                    lineTo( points.index( index % points.length() ).toPoint2D() );
                    lineTo( points.index( ( index + 1 ) % points.length() ).toPoint2D() );
                    lineTo( 0, 0 );
                }}.getGeneralPath();
            }
        };
        return new Pattern( range( 0, 8 ).map( new F<Integer, Shape>() {
            @Override public Shape f( final Integer integer ) {
                return tinyTriangle.f( integer );
            }
        } ) );
    }

    private static Shape translate( final double dx, final double dy, final Shape outline ) {
        return AffineTransform.getTranslateInstance( dx, dy ).createTransformedShape( outline );
    }

    public static void main( String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                new PFrame() {{
                    setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
                    setSize( 1024, 768 );
                    getCanvas().getLayer().addChild( new PNode() {{

                        addChild( new PatternNode( FilledPattern.sequentialFill( Pyramid.nine( 100 ), 3 ), Color.yellow ) {{
                            setOffset( 200, 200 );
                        }} );
                        for ( int i = 4; i <= 8; i++ ) {
                            final int finalI = i;
                            addChild( new PatternNode( FilledPattern.sequentialFill( polygon( 50, finalI ), finalI / 2 ), Color.red ) {{translate( 200 + finalI * 60, 200 );}} );
                        }
                        addChild( new PatternNode( FilledPattern.sequentialFill( tetrisPiece( 50 ), 4 ), Color.red ) {{translate( 200, 400 );}} );
                        addChild( new PatternNode( FilledPattern.sequentialFill( letterLShapedDiagonal( 10, 2 ), 4 ), Color.red ) {{translate( 200, 500 );}} );
                        addChild( new PatternNode( FilledPattern.sequentialFill( sixFlower( 50 ), 4 ), Color.red ) {{translate( 300, 500 );}} );
                        addChild( new PatternNode( FilledPattern.sequentialFill( interleavedLShape( 80, 2, 3 ), 6 ), Color.red ) {{translate( 400, 500 );}} );
                        addChild( new PatternNode( FilledPattern.sequentialFill( ringOfHexagons(), 7 ), Color.red ) {{translate( 400, 500 );}} );
                        addChild( new PatternNode( FilledPattern.sequentialFill( ninjaStar(), 8 ), Color.red ) {{translate( 400, 500 );}} );
                    }} );
                }}.setVisible( true );
            }
        } );
    }
}