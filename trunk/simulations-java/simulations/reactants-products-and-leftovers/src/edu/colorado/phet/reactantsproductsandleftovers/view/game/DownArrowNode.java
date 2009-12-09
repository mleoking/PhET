package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;

/**
 * A fancy arrow node, points down, for use in game instructions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DownArrowNode extends ArrowNode {

    private static final int HEAD_HEIGHT = 30;
    private static final int HEAD_WIDTH = 35;
    private static final int TAIL_WIDTH = 10;
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_PAINT = Color.BLACK;
    private static final Color FILL_PAINT = Color.YELLOW;
    
    public DownArrowNode( double length ) {
        super( new Point2D.Double( 0, 0 ), new Point2D.Double( 0, length ), HEAD_HEIGHT, HEAD_WIDTH, TAIL_WIDTH );
        setStroke( STROKE );
        setStrokePaint( STROKE_PAINT );
        setPaint( FILL_PAINT );
    }
}
