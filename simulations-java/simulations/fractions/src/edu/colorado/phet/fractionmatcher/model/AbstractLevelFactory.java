package edu.colorado.phet.fractionmatcher.model;

import fj.data.List;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionmatcher.view.PatternNode;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.model.containerset.Container;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionmatcher.model.FillType.MIXED;
import static edu.colorado.phet.fractionmatcher.model.FillType.SEQUENTIAL;
import static edu.colorado.phet.fractionmatcher.model.ShapeType.*;
import static edu.colorado.phet.fractionmatcher.view.FilledPattern.randomFill;
import static edu.colorado.phet.fractionmatcher.view.FilledPattern.sequentialFill;
import static edu.colorado.phet.fractionsintro.common.view.Colors.*;
import static fj.data.List.iterableList;
import static fj.data.List.list;

/**
 * Abstract base class for generating levels for both fractions < 1 (first tab) and the mixed numbers tab.
 *
 * @author Sam Reid
 */
public class AbstractLevelFactory {

    protected final Random RANDOM = new Random();

    //Choose only the representations that match a fraction.
    protected ArrayList<GraphicalRepresentation> filter( final ArrayList<GraphicalRepresentation> representations, final Fraction fraction ) {
        ArrayList<GraphicalRepresentation> filtered = new ArrayList<GraphicalRepresentation>();
        for ( GraphicalRepresentation representation : representations ) {
            if ( matches( representation.shapeType, fraction ) ) { filtered.add( representation ); }
        }
        return filtered;
    }

    //See if the specified shape can render the given fraction.
    protected boolean matches( final ShapeType s, final Fraction fraction ) {
        final int d = fraction.denominator;
        return s == PIES ? true :
               s == HORIZONTAL_BARS ? true :
               s == VERTICAL_BARS ? true :
               s == GRID ? ( d == 4 || d == 9 ) :
               s == FLOWER ? d == 6 :
               s == PYRAMID ? ( d == 1 || d == 4 || d == 9 ) :
               s == PLUSSES ? d == 6 :
               s == POLYGON ? d >= 3 :
               s == TETRIS ? d == 4 :
               s == LETTER_L_SHAPES ? d % 2 == 0 :
               s == INTERLEAVED_L_SHAPES ? ( d == 2 || d == 4 ) :
               s == RING_OF_HEXAGONS ? d == 7 :
               s == NINJA_STAR ? d == 8 :
               false;
    }

    //Generate all possible combinations of color, shape, fill.
    protected List<GraphicalRepresentation> generateAll( final List<ShapeType> shapes, final List<FillType> fills ) {
        ArrayList<GraphicalRepresentation> all = new ArrayList<GraphicalRepresentation>();
        for ( ShapeType shape : shapes ) {
            for ( Color color : list( LIGHT_BLUE, LIGHT_GREEN, LIGHT_RED ) ) {
                for ( FillType fill : fills ) {
                    all.add( new GraphicalRepresentation( shape, color, fill ) );
                }
            }
        }
        return iterableList( all );
    }

    //Create the graphic for one fraction.
    protected PNode createGraphic( Fraction f, final GraphicalRepresentation r ) {
        if ( f.numerator <= f.denominator ) {
            final PatternNode single = createSingle( f, r.shapeType, r.fillType == FillType.RANDOM, r.color );
            HBox box = new HBox( single );
            scaleBoxNode( box, 80 );
            return box;
        }
        else {
            int numShapes = (int) Math.ceil( f.toDouble() );
            if ( numShapes >= 3 ) {
                throw new RuntimeException( "3+ not handled yet" );
            }

            if ( r.fillType == SEQUENTIAL || r.fillType == MIXED ) {
                PatternNode first = createSingle( new Fraction( f.denominator, f.denominator ), r.shapeType, false, r.color );
                PatternNode second = createSingle( new Fraction( f.numerator - f.denominator, f.denominator ), r.shapeType, r.fillType == MIXED, r.color );

                return addToBox( first, second );
            }
            else {
                ContainerSet containerSet = new ContainerSet( f.denominator, list( new Container( f.denominator, List.<Integer>nil() ),
                                                                                   new Container( f.denominator, List.<Integer>nil() ) ) );
                for ( int i = 0; i < f.numerator; i++ ) {
                    containerSet = containerSet.toggle( containerSet.getRandomEmptyCell( RANDOM ) );
                }
                final int numInFirst = containerSet.containers.index( 0 ).getFilledCells().length();
                Fraction firstFraction = new Fraction( numInFirst, f.denominator );
                final int numInSecond = containerSet.containers.index( 1 ).getFilledCells().length();
                Fraction secondFraction = new Fraction( numInSecond, f.denominator );

                double sum = firstFraction.toDouble() + secondFraction.toDouble();
                double difference = sum - f.toDouble();
                if ( difference > 1E-6 ) {
                    System.out.println( "fraction = " + f + ", numInFirst = " + numInFirst + ", numInSecond = " + numInSecond + ", difference = " + difference );
                    throw new RuntimeException( "values didn't add up" );
                }
                PatternNode first = createSingle( firstFraction, r.shapeType, true, r.color );
                PatternNode second = createSingle( secondFraction, r.shapeType, true, r.color );

                return addToBox( first, second );
            }

        }
    }

