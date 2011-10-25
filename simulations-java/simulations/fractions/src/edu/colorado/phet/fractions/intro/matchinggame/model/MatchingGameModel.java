// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import fj.F2;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.common.phetcommon.view.util.RectangleUtils.expand;

/**
 * @author Sam Reid
 */
public class MatchingGameModel {
    public final ArrayList<Representation> fractionRepresentations = new ArrayList<Representation>();
    private final Random random = new Random();

    //Allow fractions to get closer together so it won't go into an infinite loop if they have to overlap
    private final double padding = 20;

    public MatchingGameModel( ModelViewTransform transform ) {

        final ArrayList<F2<ModelViewTransform, Fraction, Representation>> representations = new ArrayList<F2<ModelViewTransform, Fraction, Representation>>() {{
            add( new F2<ModelViewTransform, Fraction, Representation>() {
                public Representation f( ModelViewTransform transform, Fraction fraction ) {
                    return createDecimalRepresentation( transform, fraction );
                }
            } );
            add( new F2<ModelViewTransform, Fraction, Representation>() {
                public Representation f( ModelViewTransform transform, Fraction fraction ) {
                    return createFractionRepresentation( transform, fraction );
                }
            } );
            add( new F2<ModelViewTransform, Fraction, Representation>() {
                public Representation f( ModelViewTransform transform, Fraction fraction ) {
                    return createPieRepresentation( transform, fraction );
                }
            } );
        }};

        for ( int i = 0; i < 4; i++ ) {
            Fraction fraction = new Fraction( random.nextInt( 11 ) + 1, random.nextInt( 11 ) + 1 );

            ArrayList<F2<ModelViewTransform, Fraction, Representation>> remainingRepresentations = new ArrayList<F2<ModelViewTransform, Fraction, Representation>>( representations );

            for ( int k = 0; k < 2; k++ ) {
                int selected = random.nextInt( remainingRepresentations.size() );
                F2<ModelViewTransform, Fraction, Representation> selectedRepresentation = remainingRepresentations.get( selected );
                fractionRepresentations.add( selectedRepresentation.f( transform, fraction ) );
                remainingRepresentations.remove( selectedRepresentation );
            }
        }
    }

    private PieRepresentation createPieRepresentation( ModelViewTransform transform, Fraction fraction ) {
        return new PieRepresentation( transform, fraction, getPosition( new FractionRepresentation( transform, fraction ), padding ) );
    }

    private FractionRepresentation createFractionRepresentation( ModelViewTransform transform, Fraction fraction ) {
        return new FractionRepresentation( transform, fraction, getPosition( new FractionRepresentation( transform, fraction ), padding ) );
    }

    private DecimalFraction createDecimalRepresentation( ModelViewTransform transform, Fraction fraction ) {
        return new DecimalFraction( transform, fraction, getPosition( new DecimalFraction( transform, fraction ), padding ) );
    }

    private ImmutableVector2D getPosition( Representation decimalFraction, double padding ) {
        while ( hits( decimalFraction, fractionRepresentations, padding ) && padding > 0 ) {
            decimalFraction.setOffset( new ImmutableVector2D( random.nextInt( 1000 ), random.nextInt( 600 ) ) );
            padding = padding - 0.3;
        }
        return decimalFraction.getOffset();
    }

    //Check to see if one node overlaps a pre-existing node, so it can be placed in an open area
    private boolean hits( Representation representation, List<Representation> representations, double padding ) {
        for ( Representation a : representations ) {
            final PBounds aBounds = a.node.getGlobalFullBounds();
            final PBounds bBounds = representation.node.getGlobalFullBounds();
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