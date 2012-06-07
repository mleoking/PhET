// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;
import lombok.Data;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.umd.cs.piccolo.PNode;

import static fj.data.List.single;

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

    public static RepresentationType singleRepresentation( String name, F<Fraction, Boolean> appliesTo, F<Fraction, PNode> r ) {
        return new RepresentationType( name, appliesTo, single( r ) );
    }

    public static RepresentationType twoComposites( String name, final F<Fraction, Boolean> appliesTo, final F<Fraction, PNode> a, final F<Fraction, PNode> b ) {
        return new RepresentationType( name, appliesTo, single( makeComposite( a ) ).snoc( makeComposite( b ) ) );
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
    private static F<Fraction, PNode> makeComposite( final F<Fraction, PNode> f ) {
        return new F<Fraction, PNode>() {
            @Override public PNode f( Fraction fraction ) {
                return composite( fraction, f );
            }
        };
    }

    //Converts primitives (representations for fractions <=1) to composite (representation for fractions >=1)
    public static PNode composite( Fraction fraction, F<Fraction, PNode> node ) {
        if ( fraction.toDouble() <= 1 + 1E-6 ) {
            return node.f( fraction );
        }
        HBox box = new HBox();
        while ( fraction.toDouble() > 0 ) {
            if ( fraction.numerator >= fraction.denominator ) {
                final PNode node1 = node.f( new Fraction( fraction.denominator, fraction.denominator ) );
                box.addChild( node1 );
            }
            else {
                final PNode node2 = node.f( new Fraction( fraction.numerator, fraction.denominator ) );
                box.addChild( node2 );
            }
            fraction = new Fraction( fraction.numerator - fraction.denominator, fraction.denominator );
        }

        double size = box.getFullWidth();

        //Scale to a size of 110 so it will be a good fit for the starting cells and score cells
        final double scale = 110 / size;
        box.scale( scale );

        if ( scale < 1 ) {
            //if the objects got scaled down, then scale up the strokes so they will look like they have the same width
            for ( PNode child : box.getChildren() ) {
                System.out.println( "child.getClass().getName() = " + child.getClass().getName() );
                if ( child instanceof PatternNode ) {
                    PatternNode patternNode = (PatternNode) child;
                    patternNode.scaleStrokes( 1.0 / scale );
                }
            }
        }

        //scale up the font
        return new RichPNode( box );
    }
}
