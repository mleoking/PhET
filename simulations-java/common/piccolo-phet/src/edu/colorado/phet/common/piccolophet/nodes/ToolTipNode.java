/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes;

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
 * <p>
 * Several tool tip location strategies are provided, and you can provide your own.
 * The default strategy centers the tool tip above the mouse cursor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ToolTipNode extends PComposite {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int DEFAULT_START_TIME = 1500; // how long after mouseEnter that the tool tip becomes visible (ms)
    private static final Font DEFAULT_FONT = new PhetFont( 12 );
    private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
    private static final Stroke DEFAULT_STROKE = new BasicStroke( 1f );
    private static final Paint DEFAULT_STROKE_PAINT = new Color( 0, 0, 0, 200 );
    private static final Paint DEFAULT_BACKGROUND_PAINT = new Color( 255, 255, 204 ); // light yellow
    private static final Paint DEFAULT_BACKGROUND_SHADOW_PAINT = new Color( 0, 0, 0, 100 ); // transparent black
    private static final double DEFAULT_MARGIN = 5;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final PNode _associatedNode;
    private boolean _enabled;
    private Timer _showToolTipTimer;
    private IToolTipLocationStrategy _locationStrategy;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor that provides a good default behavior.
     * 
     * @param toolTipText HTML or plain-text format
     * @param associatedNode the node that the tool tip describes
     */
    public ToolTipNode( final String toolTipText, final PNode associatedNode ) {
        this( toolTipText, associatedNode, DEFAULT_START_TIME, 
                DEFAULT_FONT, DEFAULT_TEXT_COLOR, 
                DEFAULT_STROKE, DEFAULT_STROKE_PAINT, 
                DEFAULT_BACKGROUND_PAINT, DEFAULT_BACKGROUND_SHADOW_PAINT,
                DEFAULT_MARGIN );
    }
    
    /*
     * Fully-parameterized constructor.
     * Subclass and use this constructor if you want something other than the default behavior.
     * 
     * @param toolTipText
     * @param associatedNode
     * @param startTime
     * @param font
     * @param textColor
     * @param backgroundStroke
     * @param backgroundStrokePaint
     * @param backgroundPaint
     * @param backgroundShadowPaint
     * @param margin
     */
    protected ToolTipNode( final String toolTipText, final PNode associatedNode, final int startTime,
            Font font, Color textColor,
            Stroke backgroundStroke, Paint backgroundStrokePaint,
            Paint backgroundPaint, Paint backgroundShadowPaint,
            double margin ) {
        super();
        
        setVisible( false );
        setPickable( false );
        setChildrenPickable( false );

        _associatedNode = associatedNode;
        _enabled = true;

        PNode toolTipTextNode = null;
        if ( BasicHTML.isHTMLString( toolTipText ) ) {
            HTMLNode htmlNode = new HTMLNode( toolTipText );
            htmlNode.setFont( font );
            htmlNode.setHTMLColor( textColor );
            toolTipTextNode = htmlNode;
        }
        else {
            PText ptextNode = new PText( toolTipText );
            ptextNode.setFont( font );
            ptextNode.setTextPaint( textColor );
            toolTipTextNode = ptextNode;
        }

        PBounds b = toolTipTextNode.getFullBoundsReference();
        final double w = b.getWidth() + ( 2 * margin );
        final double h = b.getHeight() + ( 2 * margin );
        PPath backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        backgroundNode.setStroke( backgroundStroke );
        backgroundNode.setStrokePaint( backgroundStrokePaint );
        backgroundNode.setPaint( backgroundPaint );
        
        PPath backgroundShadowNode = new PPath( new Rectangle2D.Double( 0, 0, w, h ) );
        backgroundShadowNode.setStroke( null );
        backgroundShadowNode.setPaint( backgroundShadowPaint );
        backgroundShadowNode.setOffset( 2, 2 );

        addChild( backgroundShadowNode );
        addChild( backgroundNode );
        addChild( toolTipTextNode );
        toolTipTextNode.setOffset( margin, margin );

        _locationStrategy = new CenterToolTipAboveMouseCursor();
        
        associatedNode.addInputEventListener( new PBasicInputEventHandler() {
            
            public void mouseEntered( final PInputEvent mouseEvent ) {
                if ( ToolTipNode.this.isEnabled() ) {
                    
                    ActionListener onListener = new ActionListener() {
                        public void actionPerformed( ActionEvent ae ) {
                            _locationStrategy.setToolTipLocation( ToolTipNode.this, _associatedNode, mouseEvent );
                            ToolTipNode.this.setVisible( true );
                        }
                    };
                    
                    _showToolTipTimer = new Timer( startTime, onListener );
                    _showToolTipTimer.setInitialDelay( startTime );
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
                ToolTipNode.this.setVisible( false );
                if ( _showToolTipTimer != null ) {
                    _showToolTipTimer.stop();
                    _showToolTipTimer = null;
                }
            }
        } );
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Enables or disables the tool tip. 
     * When disable, the tool tip will never become visible.
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
    }

    /**
     * Is the tool tip enabled?
     * @return
     */
    public boolean isEnabled() {
        return _enabled;
    }
    
    /**
     * Sets the strategy for placing the tool tip (setting its location).
     * @param locationStrategy
     */
    public void setLocationStrategy( IToolTipLocationStrategy locationStrategy ) {
        _locationStrategy = locationStrategy;
    }
    
    //----------------------------------------------------------------------------
    // Strategies for setting the tool tip's location
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all strategies that set a tool tip's location.
     */
    public interface IToolTipLocationStrategy {
        /**
         * Sets the tool tip's location.
         * @param toolTipNode the tool tip node
         * @param associatedNode the node that the tool tip describes
         * @param event the mouseEntered event that made the tool tip visible
         */
        public void setToolTipLocation( ToolTipNode toolTipNode, PNode associatedNode, PInputEvent event );
    }
    
    /**
     * Tool tip is centered about the mouse cursor.
     */
    public static class CenterToolTipAboveMouseCursor implements IToolTipLocationStrategy {
        public void setToolTipLocation( ToolTipNode toolTipNode, PNode associatedNode, PInputEvent event ) {
            Point2D pGlobal = event.getPosition();
            Point2D pLocal = toolTipNode.getParent().globalToLocal( pGlobal );
            double xOffset = pLocal.getX() - ( toolTipNode.getFullBoundsReference().getWidth() / 2 );
            double yOffset = pLocal.getY() - toolTipNode.getFullBoundsReference().getHeight() - 5;
            toolTipNode.setOffset( xOffset, yOffset );
        }
    }
    
    /**
     * Tool tip is left-aligned about the mouse cursor.
     */
    public static class LeftAlignToolTipAboveMouseCursor implements IToolTipLocationStrategy {
        public void setToolTipLocation( ToolTipNode toolTipNode, PNode associatedNode, PInputEvent event ) {
            Point2D pGlobal = event.getPosition();
            Point2D pLocal = toolTipNode.getParent().globalToLocal( pGlobal );
            double xOffset = pLocal.getX();
            double yOffset = pLocal.getY() - toolTipNode.getFullBoundsReference().getHeight() - 5;
            toolTipNode.setOffset( xOffset, yOffset );
        }
    }
    
    /**
     * Tool tip is centered below its associated node.
     */
    public static class CenterToolTipUnderAssociatedNode implements IToolTipLocationStrategy {
        public void setToolTipLocation( ToolTipNode toolTipNode, PNode associatedNode, PInputEvent event ) {
            PBounds bGlobal = associatedNode.getGlobalFullBounds();
            Point2D pLocal = toolTipNode.getParent().globalToLocal( new Point2D.Double( bGlobal.getX(), bGlobal.getMaxY() ) );
            double xOffset = pLocal.getX() + ( associatedNode.getFullBoundsReference().getWidth() - toolTipNode.getFullBoundsReference().getWidth() ) / 2;
            double yOffset = pLocal.getY() + 5;
            toolTipNode.setOffset( xOffset, yOffset );
        }
    }

    //----------------------------------------------------------------------------
    // Testing
    //----------------------------------------------------------------------------
    
    public static void main( String[] args ) {
        
        // Instructions
        PText instructionsNode = new PText( "Place mouse over a square to see its tool tip." );
        instructionsNode.setFont( new PhetFont( 14 ) );

        // Orange Square
        PPath orangeNode = new PPath( new Rectangle( 0, 0, 50, 50 ) );
        orangeNode.setPaint( Color.ORANGE );
        orangeNode.addInputEventListener( new CursorHandler() );
        ToolTipNode orangeToolTipNode = new ToolTipNode( "left-aligned above mouse cursor", orangeNode );
        orangeToolTipNode.setLocationStrategy( new LeftAlignToolTipAboveMouseCursor() );
        
        // Blue Square
        PPath blueNode = new PPath( new Rectangle( 0, 0, 50, 50 ) );
        blueNode.setPaint( Color.BLUE );
        blueNode.addInputEventListener( new CursorHandler() );
        ToolTipNode blueToolTipNode = new ToolTipNode( "centered above mouse cursor", blueNode );
        
        // Red Square
        PPath redNode = new PPath( new Rectangle( 0, 0, 50, 50 ) );
        redNode.setPaint( Color.RED );
        redNode.addInputEventListener( new CursorHandler() );
        ToolTipNode redToolTipNode = new ToolTipNode( "centered below node", redNode );
        redToolTipNode.setLocationStrategy( new CenterToolTipUnderAssociatedNode() );

        // Green Square
        PPath greenNode = new PPath( new Rectangle( 0, 0, 100, 50 ) );
        greenNode.setPaint( Color.GREEN );
        greenNode.addInputEventListener( new CursorHandler() );
        ToolTipNode greenToolTipNode = new ToolTipNode( "<html><center>HTML<br><b>centered</b><br>below<br>node</center></html>", greenNode );
        greenToolTipNode.setLocationStrategy( new CenterToolTipUnderAssociatedNode() );
        
        // Layout
        final int margin = 50;
        final int spacing = 50;
        instructionsNode.setOffset( margin, margin );
        orangeNode.setOffset( margin, instructionsNode.getFullBoundsReference().getMaxY() + spacing );
        blueNode.setOffset( orangeNode.getFullBoundsReference().getMaxX() + spacing, orangeNode.getFullBoundsReference().getY() );
        redNode.setOffset( blueNode.getFullBoundsReference().getMaxX() + spacing, blueNode.getFullBoundsReference().getY() );
        greenNode.setOffset( redNode.getFullBoundsReference().getMaxX() + spacing, redNode.getFullBoundsReference().getY() );
        final double frameWidth = greenNode.getFullBoundsReference().getMaxX() + margin;
        
        // Canvas
        PCanvas canvas = new PCanvas();
        canvas.getLayer().addChild( instructionsNode );
        canvas.getLayer().addChild( orangeNode );
        canvas.getLayer().addChild( blueNode );
        canvas.getLayer().addChild( redNode );
        canvas.getLayer().addChild( greenNode );
        canvas.getLayer().addChild( orangeToolTipNode );
        canvas.getLayer().addChild( redToolTipNode );
        canvas.getLayer().addChild( greenToolTipNode );
        canvas.getLayer().addChild( blueToolTipNode );

        // Frame
        JFrame frame = new JFrame( "ToolTipNode test" );
        frame.setContentPane( canvas );
        frame.setSize( (int)frameWidth, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
