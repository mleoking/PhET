/* Copyright 2004-2010, University of Colorado */
package edu.colorado.phet.motionseries.javastage.stage.test;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * This text node provides convenience constructors that include offset and scale, and shows a point for the origin of the node.
 *
 * @author Sam Reid
 */
public class PositionedTextNode extends PNode {
    public PositionedTextNode(String text, double x, double y, double scale) {
        PText t = new PText(text);
        setScale(scale);
        addChild(t);

        PhetPPath phetPPath = new PhetPPath(new Ellipse2D.Double(-2, -2, 4, 4), Color.blue);
        addChild(phetPPath);
        setOffset(x, y);
    }

    public PositionedTextNode(String text, double x, double y) {
        this(text, x, y, 1.0);
    }
}
