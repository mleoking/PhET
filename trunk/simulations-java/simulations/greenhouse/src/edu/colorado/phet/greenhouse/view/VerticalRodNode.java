/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * A node that looks like a vertical rod that is shaded.  This is generally
 * used to connect things in the view, or so make something look like it is on
 * a pole.
 *
 * @author John Blanco
 */
public class VerticalRodNode extends PNode {
    public VerticalRodNode( double width, double height ) {
        Rectangle2D connectingRodShape = new Rectangle2D.Double( 0, 0, width, height );
        PNode connectingRod = new PhetPPath( connectingRodShape );
        connectingRod.setPaint( new GradientPaint( 0f, 0f, Color.WHITE, (float) connectingRodShape.getWidth(), 0f, Color.DARK_GRAY ) );
        addChild( connectingRod );
    }
}