    //Combine two patterns horizontally
    protected PNode addToBox( final PatternNode first, final PatternNode second ) {

        //AP: If I resample at the highest levels (definitely 7 and 8) a few double shapes appear that have a very small amount of negative space between them.  We should probably widen the negative space or scale those shapes slightly differently.
        //SR: Therefore spacing should be dependent on width
        double width = first.getFullBounds().getWidth();
        LinearFunction function = new LinearFunction( 80, 160, 10, 20 );

        final HBox box = new HBox( function.evaluate( width ), first, second );
        scaleBoxNode( box, 110.0 );
        return box;
    }

    //Scale the node so it will be a good fit for the starting cells and score cells and still have the right stroke.
    protected void scaleBoxNode( final PNode box, double newWidth ) {
        double size = box.getFullBounds().getWidth();
        final double scale = newWidth / size;

        double size2 = box.getFullBounds().getHeight();
        final double scale2 = newWidth / size2;

        box.scale( Math.min( scale, scale2 ) );

        if ( scale < 1 ) {
            //if the objects got scaled down, then scale up the strokes so they will look like they have the same width
            for ( Object child : box.getChildrenReference() ) {
                if ( child instanceof PatternNode ) {
                    PatternNode patternNode = (PatternNode) child;
                    patternNode.scaleStrokes( 1.0 / scale );
                }
            }
        }
    }

    //Create the node for a single (<=1) fraction.
    protected PatternNode createSingle( final Fraction fraction, ShapeType s, boolean random, Color color ) {
        final boolean ok = fraction.numerator <= fraction.denominator && fraction.numerator >= 0 && fraction.denominator > 0;
        if ( !ok ) {
            throw new RuntimeException( "Failed assertion, fraction = " + fraction );
        }
        final int d = fraction.denominator;
        final Pattern container = s == PIES ? Pattern.pie( d ) :
                                  s == VERTICAL_BARS ? Pattern.verticalBars( d ) :
                                  s == HORIZONTAL_BARS ? Pattern.horizontalBars( d ) :
                                  s == FLOWER && d == 6 ? Pattern.sixFlower() :
                                  s == TETRIS && d == 4 ? Pattern.tetrisPiece( 50 ) :
                                  s == PLUSSES && d == 6 ? Pattern.plusSigns( d ) :
                                  s == POLYGON ? Pattern.polygon( 80, d ) :
                                  s == PYRAMID && d == 1 ? Pattern.pyramidSingle() :
                                  s == PYRAMID && d == 4 ? Pattern.pyramidFour() :
                                  s == PYRAMID && d == 9 ? Pattern.pyramidNine() :
                                  s == GRID && d == 4 ? Pattern.grid( 2 ) :
                                  s == GRID && d == 9 ? Pattern.grid( 3 ) :
                                  s == LETTER_L_SHAPES && d % 2 == 0 ? Pattern.letterLShapedDiagonal( 14, d / 2 ) :
                                  s == INTERLEAVED_L_SHAPES && d == 2 ? Pattern.interleavedLShape( 80, 1, 1 ) :
                                  s == INTERLEAVED_L_SHAPES && d == 4 ? Pattern.interleavedLShape( 80, 2, 1 ) :
                                  s == RING_OF_HEXAGONS && d == 7 ? Pattern.ringOfHexagons() :
                                  s == NINJA_STAR && d == 8 ? Pattern.ninjaStar() :
                                  null;
        if ( container == null ) {
            throw new RuntimeException( "Null pattern for rep = " + s + ", f = " + fraction );
        }
        return new PatternNode( random ? randomFill( container, fraction.numerator, 123 ) :
                                sequentialFill( container, fraction.numerator ), color );
    }
}