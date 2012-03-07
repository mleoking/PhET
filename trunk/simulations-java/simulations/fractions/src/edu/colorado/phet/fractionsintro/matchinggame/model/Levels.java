// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.model.containerset.Container;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.view.FractionNode;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.NineGrid;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern.SixPlusSigns;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.HorizontalBarsNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PieNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.VerticalBarsNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.LIGHT_GREEN;
import static edu.colorado.phet.fractionsintro.equalitylab.view.EqualityLabCanvas.LIGHT_BLUE;
import static edu.colorado.phet.fractionsintro.matchinggame.model.Motions.MoveToCell;
import static fj.data.List.iterableList;
import static fj.data.List.range;

/**
 * Levels for the matching game, declared as functions below that return the MovableFraction instances (and hence their representations) in a level.
 *
 * @author Sam Reid
 */
public class Levels {

    public static Levels Levels = new Levels();

    private final F<Fraction, ArrayList<F<Fraction, PNode>>> representationFunction = new F<Fraction, ArrayList<F<Fraction, PNode>>>() {
        @Override public ArrayList<F<Fraction, PNode>> f( Fraction fraction ) {
            return createRepresentations( fraction );
        }
    };

    //Singleton, use Levels instance
    private Levels() {
    }

    //Nodes for filling the cells.
    final F<Fraction, PNode> numeric = new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new FractionNode( f, 0.3 );
        }
    };
    final F<Fraction, PNode> horizontalBarsGreen = makeComposite( new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new HorizontalBarsNode( new Fraction( f.numerator, f.denominator ), 0.9, LIGHT_GREEN );
        }
    } );
    final F<Fraction, PNode> horizontalBarsBlue = makeComposite( new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new HorizontalBarsNode( new Fraction( f.numerator, f.denominator ), 0.9, LIGHT_BLUE );
        }
    } );
    final F<Fraction, PNode> verticalBarsGreen = makeComposite( new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new VerticalBarsNode( new Fraction( f.numerator, f.denominator ), 0.9, LIGHT_GREEN );
        }
    } );
    final F<Fraction, PNode> verticalBarsBlue = makeComposite( new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new VerticalBarsNode( new Fraction( f.numerator, f.denominator ), 0.9, LIGHT_BLUE );
        }
    } );
    final F<Fraction, PNode> greenPies = makeComposite( new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new PieNode( new Property<ContainerSet>( new ContainerSet( f.denominator, new Container[] { new Container( f.denominator, range( 0, f.numerator ) ) } ) ), LIGHT_GREEN );
        }
    } );
    final F<Fraction, PNode> bluePies = makeComposite( new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new PieNode( new Property<ContainerSet>( new ContainerSet( f.denominator, new Container[] { new Container( f.denominator, range( 0, f.numerator ) ) } ) ), LIGHT_BLUE );
        }
    } );
    final F<Fraction, PNode> sixPlussesGreen = makeComposite( new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new PatternNode( new SixPlusSigns(), f.numerator, LIGHT_GREEN );
        }
    } );
    final F<Fraction, PNode> sixPlussesBlue = makeComposite( new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new PatternNode( new SixPlusSigns(), f.numerator, LIGHT_BLUE );
        }
    } );
    final F<Fraction, PNode> nineGridGreen = makeComposite( new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new PatternNode( new NineGrid(), f.numerator, LIGHT_GREEN );
        }
    } );
    final F<Fraction, PNode> nineGridBlue = makeComposite( new F<Fraction, PNode>() {
        @Override public PNode f( Fraction f ) {
            return new PatternNode( new NineGrid(), f.numerator, LIGHT_BLUE );
        }
    } );

    //Converts primitives (representations for fractions <=1) to composite (representation for fractions >=1)
    private F<Fraction, PNode> makeComposite( final F<Fraction, PNode> f ) {
        return new F<Fraction, PNode>() {
            @Override public PNode f( Fraction fraction ) {
                return composite( fraction, f );
            }
        };
    }

    public static final Random random = new Random();

    private ResultPair createPair( ArrayList<Fraction> fractions, ArrayList<Cell> cells, F<Fraction, ArrayList<F<Fraction, PNode>>> representationPool, final List<ResultPair> alreadySelected ) {

        //choose a fraction
        final Fraction fraction = fractions.get( random.nextInt( fractions.size() ) );

        //Sampling is without replacement, so remove the old fraction.
        fractions.remove( fraction );
        ArrayList<F<Fraction, PNode>> representations = representationPool.f( fraction );

        //Don't allow duplicate representations for fractions
        //Find all representations for the given fraction
        List<Result> previouslySelected = alreadySelected.map( new F<ResultPair, Result>() {
            @Override public Result f( final ResultPair r ) {
                return r.a;
            }
        } ).append( alreadySelected.map( new F<ResultPair, Result>() {
            @Override public Result f( final ResultPair r ) {
                return r.b;
            }
        } ) );
        List<Result> same = previouslySelected.filter( new F<Result, Boolean>() {
            @Override public Boolean f( final Result r ) {
                return r.fraction.fraction().equals( fraction ) && r.representation == numeric;
            }
        } );
        representations.removeAll( same.map( new F<Result, F<Fraction, PNode>>() {
            @Override public F<Fraction, PNode> f( final Result result ) {
                return result.representation;
            }
        } ).toCollection() );

        //create 2 representation for it
        F<Fraction, PNode> representationA = representations.get( random.nextInt( representations.size() ) );
        final Cell cellA = cells.get( random.nextInt( cells.size() ) );
        MovableFraction fractionA = fraction( fraction, cellA, representationA );

        //Don't use the same representation for the 2nd one, and put it in a new cell
        while ( representations.contains( representationA ) ) {
            representations.remove( representationA );
        }
        cells.remove( cellA );

        final Cell cellB = cells.get( random.nextInt( cells.size() ) );
        F<Fraction, PNode> representationB = representations.get( random.nextInt( representations.size() ) );
        MovableFraction fractionB = fraction( fraction, cellB, representationB );

        cells.remove( cellB );

        return new ResultPair( new Result( fractionA, representationA ), new Result( fractionB, representationB ) );
    }

    private static MovableFraction fraction( Fraction fraction, Cell cell, final F<Fraction, PNode> node ) {
        return fraction( fraction.numerator, fraction.denominator, cell, node );
    }

    //Converts primitives (representations for fractions <=1) to composite (representation for fractions >=1)
    public static PNode composite( Fraction fraction, F<Fraction, PNode> node ) {
        if ( fraction.toDouble() <= 1 + 1E-6 ) {
            return node.f( fraction );
        }
        HBox box = new HBox();
        while ( fraction.toDouble() > 0 ) {
            if ( fraction.numerator >= fraction.denominator ) {
                box.addChild( node.f( new Fraction( fraction.denominator, fraction.denominator ) ) );
            }
            else {
                box.addChild( node.f( new Fraction( fraction.numerator, fraction.denominator ) ) );
            }
            fraction = new Fraction( fraction.numerator - fraction.denominator, fraction.denominator );
        }

        //Make it smaller or won't fit
        box.scale( 0.75 );
        return new RichPNode( box );
    }

    //Create a MovableFraction for the given fraction at the specified cell
    private static MovableFraction fraction( int numerator, int denominator, Cell cell, final F<Fraction, PNode> node ) {

        //Cache nodes as images to improve performance
        //TODO: could this put the same node at 2 places in the scene graph?  If so, what problems would that cause?
        return new MovableFraction( new Vector2D( cell.rectangle.getCenter() ), numerator, denominator, false, cell, 1.0,
                                    new Cache<Fraction, PNode>( new F<Fraction, PNode>() {
                                        @Override public PNode f( final Fraction fraction ) {
                                            return new PComposite() {{ addChild( node.f( fraction ) ); }};
                                        }
                                    } ),
                                    MoveToCell( cell ), false );
    }

    private List<MovableFraction> createLevel( List<Cell> _cells, Fraction[] a ) {

        F<Fraction, ArrayList<F<Fraction, PNode>>> representations = representationFunction;
        assert _cells.length() % 2 == 0;
        ArrayList<Fraction> fractions = new ArrayList<Fraction>( Arrays.asList( a ) );

        ArrayList<MovableFraction> list = new ArrayList<MovableFraction>();

        //Use mutable collection so it can be removed from for drawing without replacement
        ArrayList<Cell> cells = new ArrayList<Cell>( _cells.toCollection() );

        //Keep track of all so that we don't replicate representations
        ArrayList<ResultPair> all = new ArrayList<ResultPair>();
        while ( list.size() < _cells.length() ) {
            ResultPair pair = createPair( fractions, cells, representations, iterableList( all ) );
            all.add( pair );
            list.add( pair.a.fraction );
            list.add( pair.b.fraction );
        }
        return iterableList( list );
    }

    public static final @Data class Result {
        public final MovableFraction fraction;
        public final F<Fraction, PNode> representation;
    }

    public static final @Data class ResultPair {
        public final Result a;
        public final Result b;
    }

    /**
     * Level 1
     * No mixed numbers
     * Only “exact” matches will be present.  So for instance if there is a 3/6  and a pie with 6 divisions and 3 shaded slices, there will not be a ½  present .  In other words, the numerical representation on this level will exactly match the virtual manipulative.
     * Only numbers/representations  ≦ 1 possible on this level
     * “Easy” shapes on this level (not some of the more abstract representations)
     */
    public F<List<Cell>, List<MovableFraction>> Level1 = new F<List<Cell>, List<MovableFraction>>() {
        @Override public List<MovableFraction> f( List<Cell> cells ) {

            final Fraction[] a = {
                    new Fraction( 1, 3 ),
                    new Fraction( 2, 3 ),
                    new Fraction( 1, 4 ),
                    new Fraction( 3, 4 ),
                    new Fraction( 1, 2 ),
                    new Fraction( 1, 1 ) };
            return createLevel( cells, a );
        }
    };

    //Create the default representations that will be used in all levels
    @SuppressWarnings("unchecked")
    private ArrayList<F<Fraction, PNode>> createRepresentations( Fraction fraction ) {
        final ArrayList<F<Fraction, PNode>> list = new ArrayList<F<Fraction, PNode>>( Arrays.asList( greenPies, bluePies, horizontalBarsGreen, horizontalBarsBlue, verticalBarsGreen, verticalBarsBlue ) );

        //Adds any representations specific to the fraction's denominator (i.e. that don't work for other denominators)
        if ( fraction.denominator == 6 ) {
            list.add( sixPlussesGreen );
            list.add( sixPlussesBlue );
        }
        if ( fraction.denominator == 9 ) {
            list.add( nineGridGreen );
            list.add( nineGridBlue );
        }

        //Add one "numerical" representation for each graphical one, so that on average there will be about 50% numerical
        final int listSize = list.size();
        for ( int i = 0; i < listSize; i++ ) {
            list.add( numeric );
        }
        return list;
    }

    /**
     * Level 2
     * Reduced fractions possible on this level.  So, for instance 3/6 and ½  could both be present.  Or a virtual representation of 3/6 could have the numerical of ½ be its only possible match
     * Still only numbers/representations  ≦ 1 possible
     * More shapes can be introduced
     */
    public F<List<Cell>, List<MovableFraction>> Level2 = new F<List<Cell>, List<MovableFraction>>() {
        @Override public List<MovableFraction> f( List<Cell> cells ) {
            return createLevel( cells, new Fraction[] {
                    new Fraction( 1, 2 ),
                    new Fraction( 2, 4 ),
                    new Fraction( 1, 3 ),
                    new Fraction( 2, 3 ),
                    new Fraction( 3, 6 ),
                    new Fraction( 2, 6 ) } );
        }
    };

    /**
     * Level 3:
     * Reduced fractions possible on this level.  So, for instance 3/6 and ½  could both be present.  Or a virtual representation of 3/6 could have the numerical of ½ be its only possible match
     * Still only numbers/representations  ≦ 1 possible
     * More shapes can be introduced
     */
    public F<List<Cell>, List<MovableFraction>> Level3 = new F<List<Cell>, List<MovableFraction>>() {
        @Override public List<MovableFraction> f( List<Cell> cells ) {
            return createLevel( cells, new Fraction[] {
                    new Fraction( 3, 2 ),
                    new Fraction( 4, 3 ),
                    new Fraction( 6, 3 ),
                    new Fraction( 4, 2 ),
                    new Fraction( 7, 6 ),
                    new Fraction( 4, 5 ),
                    new Fraction( 3, 4 ) } );
        }
    };

    /**
     * Level 4:
     * All representations possible as well as complicated mixed/improper numbers
     */
    public F<List<Cell>, List<MovableFraction>> Level4 = new F<List<Cell>, List<MovableFraction>>() {
        @Override public List<MovableFraction> f( List<Cell> cells ) {
            return createLevel( cells, new Fraction[] {
                    new Fraction( 17, 15 ),
                    new Fraction( 14, 8 ),
                    new Fraction( 6, 3 ),
                    new Fraction( 9, 8 ),
                    new Fraction( 8, 9 ),
                    new Fraction( 12, 13 ),
                    new Fraction( 9, 7 ) } );
        }
    };

    public F<List<Cell>, List<MovableFraction>> get( int level ) {
        return level == 1 ? Level1 :
               level == 2 ? Level2 :
               level == 3 ? Level3 :
               level == 4 ? Level4 :
               Level4;
    }
}