/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler.ButtonEventAdapter;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.glaciers.model.Viewport;

/**
 * ScrollArrowNode is the base class for arrow controls that are
 * used to horizontally scroll the zoomed viewport.
 * <p>
 * LeftScrollArrowNode is for scrolling to the left.
 * RightScrollArrowNode is for scrolling the the right.
 * Scrolling is constrained to the bounds of the birds-eye viewport.
 * <p>
 * The origin of each node is at the tip of the arrow.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ScrollArrowNode extends PhetPNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // offsets to move the arrow when it is pressed
    private static final int X_OFFSET = 2;
    private static final int Y_OFFSET = 2;
    
    // arrow "look"
    private static final double HEAD_HEIGHT = 25;
    private static final double HEAD_WIDTH = 50;
    private static final double TAIL_WIDTH = 25;
    private static final double TAIL_LENGTH = 10;
    private static final Point2D TIP_POINT = new Point2D.Double( 0, 0 ); // origin at arrow tip
    private static final Point2D TAIL_POINT = new Point2D.Double( TIP_POINT.getX() - ( HEAD_HEIGHT + TAIL_LENGTH ), TIP_POINT.getY() );
    private static final Color FILL_COLOR = Color.WHITE;
    private static final Color STROKE_COLOR = Color.BLACK;
    private static final Stroke STROKE = new BasicStroke( 1.5f );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _listeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ScrollArrowNode( double rotation ) {
        
        rotate( rotation );
        
        ArrowNode arrowNode = new ArrowNode( TAIL_POINT, TIP_POINT, HEAD_HEIGHT, HEAD_WIDTH, TAIL_WIDTH );
        arrowNode.setPaint( FILL_COLOR );
        arrowNode.setStroke( STROKE );
        arrowNode.setStrokePaint( STROKE_COLOR );
        addChild( arrowNode );
        
        _listeners = new ArrayList();
        
        addInputEventListener( new CursorHandler() );
        
        ButtonEventHandler buttonHandler = new ButtonEventHandler();
        addInputEventListener( buttonHandler );
        buttonHandler.addButtonEventListener( new ButtonEventAdapter() {
            
            private boolean _armed;
            
            public void setArmed( boolean armed ) {
                if ( armed != _armed ) {
                    _armed = armed;
                    if ( armed ) {
                        setOffset( getXOffset() + X_OFFSET, getYOffset() + Y_OFFSET );
                    }
                    else {
                        setOffset( getXOffset() - X_OFFSET, getYOffset() - Y_OFFSET );
                    }
                }
            }
            
            public void fire() {
                ActionEvent event = new ActionEvent( ScrollArrowNode.this, 0, "BUTTON_FIRED" );
                for ( int i = 0; i < _listeners.size(); i++ ) {
                    ( (ActionListener) _listeners.get( i ) ).actionPerformed( event );
                }
            }
        } );
    }
    
    protected void addActionListener( ActionListener listener ) {
        if (!_listeners.contains( listener )){
            _listeners.add( listener );
        }
    }

    protected void removeActionListener( ActionListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    public static class LeftScrollArrowNode extends ScrollArrowNode {
        public LeftScrollArrowNode( final Viewport birdsEyeViewport, final Viewport zoomedViewport ) {
            super( Math.PI );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    double x = zoomedViewport.getX() - zoomedViewport.getWidth() / 2;
                    double newX = Math.max( x, birdsEyeViewport.getX() ); // constrain to birds-eye viewport
                    zoomedViewport.setPosition( newX, zoomedViewport.getY() );
                }
            } );
        }
    }

    public static class RightScrollArrowNode extends ScrollArrowNode {
        public RightScrollArrowNode( final Viewport birdsEyeViewport, final Viewport zoomedViewport, final double maxX ) {
            super( 0 );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    double x = zoomedViewport.getX() + zoomedViewport.getWidth() / 2;
                    double newX = Math.min( x, maxX - zoomedViewport.getWidth() ); // constrain to birds-eye viewport
                    zoomedViewport.setPosition( newX, zoomedViewport.getY() );
                }
            } );
        }
    }
}
