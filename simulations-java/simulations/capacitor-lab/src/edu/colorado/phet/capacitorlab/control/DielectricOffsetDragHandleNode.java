package edu.colorado.phet.capacitorlab.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Drag handle for changing the dielectric offset.
 * Origin is at the left-center of the node's bounding rectangle (left arrow tip).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricOffsetDragHandleNode extends PhetPNode {

    private static final double ARROW_LENGTH = 40;
    
    private static final double HEAD_WIDTH = ARROW_LENGTH / 2;
    private static final double HEAD_HEIGHT = ARROW_LENGTH / 4;
    private static final double TAIL_WIDTH = ARROW_LENGTH / 6;
    private static final Point2D TIP_LOCATION = new Point2D.Double( 0, 0 );
    private static final Point2D TAIL_LOCATION = new Point2D.Double( ARROW_LENGTH, 0 );
    private static final Color ARROW_FILL_COLOR = Color.GREEN;
    private static final Color ARROW_HILITE_COLOR = Color.YELLOW;
    private static final Color ARROW_STROKE_COLOR = Color.BLACK;
    private static final Stroke ARROW_STROKE = new BasicStroke( 1f );
    
    private static final double LINE_LENGTH = 40;
    private static final Stroke LINE_STROKE = new BasicStroke( 3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3, 3 }, 0 ); // dashed
    private static final Color LINE_COLOR = Color.BLACK;
    
    private final DoubleArrowNode arrowNode;
    
    public DielectricOffsetDragHandleNode( Capacitor capacitor ) {
        
        // arrow
        arrowNode = new DoubleArrowNode( TIP_LOCATION, TAIL_LOCATION, HEAD_HEIGHT, HEAD_WIDTH, TAIL_WIDTH );
        arrowNode.setPaint( ARROW_FILL_COLOR );
        arrowNode.setStrokePaint( ARROW_STROKE_COLOR );
        arrowNode.setStroke( ARROW_STROKE );
        arrowNode.addInputEventListener( new CursorHandler() );
        arrowNode.addInputEventListener( new PBasicInputEventHandler() {
            
            private boolean isMousePressed, isMouseInside;
            
            @Override
            public void mouseEntered( PInputEvent event ) {
                isMouseInside = true;
                arrowNode.setPaint( ARROW_HILITE_COLOR );
            }

            @Override
            public void mouseExited( PInputEvent event ) {
                isMouseInside = false;
                if ( !isMousePressed ) {
                    arrowNode.setPaint( ARROW_FILL_COLOR );
                }
            }
            
            @Override
            public void mousePressed( PInputEvent event ) {
                isMousePressed = true;
                arrowNode.setPaint( ARROW_HILITE_COLOR );
            }
            
            @Override
            public void mouseReleased( PInputEvent event ) {
                isMousePressed = false;
                if ( !isMouseInside ) {
                    arrowNode.setPaint( ARROW_FILL_COLOR );
                }
            }
        } );
        
        // dashed line
        Line2D line = new Line2D.Double( 0, 0, LINE_LENGTH, 0 );
        PPath lineNode = new PPath( line );
        lineNode.setStroke( LINE_STROKE );
        lineNode.setStrokePaint( LINE_COLOR );
        
        // rendering order
        addChild( lineNode );
        addChild( arrowNode );
        
        // layout
        double x = 0;
        double y = -lineNode.getFullBoundsReference().getHeight() / 2;
        lineNode.setOffset( x, y );
        x = lineNode.getFullBoundsReference().getMaxX() + 2;
        y = lineNode.getYOffset();
        arrowNode.setOffset( x, y );
    }
}
