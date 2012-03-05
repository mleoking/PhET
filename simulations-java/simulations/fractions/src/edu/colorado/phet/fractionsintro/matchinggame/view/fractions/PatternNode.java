// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view.fractions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.matchinggame.model.Pattern;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows a fraction using a Pattern (see Pattern class)
 *
 * @author Sam Reid
 */
public class PatternNode extends PNode {
    public PatternNode( final Pattern representation, int numFilled, Color color ) {
        int count = 0;
        for ( Shape o : representation.shapes ) {
            addChild( new PhetPPath( o, count < numFilled ? color : Color.white, new BasicStroke( 1 ), Color.black ) );
            count++;
        }
    }
}