// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import fj.F;
import fj.data.List;
import lombok.Data;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
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
}
