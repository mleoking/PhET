// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractions.intro.common.view.Pattern;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.colorado.phet.fractions.intro.matchinggame.view.DecimalFractionNode;
import edu.colorado.phet.fractions.intro.matchinggame.view.FractionRepresentationNode;
import edu.colorado.phet.fractions.intro.matchinggame.view.PatternNode;
import edu.colorado.phet.fractions.intro.matchinggame.view.PieNode;
import edu.colorado.phet.fractions.intro.matchinggame.view.RepresentationNode;

/**
 * @author Sam Reid
 */
public class MatchingGameModel {
    public final ArrayList<RepresentationNode> nodes = new ArrayList<RepresentationNode>();
    private final Random random = new Random();

    public MatchingGameModel( ModelViewTransform transform ) {

        boolean usedNine = false;
        for ( int i = 0; i < 4; i++ ) {
            Fraction fraction = new Fraction( !usedNine ? random.nextInt( 9 ) + 1 : random.nextInt( 11 ) + 1, !usedNine ? 9 : random.nextInt( 11 ) + 1 );
            usedNine = true;

            ArrayList<Representation> remainingRepresentations = new ArrayList<Representation>( getRepresentationsForFraction( fraction ) );

            for ( int k = 0; k < 2; k++ ) {
                int selected = random.nextInt( remainingRepresentations.size() );
                Representation selectedRepresentation = remainingRepresentations.get( selected );
                final RepresentationNode node = selectedRepresentation.createNode( transform, fraction );
                node.setOffset( random.nextInt( 1000 ), random.nextInt( 600 ) );
                nodes.add( node );
                remainingRepresentations.remove( selectedRepresentation );
            }
        }
    }

    private Collection<? extends Representation> getRepresentationsForFraction( final Fraction fraction ) {
        return new ArrayList<Representation>() {{

            final boolean canUseNineGrid = fraction.denominator % 9 == 0 && fraction.getValue() < 1;

            //For nine-grid, use that representation and one other at random
            if ( canUseNineGrid ) {
                add( new Representation() {
                    public RepresentationNode createNode( ModelViewTransform transform, Fraction fraction ) {
                        return new PatternNode( transform, new Pattern.NineGrid(), fraction, fraction.numerator );
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
            add( new Representation() {
                public RepresentationNode createNode( ModelViewTransform transform, Fraction fraction ) {
                    return new DecimalFractionNode( transform, fraction );
                }
            } );
            add( new Representation() {
                public RepresentationNode createNode( ModelViewTransform transform, Fraction fraction ) {
                    return new FractionRepresentationNode( transform, fraction );
                }
            } );
            add( new Representation() {
                public RepresentationNode createNode( ModelViewTransform transform, Fraction fraction ) {
                    return new PieNode( transform, fraction );
                }
            } );
        }};
    }
}