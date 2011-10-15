// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.model;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;
import edu.colorado.phet.fractions.intro.matchinggame.view.FractionRepresentationNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class FractionRepresentation extends Representation {
    public FractionRepresentation( Fraction fraction, double x, double y ) {
        super( fraction, x, y );
    }

    @Override public PNode toNode( ModelViewTransform transform ) {
        return new FractionRepresentationNode( transform, this );
    }
}