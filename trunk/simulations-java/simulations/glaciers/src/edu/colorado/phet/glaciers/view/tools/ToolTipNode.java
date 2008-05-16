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

        associatedNode.addInputEventListener( new TimerEventStrategy( this ) );
    }

    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
    }

    public boolean isEnabled() {
        return _enabled;
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

        public TimerEventStrategy( ToolTipNode toolTipNode ) {
            super();
            _toolTipNode = toolTipNode;
        }
        
        public void mouseEntered( PInputEvent mouseEvent ) {
            if ( _toolTipNode.isEnabled() ) {
                
                ActionListener onListener = new ActionListener() {
                    public void actionPerformed( ActionEvent ae ) {
                        //XXX something wrong in here, tooltip alternates between 2 different positions for a stationary associatedNode
                        //TODO center the tooltip below its associatedNode
                        PBounds bGlobal = _toolTipNode.getAssociatedNode().getGlobalFullBounds();
                        System.out.println( "associate node global bounds = " + bGlobal );//XXX
                        Point2D pLocal = _toolTipNode.globalToLocal( new Point2D.Double( bGlobal.getX(), bGlobal.getY() + bGlobal.getHeight() + 5 ) );
                        _toolTipNode.setOffset( pLocal.getX(), pLocal.getY() );
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
    }

    public static void main( String[] args ) {

        // Square
        PPath squareNode = new PPath( new Rectangle( 0, 0, 75, 75 ) );
        squareNode.setPaint( Color.RED );
        squareNode.setOffset( 50, 50 );
        squareNode.addInputEventListener( new CursorHandler() );

        // ToolTip
        ToolTipNode toolTipNode = new ToolTipNode( "this is a red square", squareNode );

        // Canvas
        PCanvas canvas = new PCanvas();
        canvas.getLayer().addChild( squareNode );
        canvas.getLayer().addChild( toolTipNode );

        // Frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 200, 200 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
