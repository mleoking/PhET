// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import java.util.ArrayList;
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

    //Allow fractions to get closer together so it won't go into an infinite loop if they have to overlap
    private final double padding = 20;

    public MatchingGameModel( ModelViewTransform transform ) {
        ArrayList<Representation> representations = new ArrayList<Representation>() {{
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
            add( new Representation() {
                public RepresentationNode createNode( ModelViewTransform transform, Fraction fraction ) {
                    return new PatternNode( transform, new Pattern.NineGrid(), fraction );
                }
            } );
        }};

        for ( int i = 0; i < 4; i++ ) {
            Fraction fraction = new Fraction( random.nextInt( 11 ) + 1, random.nextInt( 11 ) + 1 );

            ArrayList<Representation> remainingRepresentations = new ArrayList<Representation>( representations );

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

//    private ImmutableVector2D getPosition( Representation decimalFraction, double padding ) {
//        while ( hits( decimalFraction, fractionRepresentations, padding ) && padding > 0 ) {
//            decimalFraction.setOffset( new ImmutableVector2D( random.nextInt( 1000 ), random.nextInt( 600 ) ) );
//            padding = padding - 0.3;
//        }
//        return decimalFraction.getOffset();
//    }

    //Check to see if one node overlaps a pre-existing node, so it can be placed in an open area
//    private boolean hits( Representation representation, List<Representation> representations, double padding ) {
//        for ( Representation a : representations ) {
//            final PBounds aBounds = a.node.getGlobalFullBounds();
//            final PBounds bBounds = representation.node.getGlobalFullBounds();
//            if ( expand( aBounds, padding / 2, padding / 2 ).intersects( expand( bBounds, padding / 2, padding / 2 ) ) ) {
//                return true;
//            }
//            if ( !new Rectangle2D.Double( 0, 0, 1000, 600 ).contains( expand( bBounds, padding, padding ) ) ) {
//                return true;
//            }
//        }
//        return false;
//    }
}