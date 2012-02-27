// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractionsintro.intro.model.Container;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSet;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.FractionRepNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.HorizontalBarsNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PatternNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.PieNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.RepNode;
import edu.colorado.phet.fractionsintro.matchinggame.view.fractions.VerticalBarsNode;

import static edu.colorado.phet.fractionsintro.common.view.Pattern.NineGrid;
import static edu.colorado.phet.fractionsintro.common.view.Pattern.SixPlusSigns;

/**
 * @author Sam Reid
 */
public class MatchingGameModelOld {
    public final ArrayList<RepNode> nodes = new ArrayList<RepNode>();
    private final Random random = new Random();

    public MatchingGameModelOld( ModelViewTransform transform ) {
        {
            Fraction ninth = new Fraction( random.nextInt( 8 ) + 1, 9 );
            nodes.add( new PatternNode( transform, new NineGrid(), ninth, ninth.numerator ) {{setOffset( randomLocation() );}} );
            final RepNode x = getGenericRepresentations().get( random.nextInt( getGenericRepresentations().size() ) ).createNode( transform, ninth );
            x.setOffset( randomLocation() );
            nodes.add( x );
        }

        {
            Fraction sixth = new Fraction( random.nextInt( 5 ) + 1, 6 );
            nodes.add( new PatternNode( transform, new SixPlusSigns(), sixth, sixth.numerator ) {{setOffset( randomLocation() );}} );
            final RepNode node = getGenericRepresentations().get( random.nextInt( getGenericRepresentations().size() ) ).createNode( transform, sixth );
            node.setOffset( randomLocation() );
            nodes.add( node );
        }

        for ( int i = 0; i < 4; i++ ) {
            Fraction fraction = new Fraction( random.nextInt( 11 ) + 1, random.nextInt( 11 ) + 1 );
            ArrayList<Representation> remainingRepresentations = new ArrayList<Representation>( getRepresentationsForFraction( fraction ) );

            for ( int k = 0; k < 2; k++ ) {
                int selected = random.nextInt( remainingRepresentations.size() );
                Representation selectedRepresentation = remainingRepresentations.get( selected );
                final RepNode node = selectedRepresentation.createNode( transform, fraction );
                node.setOffset( randomLocation() );
                nodes.add( node );
                remainingRepresentations.remove( selectedRepresentation );
            }
        }
    }

    private ImmutableVector2D randomLocation() {
        return new ImmutableVector2D( random.nextInt( 1000 ), random.nextInt( 600 ) );
    }

    private Collection<? extends Representation> getRepresentationsForFraction( final Fraction fraction ) {
        return new ArrayList<Representation>() {{

            final boolean canUseNineGrid = fraction.denominator % 9 == 0 && fraction.getValue() < 1;
            final boolean canUseSixPlusSigns = fraction.denominator % 6 == 0 && fraction.getValue() < 1;
            //For nine-grid, use that representation and one other at random
            if ( canUseNineGrid ) {
                add( new Representation() {
                    public RepNode createNode( ModelViewTransform transform, Fraction fraction ) {
                        return new PatternNode( transform, new NineGrid(), fraction, fraction.numerator );
                    }
                } );
                add( getGenericRepresentations().get( random.nextInt( getGenericRepresentations().size() ) ) );
            }
            else if ( canUseSixPlusSigns ) {
                add( new Representation() {
                    public RepNode createNode( ModelViewTransform transform, Fraction fraction ) {
                        return new PatternNode( transform, new SixPlusSigns(), fraction, fraction.numerator );
                    }
                } );
                add( getGenericRepresentations().get( random.nextInt( getGenericRepresentations().size() ) ) );
            }
            else {
                addAll( getGenericRepresentations() );
            }
        }};
    }

    private ArrayList<? extends Representation> getGenericRepresentations() {
        return new ArrayList<Representation>() {{
            //Don't use decimals in matching game
//            add( new Representation() {
//                public RepresentationNode createNode( ModelViewTransform transform, Fraction fraction ) {
//                    return new DecimalFractionNode( transform, fraction );
//                }
//            } );
            add( new Representation() {
                public RepNode createNode( ModelViewTransform transform, Fraction fraction ) {
                    return new FractionRepNode( transform, fraction );
                }
            } );
            add( new Representation() {

                //TODO: Fix
                public RepNode createNode( ModelViewTransform transform, Fraction fraction ) {
                    return new PieNode( transform, fraction, new Property<ContainerSet>( new ContainerSet( fraction.denominator, new Container[0] ) ) );
                }
            } );
            add( new Representation() {
                public RepNode createNode( ModelViewTransform transform, Fraction fraction ) {
                    return new HorizontalBarsNode( transform, fraction );
                }
            } );
            add( new Representation() {
                public RepNode createNode( ModelViewTransform transform, Fraction fraction ) {
                    return new VerticalBarsNode( transform, fraction );
                }
            } );
        }};
    }
}