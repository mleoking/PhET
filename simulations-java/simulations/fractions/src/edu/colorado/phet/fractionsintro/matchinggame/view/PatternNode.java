// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.common.view.Pattern;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;

/**
 * @author Sam Reid
 */
public class PatternNode extends RepresentationNode {
    public PatternNode( final ModelViewTransform transform, final Pattern representation, Fraction fraction, int numFilled ) {
        super( transform, fraction );
        int count = 0;
        for ( Shape o : representation.shapes ) {
            Color color = count < numFilled ? Color.red : Color.white;
            addChild( new PhetPPath( o, color, new BasicStroke( 1 ), Color.black ) );

            count++;
        }
    }
}