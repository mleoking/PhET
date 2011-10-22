// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.colorado.phet.fractions.intro.matchinggame.view.DecimalFractionNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class DecimalFraction extends Representation {
    public DecimalFraction( ModelViewTransform transform, Fraction fraction ) {
        this( transform, fraction, 0, 0 );
    }

    public DecimalFraction( ModelViewTransform transform, Fraction fraction, ImmutableVector2D position ) {
        this( transform, fraction, position.getX(), position.getY() );
    }

    public DecimalFraction( ModelViewTransform transform, Fraction fraction, double x, double y ) {
        super( transform, fraction, x, y );
    }

    @Override protected PNode toNode( ModelViewTransform transform ) {
        return new DecimalFractionNode( transform, this ) {{
            scale( 0.5 );
        }};
    }
}