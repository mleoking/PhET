// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.intro.common.view.Pattern;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;

/**
 * @author Sam Reid
 */
public class PatternNode extends RepresentationNode {
    public PatternNode( final ModelViewTransform transform, final Pattern representation, Fraction fraction ) {
        super( transform, fraction );
        for ( Shape o : representation.shapes ) {
            addChild( new PhetPPath( o ) );
        }
    }
}
