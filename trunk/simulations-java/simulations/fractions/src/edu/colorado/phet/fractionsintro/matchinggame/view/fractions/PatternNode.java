// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view.fractions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.common.view.Pattern;
import edu.colorado.phet.fractionsintro.equalitylab.view.EqualityLabCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PatternNode extends PNode {
    public PatternNode( final Pattern representation, int numFilled ) {
        int count = 0;
        for ( Shape o : representation.shapes ) {
            Color color = count < numFilled ? EqualityLabCanvas.lightBlue : Color.white;
            addChild( new PhetPPath( o, color, new BasicStroke( 1 ), Color.black ) );
            count++;
        }
    }
}