package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;


public class RPALArrowNode extends ArrowNode {

    private static final Point2D TAIL_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D TIP_LOCATION = new Point2D.Double( 60, 0 );
    private static final int HEAD_HEIGHT = 20;
    private static final int HEAD_WIDTH = 20;
    private static final int TAIL_WIDTH = 10;
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_PAINT = Color.BLACK;
    private static final Color FILL_PAINT = new Color( 46, 107, 178 );
    
    public RPALArrowNode() {
        super( TAIL_LOCATION, TIP_LOCATION, HEAD_HEIGHT, HEAD_WIDTH, TAIL_WIDTH );
        setStroke( STROKE );
        setStrokePaint( STROKE_PAINT );
        setPaint( FILL_PAINT );
    }
}
