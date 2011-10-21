// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.colorado.phet.fractions.intro.matchinggame.view.PieNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PieRepresentation extends Representation {
    protected PieRepresentation( ModelViewTransform transform, Fraction fraction, double x, double y ) {
        super( transform, fraction, x, y );
    }

    @Override protected PNode toNode( ModelViewTransform transform ) {
        return new PieNode( transform, this );
    }
}