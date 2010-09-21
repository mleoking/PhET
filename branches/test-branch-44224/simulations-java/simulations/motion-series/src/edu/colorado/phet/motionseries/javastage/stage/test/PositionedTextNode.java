/* Copyright 2004-2010, University of Colorado */
package edu.colorado.phet.motionseries.javastage.stage.test;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This text node provides convenience constructors that include offset and scale, and shows a point for the origin of the node.
 *
 * @author Sam Reid
 */
public class PositionedTextNode extends PText {
    
    public PositionedTextNode(String text, double x, double y, double scale) {
        super( text );

        // origin point
        PhetPPath phetPPath = new PhetPPath(new Ellipse2D.Double(-2, -2, 4, 4), Color.blue);
        addChild(phetPPath);
        
        setOffset(x, y);
        setScale(scale);
    }

    public PositionedTextNode(String text, double x, double y) {
        this(text, x, y, 1.0);
    }
}
