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
}