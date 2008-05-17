/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicHTML;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * ToolTipNode is a Swing-style "tool tip" that can be associated with a node.
 * When the mouse is placed over the associated node, the tool tip appears after a brief delay.
 * Pressing the mouse or moving the mouse off the associated node hides the tool tip.
 * Tool tip placement strategy can be specified.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolTipNode extends PComposite {

    private static final int DEFAULT_START_TIME = 2000; // how long after mouseEnter that the tool tip becomes visible (ms)
    private static final Font DEFAULT_FONT = new PhetFont( 12 );
    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );
    private static final Color DEFAULT_STROKE_COLOR = new Color( 0, 0, 0, 200 );
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color( 255, 255, 204 ); // light yellow
    private static final Color DEFAULT_BACKGROUND_SHADOW_COLOR = new Color( 0, 0, 0, 100 ); // transparent black
    private static final Color DEFAULT_FOREGROUND_COLOR = Color.BLACK;
    private static final double DEFAULT_MARGIN = 5;

    private PNode _associatedNode;
    private boolean _enabled;
    private TimerEventStrategy _eventStrategy;

    public ToolTipNode( final String toolTipText, final PNode associatedNode ) {
        super();
        
        setVisible( false );
        setPickable( false );
        setChildrenPickable( false );

        _associatedNode = associatedNode;
        _enabled = true;

        PNode toolTipTextNode = null;
        if ( BasicHTML.isHTMLString( toolTipText ) ) {
            HTMLNode htmlNode = new HTMLNode( toolTipText );
            htmlNode.setFont( DEFAULT_FONT );
            htmlNode.setHTMLColor( DEFAULT_FOREGROUND_COLOR );
            toolTipTextNode = htmlNode;
        }
        else {
            PText ptextNode = new PText( toolTipText );
            ptextNode.setFont( DEFAULT_FONT );
            ptextNode.setTextPaint( DEFAULT_FOREGROUND_COLOR );
            toolTipTextNode = ptextNode;
        }

        PBounds b = toolTipTextNode.getFullBoundsReference();
        final double w = b.getWidth() + ( 2 * DEFAULT_MARGIN );
        final double h = b.getHeight() + ( 2 * DEFAULT_MARGIN );
        PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        backgroundNode.setStroke( DEFAULT_STROKE );
        backgroundNode.setStrokePaint( DEFAULT_STROKE_COLOR );
        backgroundNode.setPaint( DEFAULT_BACKGROUND_COLOR );
        
        PPath backgroundShadowNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        backgroundShadowNode.setStroke( null );
        backgroundShadowNode.setPaint( DEFAULT_BACKGROUND_SHADOW_COLOR );
        backgroundShadowNode.setOffset( 2, 2 );

        addChild( backgroundShadowNode );
        addChild( backgroundNode );
        addChild( toolTipTextNode );
        toolTipTextNode.setOffset( DEFAULT_MARGIN, DEFAULT_MARGIN );

        _eventStrategy = new TimerEventStrategy( this );
        associatedNode.addInputEventListener( _eventStrategy );
    }

    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
    }

    public boolean isEnabled() {
        return _enabled;
    }
    
    public void setShowToolTipBelowNode( boolean b ) {
        _eventStrategy.setShowToolTipBelowNode( b );
    }
    
    public PNode getAssociatedNode() {
        return _associatedNode;
    }

    /*
     * When the mouse enters the associated node, the tool tip is shown after a delay.
     * When the mouse is pressed or when the mouse exists, the tool tip is hidden.
     */
    private static class TimerEventStrategy extends PBasicInputEventHandler {

        private ToolTipNode _toolTipNode;
        private Timer _showToolTipTimer;
        private boolean _showToolTipBelowNode; // true=tool tip appears below node, false=tool tip appears above mouse cursor

        public TimerEventStrategy( ToolTipNode toolTipNode ) {
            super();
            _toolTipNode = toolTipNode;
            _showToolTipBelowNode = false;
        }
        
        public void mouseEntered( final PInputEvent mouseEvent ) {
            if ( _toolTipNode.isEnabled() ) {
                
                ActionListener onListener = new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {

                        if ( _showToolTipBelowNode ) {
                            // center tooltip below node
                            PBounds bGlobal = _toolTipNode.getAssociatedNode().getGlobalFullBounds();
                            Point2D pLocal = _toolTipNode.getParent().globalToLocal( new Point2D.Double( bGlobal.getX(), bGlobal.getMaxY() ) );
                            double xOffset = pLocal.getX() + ( _toolTipNode.getAssociatedNode().getFullBoundsReference().getWidth() - _toolTipNode.getFullBoundsReference().getWidth() ) / 2;
                            double yOffset = pLocal.getY() + 5;
                            _toolTipNode.setOffset( xOffset, yOffset );
                        }
                        else {
                            // center tooltip above mouse cursor
                            Point2D pGlobal = mouseEvent.getPosition();
                            Point2D pLocal = _toolTipNode.getParent().globalToLocal( pGlobal );
                            double xOffset = pLocal.getX() - ( _toolTipNode.getFullBoundsReference().getWidth() / 2 );
                            double yOffset = pLocal.getY() - _toolTipNode.getFullBoundsReference().getHeight() - 5;
                            _toolTipNode.setOffset( xOffset, yOffset );
                        }

                        _toolTipNode.setVisible( true );
                    }
                };
                
                _showToolTipTimer = new Timer( DEFAULT_START_TIME, onListener );
                _showToolTipTimer.setInitialDelay( DEFAULT_START_TIME );
                _showToolTipTimer.setRepeats( false );
                _showToolTipTimer.start();
            }
        }
        
        public void mousePressed( PInputEvent event ) {
            hideToolTip();
        }

        public void mouseExited( PInputEvent event ) {
            hideToolTip();
        }
        
        private void hideToolTip() {
            _toolTipNode.setVisible( false );
            if ( _showToolTipTimer != null ) {
                _showToolTipTimer.stop();
                _showToolTipTimer = null;
            }
        }
        
        public void setShowToolTipBelowNode( boolean b ) {
            _showToolTipBelowNode = b;
        }
    }

    public static void main( String[] args ) {

        // Orange Square
        PPath orangeNode = new PPath( new Rectangle( 0, 0, 100, 100 ) );
        orangeNode.setPaint( Color.ORANGE );
        orangeNode.setOffset( 50, 50 );
        orangeNode.addInputEventListener( new CursorHandler() );
        
        // Tooltip that is centered above the mouse cursor
        ToolTipNode orangeToolTipNode = new ToolTipNode( "tool tip follows mouse", orangeNode );
        
        // Red Square
        PPath redNode = new PPath( new Rectangle( 0, 0, 50, 50 ) );
        redNode.setPaint( Color.RED );
        redNode.setOffset( 200, 50 );
        redNode.addInputEventListener( new CursorHandler() );

        // ToolTip that is wider than its associated node, centered below the node.
        ToolTipNode redToolTipNode = new ToolTipNode( "tool tip centered below red square", redNode );
        redToolTipNode.setShowToolTipBelowNode( true );

        // Green Square
        PPath greenNode = new PPath( new Rectangle( 0, 0, 100, 100 ) );
        greenNode.setPaint( Color.GREEN );
        greenNode.setOffset( 350, 50 );
        greenNode.addInputEventListener( new CursorHandler() );
        
        // ToolTip that is narrower than its associated node, centered below the node, HTML text.
        ToolTipNode greenToolTipNode = new ToolTipNode( "<html><center>centered<br>green<br>tool tip</center></html>", greenNode );
        greenToolTipNode.setShowToolTipBelowNode( true );
        
        // Canvas
        PCanvas canvas = new PCanvas();
        canvas.getLayer().addChild( orangeNode );
        canvas.getLayer().addChild( redNode );
        canvas.getLayer().addChild( greenNode );
        canvas.getLayer().addChild( orangeToolTipNode );
        canvas.getLayer().addChild( redToolTipNode );
        canvas.getLayer().addChild( greenToolTipNode );

        // Frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 600, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
