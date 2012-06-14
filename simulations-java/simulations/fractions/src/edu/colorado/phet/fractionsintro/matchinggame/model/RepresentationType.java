// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;
import lombok.Data;

import java.util.Random;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.intro.model.containerset.Container;
import edu.colorado.phet.fractionsintro.intro.model.containerset.ContainerSet;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.umd.cs.piccolo.PNode;

import static fj.data.List.*;

/**
 * A particular representation like pies, which can have different specific representations (such as blue pies vs green pies), but
 * which should remain distinct, so e.g., the fraction 3/7 doesn't appear as both blue pies and green pies.
 *
 * @author Sam Reid
 */
public @Data class RepresentationType {
    public final String name;
    public final F<Fraction, Boolean> appliesTo;
    public final List<F<Fraction, PNode>> representations;

    //Function wrappers for use in collections
    public static F<RepresentationType, String> _name = new F<RepresentationType, String>() {
        @Override public String f( final RepresentationType representationType ) {
            return representationType.name;
        }
    };

    //Seed that should change whenever the level changes.  Since nodes are recreated often in this sim, random representations must use same seed for each level
    //or they will "jump around"
    private static int seed = 0;
    private static Random random = new Random();

    public static RepresentationType singleRepresentation( String name, F<Fraction, Boolean> appliesTo, F<Fraction, PNode> r ) {
        return new RepresentationType( name, appliesTo, single( r ) );
    }

    @SuppressWarnings(value = "unchecked")
    public static RepresentationType toRepresentation( String name, final F<Fraction, Boolean> appliesTo, boolean randomComposite,
                                                       final F<Fraction, PNode> a,
                                                       final F<Fraction, PNode> b,
                                                       final F<Fraction, PNode> c ) {
        return new RepresentationType( name, appliesTo, list( makeComposite( randomComposite, a ), makeComposite( randomComposite, b ), makeComposite( randomComposite, c ) ) );
    }

    public boolean contains( final F<Fraction, PNode> representation ) {
        return representations.exists( new F<F<Fraction, PNode>, Boolean>() {
            @Override public Boolean f( final F<Fraction, PNode> f ) {
                return f == representation;
            }
        } );
    }

    public F<Fraction, PNode> chooseOne() { return representations.index( Levels.random.nextInt( representations.length() ) ); }

    //Converts primitives (representations for fractions <=1) to composite (representation for fractions >=1)
    private static F<Fraction, PNode> makeComposite( final boolean randomComposite, final F<Fraction, PNode> f ) {
        return new F<Fraction, PNode>() {
            @Override public PNode f( Fraction fraction ) {
                return composite( randomComposite, fraction, f );
            }
        };
    }

    //Converts primitives (representations for fractions <=1) to composite (representation for fractions >=1)
    public static PNode composite( boolean randomComposite, Fraction f, F<Fraction, PNode> node ) {
        if ( f.toDouble() <= 1 + 1E-6 ) {
            return node.f( f );
        }

        RichPNode box = randomComposite ? buildCompositeRandomized( f, node ) : buildCompositeFirstFilledWhole( f, node );

        //Scale to a size of 110 so it will be a good fit for the starting cells and score cells
        double size = box.getFullWidth();
        final double scale = 110 / size;
        box.scale( scale );

        if ( scale < 1 ) {
            //if the objects got scaled down, then scale up the strokes so they will look like they have the same width
            for ( PNode child : box.getChildren() ) {
                if ( child instanceof PatternNode ) {
                    PatternNode patternNode = (PatternNode) child;
                    patternNode.scaleStrokes( 1.0 / scale );
                }
            }
        }

        //scale up the font
        return new RichPNode( box );
    }

    private static RichPNode buildCompositeRandomized( final Fraction f, final F<Fraction, PNode> node ) {
        int numContainers = (int) Math.ceil( f.toDouble() );
        Random random = new Random( f.hashCode() * 17 + node.hashCode() + seed );
        ContainerSet containerSet = new ContainerSet( f.denominator, range( 0, numContainers ).map( new F<Integer, Container>() {
            @Override public Container f( final Integer integer ) {
                return new Container( f.denominator, new int[0] );
            }
        } ) );
        for ( int i = 0; i < f.numerator; i++ ) {
            containerSet = containerSet.toggle( containerSet.getRandomEmptyCell( random ) );
        }

        HBox box = new HBox();
        for ( int i = 0; i < numContainers; i++ ) {
            final int numeratorToUse = containerSet.containers.index( i ).getFilledCells().length();
            box.addChild( node.f( new Fraction( numeratorToUse, f.denominator ) ) );
        }
        return box;
    }

    private static RichPNode buildCompositeFirstFilledWhole( Fraction f, final F<Fraction, PNode> node ) {
        HBox box = new HBox();
        while ( f.toDouble() > 0 ) {
            final int numeratorToUse = f.numerator >= f.denominator ? f.denominator : f.numerator;
            box.addChild( node.f( new Fraction( numeratorToUse, f.denominator ) ) );
            f = new Fraction( f.numerator - f.denominator, f.denominator );
        }
        return box;
    }

    public static void newSeed() {
        seed = random.nextInt( Integer.MAX_VALUE / 2 );
    }
}