// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.forcesandmotionbasics.tugofwar;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.forcesandmotionbasics.tugofwar.ForcesNode.TextLocation;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.forcesandmotionbasics.AbstractForcesAndMotionBasicsCanvas.CONTROL_FONT;
import static edu.colorado.phet.forcesandmotionbasics.AbstractForcesAndMotionBasicsCanvas.INSET;

/**
 * @author Sam Reid
 */
public class ForceArrowNode extends PNode {
    public ForceArrowNode( final boolean transparent, final Vector2D tail, final double forceInNewtons, final String name, Color color, final TextLocation textLocation ) {
        final double value = forceInNewtons * 4;
        if ( value == 0 ) { return; }
        final ArrowNode arrowNode = new ArrowNode( tail.toPoint2D(), tail.plus( value, 0 ).toPoint2D(), 30, 40, 20, 2, false );
        arrowNode.setPaint( transparent ? new Color( color.getRed(), color.getGreen(), color.getBlue(), 200 ) : color );
        arrowNode.setStroke( transparent ? new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[] { 6, 4 }, 0 ) : new BasicStroke( 1 ) );
        addChild( arrowNode );
        addChild( new PhetPText( name, CONTROL_FONT ) {{
            if ( textLocation == TextLocation.SIDE ) {
                if ( value > 0 ) {
                    setOffset( arrowNode.getFullBounds().getMaxX() + INSET, arrowNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                }
                else {
                    setOffset( arrowNode.getFullBounds().getMinX() - getFullBounds().getWidth() - INSET, arrowNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
                }
            }
            else {
                setOffset( arrowNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, arrowNode.getFullBounds().getY() - getFullBounds().getHeight() - INSET );
            }
        }} );
    }
}
