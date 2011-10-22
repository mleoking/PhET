// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.colorado.phet.fractions.intro.matchinggame.view.PieNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PieRepresentation extends Representation {
    protected PieRepresentation( ModelViewTransform transform, Fraction fraction, ImmutableVector2D position ) {
        super( transform, fraction, position.getX(), position.getY() );
    }

    @Override protected PNode toNode( ModelViewTransform transform ) {
        return new PieNode( transform, this );
    }
}