// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.common.phetcommon.view.util.RectangleUtils.expand;

/**
 * @author Sam Reid
 */
public class MatchingGameModel {
    public final ObservableList<Representation> fractionRepresentations = new ObservableList<Representation>();
    private final Random random = new Random();

    //Allow fractions to get closer together so it won't go into an infinite loop if they have to overlap
    private final double padding = 20;

    public MatchingGameModel( ModelViewTransform transform ) {
        for ( int i = 0; i < 10; i++ ) {
            Fraction fraction = new Fraction( random.nextInt( 11 ) + 1, random.nextInt( 11 ) + 1 );

            fractionRepresentations.add( new DecimalFraction( transform, fraction, getPosition( new DecimalFraction( transform, fraction ), padding ) ) );
            fractionRepresentations.add( new FractionRepresentation( transform, fraction, getPosition( new FractionRepresentation( transform, fraction ), padding ) ) );
            fractionRepresentations.add( new PieRepresentation( transform, fraction, getPosition( new FractionRepresentation( transform, fraction ), padding ) ) );
        }
    }

    private ImmutableVector2D getPosition( Representation decimalFraction, double padding ) {
        while ( hits( decimalFraction, fractionRepresentations, padding ) && padding > 0 ) {
            decimalFraction.setOffset( new ImmutableVector2D( random.nextInt( 1000 ), random.nextInt( 600 ) ) );
            padding = padding - 0.3;
        }
        return decimalFraction.getOffset();
    }

    //Check to see if one node overlaps a pre-existing node, so it can be placed in an open area
    private boolean hits( Representation representation, ObservableList<Representation> representations, double padding ) {
        for ( Representation a : representations ) {
            final PBounds aBounds = a.toNode( ModelViewTransform.createIdentity() ).getGlobalFullBounds();
            final PBounds bBounds = representation.toNode( ModelViewTransform.createIdentity() ).getGlobalFullBounds();
            if ( expand( aBounds, padding / 2, padding / 2 ).intersects( expand( bBounds, padding / 2, padding / 2 ) ) ) {
                return true;
            }
            if ( !new Rectangle2D.Double( 0, 0, 1000, 600 ).contains( expand( bBounds, padding, padding ) ) ) {
                return true;
            }
        }
        return false;
    }
}