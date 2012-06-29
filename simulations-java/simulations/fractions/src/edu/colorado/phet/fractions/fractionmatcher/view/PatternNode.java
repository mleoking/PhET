// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.view;

import fj.P2;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows a fraction using a Pattern (see Pattern class)
 *
 * @author Sam Reid
 */
public class PatternNode extends PNode {

    public PatternNode( final FilledPattern representation, Color color ) {
        for ( P2<Shape, Boolean> o : representation.shapes ) {

            //Using a stroke more than 1 here prevents the right side from getting trimmed off during toImage
            addChild( new PhetPPath( o._1(), o._2() ? color : Color.white, new BasicStroke( 1f ), Color.black ) );
        }

        addChild( new PhetPPath( representation.outline, new BasicStroke( 2f ), Color.black ) );
    }

    //Update the strokes of children so they will appear at the same size even if there is an overall scale.
    public void scaleStrokes( final double scale ) {
        for ( Object child : getChildrenReference() ) {
            PNode childNode = (PNode) child;
            if ( childNode instanceof PhetPPath ) {
                PhetPPath path = (PhetPPath) childNode;
                BasicStroke stroke = (BasicStroke) path.getStroke();
                path.setStroke( new BasicStroke( (float) ( stroke.getLineWidth() * scale ) ) );
            }
        }
    }
}